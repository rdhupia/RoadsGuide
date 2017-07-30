package rsdhupia.ca.roadsguide;

import android.util.Log;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by User on 16/11/2015.
 */
public class Restriction implements Serializable{

    private String id;
    private String roadAffected;
    private String workZone;
    private String district;
    private double latitude;
    private double longitude;
    private String roadClass;
    private int planned;
    private int severity;
    private String source;
    private long timestampLastUpdated;
    private long timestampStart;
    private long timestampEnd;
    private String workPeriod;
    private int expired;
    private String signing;
    private String notification;
    private String workEventType;
    private String contractor;
    private String permitType;
    private String description;
    private String impact;
    private String durationStart;
    private String durationEnd;

    public Restriction() {
        id = "";
        roadAffected = "";
        workZone = "";
        district = "";
        latitude = 0;
        longitude = 0;
        roadClass = "";
        planned = 0;
        severity = 0;
        timestampStart = 0;
        timestampEnd = 0;
        workPeriod = "";
        expired = 0;
        signing = "";
        notification = "";
        workEventType = "Other";
        contractor = "";
        permitType = "Other";
        description = "";
        impact = "";
        durationStart = "today";
        durationEnd = "today";

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoadAffected() {
        return roadAffected;
    }

    public void setRoadAffected(String roadAffected) {
        this.roadAffected = roadAffected;
    }

    public String getWorkZone() {
        return workZone;
    }

    public void setWorkZone(String workZone) {
        this.workZone = workZone;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getRoadClass() {
        return roadClass;
    }

    public void setRoadClass(String roadClass) {
        this.roadClass = roadClass;
    }

    public String getPlanned() {
        if(planned == 1)
            return "Planned";
        else
        return "an Emergency";
    }

    public void setPlanned(int planned) {
        this.planned = planned;
    }

    public String getSeverity() {
        if(severity == 0)
            return "Normal";
        else
            return "High";
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getTimestampStart() {

        return convertTime(timestampStart);
    }

    public long getTimeStampStartLong() {
        return timestampStart;
    }
    public long getTimeStampEndLong() {
        return timestampEnd;
    }

    public void setTimestampStart(long timestampStart) {

        this.timestampStart = timestampStart;

    }

    public String getTimestampEnd() {

        return convertTime(timestampEnd);
    }

    public void setTimestampEnd(long timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public String getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(String workPeriod) {
        this.workPeriod = workPeriod;
    }

    public int getExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }

    public String getSigning() {
        return signing;
    }

    public void setSigning(String signing) {
        this.signing = signing;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getWorkEventType() {
        return workEventType;
    }

    public void setWorkEventType(String workEventType) {
        this.workEventType = workEventType;
    }

    public String getContractor() {
        return contractor;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getPermitType() {
        return permitType;
    }

    public void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTimestampLastUpdated() {

        return convertTime(timestampLastUpdated);
    }

    public void setTimestampLastUpdated(long timestampLastUpdated) {
        this.timestampLastUpdated = timestampLastUpdated;
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd.MMM.yyyy 'at' h:mm a");
        return format.format(date);
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }
}

