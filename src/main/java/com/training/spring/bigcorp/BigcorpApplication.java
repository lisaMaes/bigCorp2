package com.training.spring.bigcorp;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;



@SpringBootApplication
@EnableConfigurationProperties
public class BigcorpApplication {

	private final static Logger logger = LoggerFactory.getLogger(SiteService.class);

	public static void main(String[] args) {

		SpringApplication.run(BigcorpApplication.class,args);
	}

}
