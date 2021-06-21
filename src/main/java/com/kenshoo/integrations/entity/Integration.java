package com.kenshoo.integrations.entity;

public class Integration {
    public int getId() {
        return id;
    }

    public String getKsId() {
        return ksId;
    }

    public String getData() {
        return data;
    }

    private int id;
    private String ksId;
    private String data;

    public Integration(int id, String ksId, String data) {
        this.id = id;
        this.ksId = ksId;
        this.data = data;
    }
}
