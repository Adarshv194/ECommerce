package com.ShopOnline.Buy.online.exceptions;

import java.util.Date;
import java.util.Map;

public class ValidationResponse {
    private Date timeStamp;
    private Map<String, String> errors;
    private String details;
    private int status;

    public ValidationResponse(Date timeStamp, Map<String, String> errors, String details, int status) {
        this.timeStamp = timeStamp;
        this.errors = errors;
        this.details = details;
        this.status = status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
