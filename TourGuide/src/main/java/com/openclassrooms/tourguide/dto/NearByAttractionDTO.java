package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Location;

public class NearByAttractionDTO {
    private String attractionName;
    private Location attractionLocation;
    private Location userLocation;
    private double distanceInMiles;
    private int rewardPoints;

    public NearByAttractionDTO(String attractionName, Location attractionLocation, Location userLocation, double distanceInMiles, int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLocation = attractionLocation;
        this.userLocation = userLocation;
        this.distanceInMiles = distanceInMiles;
        this.rewardPoints = rewardPoints;
    }
    public String getAttractionName() {
        return attractionName;
    }
    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }
    public Location getAttractionLocation() {
        return attractionLocation;
    }
    public void setAttractionLocation(Location attractionLocation) {
        this.attractionLocation = attractionLocation;
    }
    public Location getUserLocation() {
        return userLocation;
    }
    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }
    public double getDistanceInMiles() {
        return distanceInMiles;
    }
    public void setDistanceInMiles(double distanceInMiles) {
        this.distanceInMiles = distanceInMiles;
    }
    public int getRewardPoints() {
        return rewardPoints;
    }
    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

}
