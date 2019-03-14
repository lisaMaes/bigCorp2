package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class MeasureDaoImplTest {

    @Autowired
    private MeasureDao measureDao;
    @Autowired
    private EntityManager entityManager;


    @Test
    public void findById() {
        Optional<Measure> measure = measureDao.findById(-1L);
        Assertions.assertThat(measure).get()
                .extracting( "id").containsExactly(-1L);
        Assertions.assertThat(measure).get().extracting("valueInWatt").containsExactly(1_000_000);


    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Measure>  measure = measureDao.findById(0L);
        Assertions.assertThat(measure).isEmpty();
    }

    @Test
    public void findAll() {
        List<Measure> measures = measureDao.findAll();
        Assertions.assertThat(measures).hasSize(10);
    }

    @Test
    public void create() {

        Captor captor = new Captor("c1", "Eolienne", new Site("site"));;
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.save(new Measure(Instant.now(), 2_333_666, captor));
        Assertions.assertThat(measureDao.findAll()).hasSize(11);
    }

    @Test
    public void update() {
        Optional<Measure> measure = measureDao.findById(-1L);
        Assertions.assertThat(measure).get().extracting("valueInWatt").containsExactly(1_000_000);
        measure.ifPresent(m-> {

            m.setValueInWatt(2_333_666);
            measureDao.save(m);
        });
        measure = measureDao.findById(-1L);
        Assertions.assertThat(measure).get().extracting("valueInWatt").containsExactly(2_333_666);
    }

    @Test
    public void deleteById() {
        Captor captor = new Captor("c1", "Eolienne", new Site("site"));;

        Measure measure = measureDao.save(new Measure(Instant.now(), 2_333_666, captor));
        Assertions.assertThat(measureDao.findById(measure.getId())).isNotEmpty();
        measureDao.delete(measure);
        Assertions.assertThat(measureDao.findById(measure.getId())).isEmpty();
    }
}