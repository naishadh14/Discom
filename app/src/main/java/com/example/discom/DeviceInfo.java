package com.example.discom;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
    String name;
    String address;

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDuplicate(DeviceInfo device) {
        return (this.name.equals(device.name)) && (this.address.equals(device.address));
    }
}
