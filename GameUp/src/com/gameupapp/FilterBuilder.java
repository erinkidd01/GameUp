package com.gameupapp;

import java.util.List;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

/**
 * TODO add temporal filtering (ie allow excluding past games)
 */
public class FilterBuilder {
	private ParseQuery<GameParse> query;
	private GameUpInterface gameup;
	
	public FilterBuilder() {
		query = ParseQuery.getQuery(GameParse.class);
		gameup = GameUpInterface.getInstance();
	}
	
	/**
	 * TODO: Assumes miles
	 * @param radius
	 * @param latitude
	 * @param longitude
	 */
	public FilterBuilder setRadius(double radius, double latitude, double longitude) {
		ParseQuery<GameParse> radQuery = gameup.getQueriesWithinMiles(
				radius, latitude, longitude);
		query.whereMatchesQuery("location", radQuery);
		return this;
	}
	
	/**
	 * 
	 * @param level Takes the desired ability level (from 1-4)
	 */
	public FilterBuilder setAbilityLevel(int level) {
		assert(level < 4);
		assert(level >= 0);
		
		ParseQuery<GameParse> abilityQuery = gameup.getQueryWithAbility(level);
		query.whereMatchesKeyInQuery("abilityLevel", "abilityLevel", abilityQuery);
		return this;
	}
	
	/**
	 * 
	 * @param sport The name of the desired sport
	 */
	public FilterBuilder setSport(String sport) {
		ParseQuery<GameParse> sportQuery = gameup.getQueryWithSportName(sport);
		query.whereMatchesKeyInQuery("sport", "sport", sportQuery);
		return this;
	}
	
	/**
	 * Filters out all games that have already happened
	 */
	public FilterBuilder filterIntoFuture() {
		ParseQuery<GameParse> temporalFilter = gameup.getQueryOnFutureGames();
		query.whereMatchesKeyInQuery("startDateTime", "startDateTime", temporalFilter);
		return this;
	}
	
	public FilterBuilder addSortAscendingOnKey(String key) {
		query.addAscendingOrder(key);
		return this;
	}
	
	public FilterBuilder addSortDescendingOnKey(String key) {
		query.addDescendingOrder(key);
		return this;
	}
	
	public FilterBuilder exclusivelySortAscendingOnKey(String key) {
		query.orderByAscending(key);
		return this;
	}
	
	public FilterBuilder exclusivelySortDescendingOnKey(String key) {
		query.orderByDescending(key);
		return this;
	}
	
	/**
	 * TODO I'm not sure if we actually always need to include sport?
	 * @return A query constructed to match all desired parameters 
	 */
	public ParseQuery<GameParse> build() {
		query.include("sport");
		return query;
	}
	
	/**
	 * TODO I'm not sure if we actually always need to include sport?
	 * @return A list of all games matching the constructed query
	 */
	public List<GameParse> execute() {
		query.include("sport");
		return gameup.filterGamesWithQuery(query);
		
	}
	
}
