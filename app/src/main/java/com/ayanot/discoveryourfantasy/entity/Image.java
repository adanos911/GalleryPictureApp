package com.ayanot.discoveryourfantasy.entity;

import java.io.Serializable;

public class Image implements Serializable {
    private String name;
//    private int resourceId;

    public Image(String name) {
        this.name = name;
//        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
//    public int getResourceId() {
//        return resourceId;
//    }
//    public void setResourceId(int resourceId) {
//        this.resourceId = resourceId;
//    }
}
