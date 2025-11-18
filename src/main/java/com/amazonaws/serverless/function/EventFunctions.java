// Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License"). You may
// not use this file except in compliance with the License. A copy of the
// License is located at
//
//	  http://aws.amazon.com/apache2.0/
//
// or in the "license" file accompanying this file. This file is distributed
// on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
// express or implied. See the License for the specific language governing
// permissions and limitations under the License.


package com.amazonaws.serverless.function;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.amazonaws.serverless.dao.DynamoDBEventDao;
import com.amazonaws.serverless.pojo.Team;
import com.amazonaws.serverless.util.Consts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.serverless.domain.Event;
import com.amazonaws.serverless.pojo.City;

public final class EventFunctions {

    private static final Logger log = LogManager.getLogger(EventFunctions.class);
    private static final DynamoDBEventDao eventDao = DynamoDBEventDao.instance();


    public List<Event> getAllEventsHandler() {

        log.info("GetAllEvents invoked to scan table for ALL events");
        List<Event> events = eventDao.findAllEvents();
        log.info("Found " + events.size() + " total events.");
        return events;
    }

    public List<Event> getEventsForTeam(Team team) {
        if (team == null || team.getTeamName() == null || 
            team.getTeamName().isEmpty() || team.getTeamName().equals(Consts.UNDEFINED)) {
            log.error("GetEventsForTeam received null or empty team name");
            throw new IllegalArgumentException("Team name cannot be null or empty");
        }

        var name = URLDecoder.decode(team.getTeamName(), StandardCharsets.UTF_8);
        log.info("GetEventsForTeam invoked for team with name = {}", name);
        var events = eventDao.findEventsByTeam(name);
        log.info("Found {} events for team = {}", events.size(), name);

        return events;
    }

    public List<Event> getEventsForCity(City city) {
        if (city == null || city.getCityName() == null || 
            city.getCityName().isEmpty() || city.getCityName().equals(Consts.UNDEFINED)) {
            log.error("GetEventsForCity received null or empty city name");
            throw new IllegalArgumentException("City name cannot be null or empty");
        }

        var name = URLDecoder.decode(city.getCityName(), StandardCharsets.UTF_8);
        log.info("GetEventsForCity invoked for city with name = {}", name);
        var events = eventDao.findEventsByCity(name);
        log.info("Found {} events for city = {}", events.size(), name);

        return events;
    }

    public void saveOrUpdateEvent(Event event) {
        if (event == null) {
            log.error("SaveEvent received null input");
            throw new IllegalArgumentException("Cannot save null object");
        }

        log.info("Saving or updating event for team = {}, date = {}", 
                event.getHomeTeam(), event.getEventDate());
        eventDao.saveOrUpdateEvent(event);
        log.info("Successfully saved/updated event");
    }

    public void deleteEvent(Event event) {
        if (event == null) {
            log.error("DeleteEvent received null input");
            throw new IllegalArgumentException("Cannot delete null object");
        }

        log.info("Deleting event for team = {}, date = {}", 
                event.getHomeTeam(), event.getEventDate());
        eventDao.deleteEvent(event.getHomeTeam(), event.getEventDate());
        log.info("Successfully deleted event");
    }

}
