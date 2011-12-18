package com.sharepast.dal.domain.user;

import com.sharepast.dal.domain.AppTimeZone;
import org.hibernate.annotations.AccessType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
@AccessType("field")
public class Location implements Serializable {

    @Column(length = 3)
    private String country;

    @Column(length = 3)
    private String language;

    @Column(name = "postal_code", length = 25)
    private String postalCode;

    @Column(name="time_zone")
    @Enumerated(EnumType.STRING)
    private AppTimeZone timeZone;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private float altitude;

    @Column
    private String line1;

    @Column
    private String line2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String province;

    @Column(name = "neighborhood_name", length = 100)
    private String neighborhoodName;

    @Column(name = "dma_id")
    private Integer dmaId = 0;

    public Location() {
    }

    public Location(String country, String language, String postalCode, AppTimeZone timeZone, double latitude, double longitude, float altitude) {

        this.country = country;
        this.language = language;
        this.postalCode = postalCode;
        this.timeZone = timeZone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public synchronized String getCountry() {

        return country;
    }

    public synchronized void setCountry(String country) {

        this.country = country;
    }

    public synchronized String getLanguage() {

        return language;
    }

    public synchronized void setLanguage(String language) {

        this.language = language;
    }

    public synchronized String getPostalCode() {

        return postalCode;
    }

    public synchronized void setPostalCode(String postalCode) {

        this.postalCode = postalCode;
    }

    public synchronized AppTimeZone getTimeZone() {

        return timeZone;
    }

    public synchronized void setTimeZone(AppTimeZone timeZone) {

        this.timeZone = timeZone;
    }


    public synchronized double getLatitude() {

        return latitude;
    }


    public synchronized void setLatitude(double latitude) {

        this.latitude = latitude;
    }


    public synchronized double getLongitude() {

        return longitude;
    }


    public synchronized void setLongitude(double longitude) {

        this.longitude = longitude;
    }


    public synchronized float getAltitude() {

        return altitude;
    }


    public synchronized void setAltitude(float altitude) {

        this.altitude = altitude;
    }


    public synchronized String getNeighborhoodName() {
        return neighborhoodName;
    }


    public synchronized void setNeighborhoodName(String neighborhoodName) {
        this.neighborhoodName = neighborhoodName;
    }


    public synchronized String getProvince() {
        return province;
    }


    public synchronized void setProvince(String province) {
        this.province = province;
    }


    public synchronized String getState() {
        return state;
    }


    public synchronized void setState(String state) {
        this.state = state;
    }


    public synchronized String getCity() {
        return city;
    }


    public synchronized void setCity(String city) {
        this.city = city;
    }


    public synchronized String getLine2() {
        return line2;
    }


    public synchronized void setLine2(String line2) {
        this.line2 = line2;
    }


    public synchronized String getLine1() {
        return line1;
    }


    public synchronized void setLine1(String line1) {
        this.line1 = line1;
    }


    public synchronized Integer getDmaId() {
        if (dmaId == null)
            return 0;
        return dmaId;
    }


    public synchronized void setDmaId(Integer dmaId) {
        if (dmaId == null)
            dmaId = 0;
        this.dmaId = dmaId;
    }


}
