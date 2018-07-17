package com.tsuresh.ideamart;

public class IdeamartClient {

    private String applicationID;
    private String password;

    public IdeamartClient(String applicationID, String password) {
        this.applicationID = applicationID;
        this.password = password;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public String getPassword() {
        return password;
    }

}