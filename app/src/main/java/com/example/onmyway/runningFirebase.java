package com.example.onmyway;

public class runningFirebase {
    private String name;
    private String number;
    private String message;
    private String distance;
    private String address;

    public runningFirebase(){}

    public runningFirebase(String name,String number,String address, String message, String distance){
        this.name=name;
        this.number=number;
        this.address=address;
        this.message=message;
        this.distance=distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String savedName) {
        this.name = savedName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
