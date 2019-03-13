package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.config.Monitored;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.service.measure.MeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CaptorServiceImpl implements CaptorService{


    private MeasureService fixedMeasureService;
    private MeasureService simulatedMeasureService;
    private MeasureService realMeasureService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CaptorDao captorDao;


    public CaptorServiceImpl(){

    }

    public CaptorServiceImpl(MeasureService fixedMeasureService, MeasureService simulatedMeasureService, MeasureService realMeasureService) {
        this.fixedMeasureService = fixedMeasureService;
        this.simulatedMeasureService = simulatedMeasureService;
        this.realMeasureService = realMeasureService;
    }

    @Override
    @Monitored
    public Set<Captor> findBySite(String siteId) {



        Set<Captor> captors = captorDao.findBySiteId(siteId).stream().collect(Collectors.toSet());

        return captors;
    }



}
