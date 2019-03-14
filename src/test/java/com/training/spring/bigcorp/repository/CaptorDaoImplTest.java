package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class CaptorDaoImplTest {
    @Autowired
    private CaptorDao captorDao;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void findById() {
        Optional<Captor> captor = captorDao.findById("c1");
        Assertions.assertThat(captor)
                                .get()
                                .extracting("name")
                                .containsExactly("Eolienne");
    }
    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Captor>  captor = captorDao.findById("unknown");
        Assertions.assertThat(captor).isEmpty();
    }
    @Test
    public void findAll() {
        List<Captor> captors = captorDao.findAll();
        Assertions.assertThat(captors)
                .hasSize(2)
                .extracting("id", "name")
                .contains(Tuple.tuple("c1", "Eolienne"))
                .contains(Tuple.tuple("c2", "Laminoire à chaud"));
    }
    @Test
    public void create() {
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        captorDao.save(new RealCaptor("New captor",new Site("site1", "Bigcorp Lyon")));
        Assertions.assertThat(captorDao.findAll())
                    .hasSize(3)
                    .extracting(Captor::getName)
                    .contains("Eolienne", "Laminoire à chaud", "New captor");
     }
    @Test
    public void update() {
        Optional<Captor>  captor = captorDao.findById("c1");
        Assertions.assertThat(captor).get().extracting("name").containsExactly("Eolienne");
        captor.ifPresent(c->{
                                c.setName("Captor updated");
                                captorDao.save(c);
                        });
        captor = captorDao.findById("c1");
        Assertions.assertThat(captor).get().extracting("name").containsExactly("Captor updated");
    }
    @Test
    public void delete() {
        Captor  newcaptor = new RealCaptor("New captor", new Site("site1", "Bigcorp Lyon"));
        captorDao.save(newcaptor);
        Assertions.assertThat(captorDao.findById(newcaptor.getId())).isNotEmpty();
        captorDao.delete(newcaptor);
        Assertions.assertThat(captorDao.findById(newcaptor.getId())).isEmpty();
    }
    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsUsedAsForeignKey() {
        Captor captor = captorDao.getOne("c1");
        Assertions.assertThatThrownBy(() -> {
                                            captorDao.delete(captor);
                                            entityManager.flush();
                                            })

                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void findBySiteIdTest(){
        List<Captor> captors = captorDao.findBySiteId("site1");
        Assertions.assertThat(captors)
                .extracting("id", "name")
                .contains(Tuple.tuple("c1", "Eolienne"))
                .contains(Tuple.tuple("c2", "Laminoire à chaud"));
    }

    @Test
    public void findByExample() {
        ExampleMatcher matcher = ExampleMatcher.matching()
                                            .withMatcher("name", match-> match.ignoreCase().contains())
                                            .withIgnoreNullValues()
                                            .withIgnorePaths("id");
        Site site = new Site();
        site.setId("site1");
        Captor captor = new FixedCaptor("lienn", site);


        List<Captor> captors = captorDao.findAll(Example.of(captor, matcher));
        Assertions.assertThat(captors)
                .hasSize(1)
                .extracting("id", "name")
                .containsExactly(Tuple.tuple("c1", "Eolienne"));
    }

    @Test
    public void preventConcurrentWrite() {
        Captor captor = captorDao.getOne("c1");

        // A la base le numéro de version est à sa valeur initiale
        Assertions.assertThat(captor.getVersion()).isEqualTo(0);

        // On detache cet objet du contexte de persistence
        entityManager.detach(captor);
        captor.setName("Captor updated");

        // On force la mise à jour en base (via le flush) et on vérifie que l'obje retourné
        // et attaché à la session a été mis à jour
        Captor attachedCaptor = captorDao.save(captor);
        entityManager.flush();

        Assertions.assertThat(attachedCaptor.getName()).isEqualTo("Captor updated");
        Assertions.assertThat(attachedCaptor.getVersion()).isEqualTo(1);

        // Si maintenant je réessaie d'enregistrer captor, comme le numéro de version est
        // à 0 je dois avoir une exception
        Assertions.assertThatThrownBy(() -> captorDao.save(captor))
                .isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    public void createShouldThrowExceptionWhenNameIsNull() {
        Site site = new Site("site1", "Bigcorp Lyon");

        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.save(new RealCaptor(null, site));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("ne peut pas être nul");
    }

    @Test
    public void createShouldThrowExceptionWhenNameSizeIsInvalid() {
        Site site = new Site("site1", "Bigcorp Lyon");

        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.save(new RealCaptor("ee", site));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("la taille doit être comprise entre 3 et 300");
    }

    @Test
    public void createSimulatedCaptorShouldThrowExceptionWhenMinMaxAreInvalid() {

        Site site = new Site("site1", "Bigcorp Lyon");

        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.save(new SimulatedCaptor("Mon site", site, 10, 5));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("minPowerInWatt should be less than maxPowerInWatt");
    }
}
