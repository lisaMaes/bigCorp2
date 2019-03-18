package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Captor;
import org.assertj.core.api.Assertions;
import org.junit.Test;


import java.util.Set;


public class CaptorServiceImplTest {
    private CaptorServiceImpl captorService = new CaptorServiceImpl();

    @Test
    public void findBySiteShouldReturnNullWhenIdIsNull() {
        // Initialisation
        String siteId = null;

        // Appel du SUT
        Set<Captor> captors = captorService.findBySite(siteId);

        // Vérification
        Assertions.assertThat(captors).isEmpty();
    }

    @Test
    public void findBySite() {
        // Initialisation
        String siteId = "site1";

        // Appel du SUT
        Set<Captor> captors = captorService.findBySite(siteId);

        // Vérification
        Assertions.assertThat(captors).hasSize(2);
        //Assertions.assertThat(captors).extracting(Captor::getName).contains("Capteur A");
    }
}