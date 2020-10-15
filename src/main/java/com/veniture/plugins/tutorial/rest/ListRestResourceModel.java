package com.veniture.plugins.tutorial.rest;

import com.veniture.plugins.tutorial.dto.Currency;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListRestResourceModel {

    @XmlElement(name = "value")
    private Object message;

    public ListRestResourceModel(Object currency) {
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}