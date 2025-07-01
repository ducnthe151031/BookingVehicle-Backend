package com.example.bookingvehiclebackend.demo;

public class chatReponseDemo {
    private String response;
    private String actionType;

    public chatReponseDemo() {}

    public chatReponseDemo(String response, String actionType) {
        this.response = response;
        this.actionType = actionType;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
