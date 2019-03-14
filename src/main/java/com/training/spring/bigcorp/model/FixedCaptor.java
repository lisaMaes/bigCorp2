package com.training.spring.bigcorp.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("FIXED")
public class FixedCaptor extends Captor {

    @Column
    private Integer DefaultPowerInWatt;

    @Deprecated
    public FixedCaptor() {
        super();
    // used only by serializer and deserializer
    }

    public FixedCaptor(String name, Site site) {
        super(name, site);
    }

    public Integer getDefaultPowerInWatt() {
        return DefaultPowerInWatt;
    }

    public void setDefaultPowerInWatt(Integer defaultPowerInWatt) {
        DefaultPowerInWatt = defaultPowerInWatt;
    }
}
