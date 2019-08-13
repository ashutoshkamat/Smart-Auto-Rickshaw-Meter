package com.example.passengermodule.Models;

public class Passenger {

    String name, mobile_number;
    String pid;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", mobile_number='" + mobile_number + '\'' +
                '}';
    }
}
