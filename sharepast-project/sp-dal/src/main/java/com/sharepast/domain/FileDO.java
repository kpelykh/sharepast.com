package com.sharepast.domain;

import com.sharepast.commons.util.UTC;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

@Entity
@org.hibernate.annotations.AccessType("field")
@Table(name = "file")
public class FileDO extends IEntity<Integer> {

    @Column(name = "name", nullable = false, unique = false, length = 255)
    private String name;

    @Column(name = "location", nullable = true, unique = false, length = 512)
    private String location;

    @Column
    private long size;

    @Length(max = 100)
    @Column(name = "mime_type", unique = false, length = 100)
    private String mimeType;

    @Length(max = 100)
    @Column(name = "title", unique = false, length = 100)
    private String title;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated = UTC.now();

    public synchronized String getMimeType() {

        return mimeType;
    }

    public synchronized void setMimeType(String mimeType) {

        this.mimeType = mimeType;
    }

    public synchronized long getSize() {
        return size;
    }

    public synchronized void setSize(long size) {
        this.size = size;
    }

    public synchronized String getLocation() {
        return location;
    }

    public synchronized void setLocation(String location) {
        this.location = location;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized String getTitle() {
        return title;
    }

    public synchronized void setTitle(String title) {
        this.title = title;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
