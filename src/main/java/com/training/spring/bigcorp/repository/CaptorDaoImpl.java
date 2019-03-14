package com.training.spring.bigcorp.repository;


import com.training.spring.bigcorp.model.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@Repository
@Transactional
public class CaptorDaoImpl implements CaptorDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MeasureDao mesureDao;

    @Override
    public List<Captor> findBySiteId(String siteId) {

        return em.createQuery("select c from Captor c inner join c.site s where s.id = :siteId", Captor.class)
                .setParameter("siteId", siteId)
                .getResultList();

    }

    @Override
    public void persist(Captor captor) {

        em.persist(captor);
    }

    @Override
    public Captor findById(String id) {

        try{
            Captor captor= em.find(Captor.class, id);
            return captor;

        }catch (Exception e){

            return null;
        }
    }

    @Override
    public List<Captor> findAll() {

        return em.createQuery("select c from Captor c inner join c.site s", Captor.class)
                .getResultList();
    }



    @Override
    public void delete(Captor captor) {

        mesureDao.findByCaptorId(captor.getId()).forEach(m-> mesureDao.delete(m));

        em.remove(captor);
    }

}
