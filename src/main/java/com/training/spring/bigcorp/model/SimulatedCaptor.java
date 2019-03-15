package com.training.spring.bigcorp.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("SIMULATED")
public class SimulatedCaptor extends Captor{

    @NotNull
    private Integer minPowerInWatt;

    @NotNull
    private Integer maxPowerInWatt;


    public SimulatedCaptor() {
        super();
    }

    public SimulatedCaptor(String name, Site site) {
        super(name, site, PowerSource.SIMULATED);

    }

    public SimulatedCaptor(String name, Site site, @NotNull Integer minPowerInWatt, @NotNull Integer maxPowerInWatt) {
        super(name, site, PowerSource.SIMULATED);

        this.minPowerInWatt = minPowerInWatt;
        this.maxPowerInWatt = maxPowerInWatt;
    }

    public Integer getMinPowerInWatt() {
        return minPowerInWatt;
    }

    public void setMinPowerInWatt(Integer minPowerinWatt) {
        minPowerInWatt = minPowerinWatt;
    }

    public Integer getMaxPowerInWatt() {
        return maxPowerInWatt;
    }

    public void setMaxPowerInWatt(Integer maxPowerInWatt) {
        this.maxPowerInWatt = maxPowerInWatt;
    }

    @AssertTrue(message = "minPowerInWatt should be less than maxPowerInWatt")
    public boolean isValid(){

        return this.minPowerInWatt <= this.maxPowerInWatt;
    }

}
