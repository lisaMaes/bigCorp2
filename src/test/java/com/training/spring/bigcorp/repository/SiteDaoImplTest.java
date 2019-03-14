package com.training.spring.bigcorp.repository;


import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
@Transactional
public class SiteDaoImplTest {

    @Autowired
    private SiteDao siteDao;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void findById() {
        Optional<Site> site = siteDao.findById("site1");
        Assertions.assertThat(site)
                .get()
                .extracting("name")
                .containsExactly("Bigcorp Lyon");
    }
    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Site> site = siteDao.findById("unknown");
        Assertions.assertThat(site).isEmpty();
    }
    @Test
    public void findAll() {
        List<Site> sites = siteDao.findAll();
        Assertions.assertThat(sites)
                .hasSize(1)
                .extracting("id", "name")
                .contains(Tuple.tuple("site1", "Bigcorp Lyon"));
    }
    @Test
    public void create() {
        Assertions.assertThat(siteDao.findAll()).hasSize(1);
        siteDao.save(new Site("New site"));
        Assertions.assertThat(siteDao.findAll())
                    .hasSize(2)
                    .extracting(Site::getName)
                    .contains("Bigcorp Lyon", "New site");
     }
    @Test
    public void update() {
        Optional<Site> site = siteDao.findById("site1");
        Assertions.assertThat(site).get().extracting("name").containsExactly("Bigcorp Lyon");
        site.ifPresent(s -> {
                            s.setName("Site updated");
                            siteDao.save(s);
                            });
        site = siteDao.findById("site1");
        Assertions.assertThat(site).get().extracting("name").containsExactly("Site updated");

    }
    @Test
    public void delete() {
        Site newsite = new Site("New site");
        siteDao.save(newsite);
        Assertions.assertThat(siteDao.findById(newsite.getId())).isNotEmpty();
        siteDao.delete(newsite);
        Assertions.assertThat(siteDao.findById(newsite.getId())).isEmpty();
    }

    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsUsedAsForeignKey() {
        Site site = siteDao.getOne("site1");
        Assertions
                .assertThatThrownBy(() -> {
                                            siteDao.delete(site);
                                            entityManager.flush();
                                            })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
        }

    @Test
    public void preventConcurrentWrite() {
        Site site = siteDao.getOne("site1");

        // A la base le numéro de version est à sa valeur initiale
        Assertions.assertThat(site.getVersion()).isEqualTo(0);

        // On detache cet objet du contexte de persistence
        entityManager.detach(site);
        site.setName("site updated");

        // On force la mise à jour en base (via le flush) et on vérifie que l'objet retourné
        // et attaché à la session a été mis à jour
        Site attachedSite = siteDao.save(site);
        entityManager.flush();

        Assertions.assertThat(attachedSite.getName()).isEqualTo("site updated");
        Assertions.assertThat(attachedSite.getVersion()).isEqualTo(1);

        // Si maintenant je réessaie d'enregistrer captor, comme le numéro de version est
        // à 0 je dois avoir une exception
        Assertions.assertThatThrownBy(() -> siteDao.save(site))
                .isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}

