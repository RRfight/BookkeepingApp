package com.exmaple.recordlife.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by RR on 2019/11/22.
 */

public class RecordType extends DataSupport{
    private int id;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
