package com.exmaple.recordlife.entity;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by RR on 2019/11/18.
 */

public class Journal extends DataSupport{
    private Integer id;
    private Date date;
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
