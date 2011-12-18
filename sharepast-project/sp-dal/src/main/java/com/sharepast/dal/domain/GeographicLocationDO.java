package com.sharepast.dal.domain;

import com.sharepast.persistence.Durable;
import com.sharepast.util.Util;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/*@Table(appliesTo="geographic_location",
     indexes = {
             @Index(name="idx_postal_code", columnNames={"postal_code"} ),
             @Index(name="idx_city", columnNames={"city"} ),
             @Index(name="idx_state", columnNames={"state"} ),
             @Index(name="idx_area_code", columnNames={"state"} )
     } )*/
@Entity
@org.hibernate.annotations.AccessType("field")
@Table(name = "geographic_location")
public class GeographicLocationDO extends Durable<Long> {

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(nullable = false, length = 255)
    private String city;

    @Column(nullable = false, length = 255)
    private String state;

    @Column(nullable = false, length = 255)
    private String county;

    @Column(name="time_zone")
    @Enumerated(EnumType.STRING)
    private AppTimeZone appTimeZone;

    @Column(name = "daylight_saving_change", nullable = false, length = 1)
    @Type(type="yes_no")
    private boolean dstZone;

    @Column(nullable = false, length = 1)
    @Type(type="yes_no")
    private boolean approved;

    @Column(name = "area_code", nullable = false, length = 50)
    private String areaCode;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="latitude", column = @Column(name="latitude") ),
            @AttributeOverride(name="longitude", column = @Column(name="longitude") )
    } )
    private GPSCoordinates coordinates;

    @Column(name = "dma_name", nullable = false, length = 50)
    private String dmaZoneName;

    @Column(name = "dma_id")
    private int dmaZoneId;

    @Column(name = "launch_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date locationLaunchDate;

    @Column(name = "target_customers_count")
    private Integer targetCustomerCount;

    public GeographicLocationDO() {
        coordinates = new GPSCoordinates();
    }

    public int getTargetCustomerCount() {
        return targetCustomerCount;
    }


    public void setTargetCustomerCount(Integer targetCustomerCount) {
        if (targetCustomerCount == null)
            targetCustomerCount = 0;
        this.targetCustomerCount = targetCustomerCount;
    }


    public Date getLocationLaunchDate() {
        return locationLaunchDate;
    }


    public void setLocationLaunchDate(Date locationLaunchDate) {
        this.locationLaunchDate = locationLaunchDate;
    }


    public int getDmaZoneId() {
        return dmaZoneId;
    }


    public void setDmaZoneId(int dmaZoneId) {
        this.dmaZoneId = dmaZoneId;
    }


    public GPSCoordinates getCoordinates() {
        return coordinates;
    }


    public String getDmaZoneName() {
        return dmaZoneName;
    }


    public void setDmaZoneName(String dmaZoneName) {
        this.dmaZoneName = dmaZoneName;
    }

    public void setCoordinates(GPSCoordinates coordinates) {
        this.coordinates = coordinates;
    }


    public String getAreaCode() {
        return areaCode;
    }


    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public boolean isDstZone() {
        return dstZone;
    }


    public void setDstZone(boolean dstZone) {
        this.dstZone = dstZone;
    }


    public AppTimeZone getTimeZone() {
        return appTimeZone;
    }


    public void setTimeZone(AppTimeZone appTimeZone) {
        this.appTimeZone = appTimeZone;
    }


    public String getCounty() {
        return county;
    }


    public void setCounty(String county) {
        this.county = county;
    }


    public String getState() {
        return state;
    }


    public void setState(String state) {
        this.state = state;
    }


    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }


    public String getPostalCode() {
        return postalCode;
    }


    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getSlugifiedUri() {
        return getSlugifiedUri(false);
    }

    public String getSlugifiedUri(boolean useAlternativeName) {
        StringBuffer sb = new StringBuffer();
        sb.append("/");

        sb.append(this.getDmaZoneName());

        sb.append("/");
        sb.append(this.getCity());

        return Util.slugify(sb.toString());
    }

}
