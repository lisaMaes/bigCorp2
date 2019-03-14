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

import java.time.Instant;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class MeasureDaoImplTest {

    @Autowired
    private MeasureDao measureDao;


    @Test
    public void findById() {
        Measure measure = measureDao.findById(-1L);
        Assertions.assertThat(measure.getId().equals(-1L));
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1_000_000);
        Assertions.assertThat(measure.getCaptor().getName()).isEqualTo("Eolienne");
        Assertions.assertThat(measure.getCaptor().getSite().getName()).isEqualTo("Bigcorp Lyon");

    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Measure measure = measureDao.findById(0L);
        Assertions.assertThat(measure).isNull();
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
        measureDao.persist(new Measure(Instant.now(), 2_333_666, captor));
        Assertions.assertThat(measureDao.findAll()).hasSize(11);
    }

    @Test
    public void update() {
        Measure measure = measureDao.findById(-1L);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1_000_000);
        measure.setValueInWatt(2_333_666);
        measureDao.persist(measure);
        measure = measureDao.findById(-1L);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(2_333_666);
    }

    @Test
    public void delete() {
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.delete(measureDao.findById(-1L));
        Assertions.assertThat(measureDao.findAll()).hasSize(9);
    }
}