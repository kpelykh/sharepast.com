package kp.app.dal.domain;


import org.hibernate.annotations.AccessType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AccessType("field")
public class GPSCoordinates implements Serializable {

    @Column
    private double latitude;

    @Column
    private double longitude;

    public GPSCoordinates() {
    }

    public GPSCoordinates(double latitude, double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public String toString() {
        return String.format("[%f:%f]", latitude, longitude);
    }

}