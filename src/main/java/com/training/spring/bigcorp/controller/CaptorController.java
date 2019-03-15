package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.controller.dto.CaptorDto;
import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.training.spring.bigcorp.model.PowerSource.*;

@Transactional
@Controller
@RequestMapping(path="/sites/{siteId}/captors")
public class CaptorController {

    @Autowired
    private CaptorDao captorDao;
    @Autowired
    private SiteDao siteDao;
    @Autowired
    private MeasureDao measureDao;

    public CaptorController(CaptorDao captorDao, SiteDao siteDao) {
        this.captorDao = captorDao;
        this.siteDao = siteDao;
    }

    private CaptorDto toDto(Captor captor) {
        if (captor instanceof FixedCaptor) {
            return new CaptorDto(captor.getSite(), (FixedCaptor) captor);
        }
        if (captor instanceof SimulatedCaptor) {
            return new CaptorDto(captor.getSite(), (SimulatedCaptor) captor);
        }
        if (captor instanceof RealCaptor) {
            return new CaptorDto(captor.getSite(), (RealCaptor) captor);
        }
        throw new IllegalStateException("Captor type not managed by app");
    }

    private List<CaptorDto> toDtos(List<Captor> captors) {
        return captors.stream()
                .map(this::toDto)
                .sorted(Comparator.comparing(CaptorDto::getName))
                .collect(Collectors.toList());
    }

    @GetMapping
    public ModelAndView findAll(@PathVariable String siteId){
        return new ModelAndView("captors")
                .addObject("captors", toDtos(captorDao.findBySiteId(siteId)));
    }

    @GetMapping("/{id}")
    public ModelAndView findById(@PathVariable String siteId, @PathVariable String id){

        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);

        return new ModelAndView("captorSimulated")
                    .addObject("captor",
                            captorDao.findById(id).orElseThrow(IllegalArgumentException::new))
                    .addObject(site);
    }

    @GetMapping("/{powerSource}/create")
    public ModelAndView create(@PathVariable String siteId, @PathVariable PowerSource powerSource){

        ModelAndView modelAndView = null;

        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);

        Captor captor = CaptorDto.emptyCaptor(site, powerSource);

        Integer real =null, simulated = null, fixed = null;

        if (powerSource == REAL){
            real = 2;
            modelAndView =  new ModelAndView("captor").addObject("captor", captor)
                    .addObject("real", real)
                    .addObject("site",site);
        }else if(powerSource == SIMULATED){
            simulated = 2;
            modelAndView = new ModelAndView("captor").addObject("captor", captor)
                    .addObject("simulated",simulated)
                    .addObject("site",site);
        }else if (powerSource == FIXED){
            fixed = 2;
            modelAndView = new ModelAndView("captor").addObject("captor", captor)
                    .addObject("fixed", fixed)
                    .addObject("site",site);
        }
           return modelAndView;
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView save (@PathVariable String siteId, CaptorDto captorDto){

        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);

        Captor captor = captorDto.toCaptor(site);

   /*     if (captor.getId() == null) {
            captorToPersist = new SimulatedCaptor(captor.getName(), site,captor.getMinPowerInWatt(), captor.getMaxPowerInWatt());
        } else {
            captorToPersist = (SimulatedCaptor) captorDao.findById(captor.getId())
                    .orElseThrow(IllegalArgumentException::new);
            captorToPersist.setName(captor.getName());
            captorToPersist.setMaxPowerInWatt(captor.getMaxPowerInWatt());
            captorToPersist.setMinPowerInWatt(captor.getMinPowerInWatt());

        }*/
        captorDao.save(captor);
        return new ModelAndView("site").addObject("site", site);

    }

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable String siteId, @PathVariable String id) {

        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);

        measureDao.deleteByCaptorId(id);

        Captor captor = captorDao.findById(id).orElseThrow(IllegalArgumentException::new);



        captorDao.delete(captor);

        return new ModelAndView("site").addObject("site", site);
    }

}
