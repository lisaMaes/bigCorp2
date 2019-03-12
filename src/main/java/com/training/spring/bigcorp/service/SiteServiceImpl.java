package com.training.spring.bigcorp.service;


import com.training.spring.bigcorp.config.Monitored;
import com.training.spring.bigcorp.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Service
@Lazy
public class SiteServiceImpl implements SiteService {

    private final static Logger logger = LoggerFactory.getLogger(SiteService.class);

    private CaptorService captorService = new CaptorServiceImpl();


    @Autowired
    public SiteServiceImpl(CaptorService captorService){
        System.out.println("Init SiteServiceImpl :" + this);
        this.captorService = captorService;
    }

    @Autowired
    private ResourceLoader resourceLoader;


    public SiteServiceImpl(){

    }

    @Monitored
    @Override
    public Site findById(String siteId) {
        logger.debug("Appel de findById :" + this,  siteId);
        if (siteId == null) {
            return null;
        }

        Site site = new Site("Florange");
        site.setId(siteId);
        site.setCaptors(captorService.findBySite(siteId));
        return site;
    }

    @Override
    public void readFile(String path) {

        Resource resource = resourceLoader.getResource(path);

        try (InputStream stream = resource.getInputStream()) {
            Scanner scanner = new Scanner(stream).useDelimiter("\\n");
            while (scanner.hasNext()) {
            logger.debug("Appel readFile"+scanner.next(), path);
            }
        }
        catch (IOException e) {
            logger.error("Erreur sur chargement fichier", e);

        }
    }
}
