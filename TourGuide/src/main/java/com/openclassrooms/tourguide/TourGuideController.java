package com.openclassrooms.tourguide;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

import com.openclassrooms.tourguide.dto.NearByAttractionDTO;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	return tourGuideService.getUserLocation(getUser(userName));
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public List<NearByAttractionDTO> getNearbyAttractions(@RequestParam String userName) {
        User user = getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        RewardCentral rewardCentral = new RewardCentral();
        List<NearByAttractionDTO> nearByAttractionDTOs = tourGuideService.getNearByAttractions(visitedLocation)
                .stream()
                .map(attraction -> new NearByAttractionDTO(
                    attraction.attractionName,
                    new Location(attraction.latitude, attraction.longitude),
                    visitedLocation.location,
                    RewardsService.getDistance(attraction, visitedLocation.location),
                    rewardCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId()
                )))
                .toList();
        return nearByAttractionDTOs;
    }
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}