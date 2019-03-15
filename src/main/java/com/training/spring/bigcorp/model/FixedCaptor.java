package com.training.spring.bigcorp.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("FIXED")
public class FixedCaptor extends Captor {


    @NotNull
    private Integer DefaultPowerInWatt;

    @Deprecated
    public FixedCaptor() {
        super();
    // used only by serializer and deserializer
    }

    public FixedCaptor(String name, Site site, Integer defaultPowerInWatt) {
        super(name, site, PowerSource.FIXED);
        this.DefaultPowerInWatt = defaultPowerInWatt;

    }



    public Integer getDefaultPowerInWatt() {
        return DefaultPowerInWatt;
    }

    public void setDefaultPowerInWatt(Integer defaultPowerInWatt) {
        DefaultPowerInWatt = defaultPowerInWatt;
    }
}
