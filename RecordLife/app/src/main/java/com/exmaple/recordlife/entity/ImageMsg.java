package com.exmaple.recordlife.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by RR on 2019/11/20.
 */

public class ImageMsg extends DataSupport{
    private int id;
    private int journalId;
    private byte[] image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJournalId() {
        return journalId;
    }

    public void setJournalId(int journalId) {
        this.journalId = journalId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
