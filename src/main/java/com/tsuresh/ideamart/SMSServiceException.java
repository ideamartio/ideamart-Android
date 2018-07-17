package com.tsuresh.ideamart;

public class SMSServiceException extends Throwable {

    private String statusDetail;
    private String statusCode;

    public SMSServiceException(String statusDetail, String statusCode) {
        this.statusDetail = statusDetail;
        this.statusCode = statusCode;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public String getStatusCode() {
        return statusCode;
    }

}