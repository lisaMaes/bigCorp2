package com.training.spring.bigcorp.service.measure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.training.springcore.service.measure")
@PropertySource("classpath:application.properties")
public class MeasureServiceConfigurationTest {
}
