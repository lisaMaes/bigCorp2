package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Measure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class MeasureDaoImpl implements MeasureDao {


    private static String SELECT_WITH_JOIN = "select m from Measure m inner join m.captor c inner join c.site s";

    @PersistenceContext
    private EntityManager em;

    @Override
    public void persist(Measure measure) {

        em.persist(measure);
    }

    @Override
    public Measure findById(Long id) {

        try{
            Measure measure = em.find(Measure.class, id);

            return measure;

        }catch (Exception e){

            return null;
        }
    }

    @Override
    public List<Measure> findAll() {

        return em.createQuery(SELECT_WITH_JOIN, Measure.class)
                .getResultList();
    }


    @Override
    public void delete(Measure measure) {

        em.remove(measure);
    }

    @Override
    public List<Measure> findBySiteId(String siteId) {

        return em.createQuery(SELECT_WITH_JOIN+" where s.id = :siteId", Measure.class)
                .setParameter("siteId", siteId)
                .getResultList();
    }

    @Override
    public List<Measure> findByCaptorId(String captorId) {
        return em.createQuery(SELECT_WITH_JOIN+" where c.id = :captorId", Measure.class)
                .setParameter("captorId", captorId)
                .getResultList();
    }




}
