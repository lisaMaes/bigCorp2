package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class SiteDaoImpl  implements SiteDao{

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CaptorDao captorDao;

    @Override
    public void persist(Site site) {

        em.persist(site);

    }

    @Override
    public Site findById(String id) {

        try{

            Site site = em.find(Site.class, id);
            return site;

        }catch (Exception e){
            return null;
        }
    }

    @Override
    public List<Site> findAll() {
        return em.createQuery("select s from Site s ", Site.class)
                .getResultList();
    }


    @Override
    public void delete(Site site) {

        captorDao.findBySiteId(site.getId()).forEach(c-> captorDao.delete(c));

        em.remove(site);
    }
}
