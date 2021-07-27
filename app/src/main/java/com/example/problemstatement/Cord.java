package com.example.problemstatement;

import java.io.Serializable;

public class Cord implements Serializable {
    String cord;

    public Cord(String cord) {
        this.cord = cord;
    }

    public String getCord() {
        return cord;
    }

}
