package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
		this.attractions.addAll(gpsUtil.getAttractions());
	}

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	private final ExecutorService executorService = Executors.newFixedThreadPool(100);
	private final List<Attraction> attractions = new ArrayList<>();

	public CompletableFuture<Void> calculateRewards(User user) {
		return CompletableFuture.runAsync(() -> {
			List<VisitedLocation> userLocations = new CopyOnWriteArrayList<>(user.getVisitedLocations());
			for (VisitedLocation visitedLocation : userLocations) {

				for (Attraction attraction : attractions) {
					final VisitedLocation currentVisitedLocation = visitedLocation;
					final Attraction currentAttraction = attraction;

					if (user.getUserRewards().stream()
							.noneMatch(r -> r.attraction.attractionName.equals(currentAttraction.attractionName))) {
						if (nearAttraction(currentVisitedLocation, currentAttraction)) {
							user.addUserReward(new UserReward(currentVisitedLocation, currentAttraction,
									getRewardPoints(currentAttraction, user)));
						}
					}
				}
			}
		}, executorService);
	}

	public void calculateAllRewardsAsync(List<User> users) {
		List<CompletableFuture<Void>> futures = users.stream()
				.map(this::calculateRewards)
				.collect(Collectors.toList());

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	public static double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}

}
