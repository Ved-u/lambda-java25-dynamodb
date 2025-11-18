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


package com.amazonaws.serverless.dao;

import com.amazonaws.serverless.domain.Event;
import com.amazonaws.serverless.manager.DynamoDBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Comparator;

public final class DynamoDBEventDao implements EventDao {
    
    private static final Logger log = LogManager.getLogger(DynamoDBEventDao.class);
    
    private static final class InstanceHolder {
        private static final DynamoDBEventDao INSTANCE = new DynamoDBEventDao();
    }
    
    private final DynamoDbTable<Event> eventTable;
    
    private DynamoDBEventDao() {
        this.eventTable = DynamoDBManager.instance().eventTable();
    }
    
    public static DynamoDBEventDao instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public List<Event> findAllEvents() {
        return eventTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findEventsByCity(String city) {
        var queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(city).build()))
                .build();
                
        return eventTable.index(Event.CITY_INDEX)
                .query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findEventsByTeam(String team) {
        // Query for home team events
        var homeQuery = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(team).build()))
                .build();
        
        var homeEvents = eventTable.query(homeQuery)
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
        
        // Query for away team events
        var awayQuery = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(team).build()))
                .build();
        
        var awayEvents = eventTable.index(Event.AWAY_TEAM_INDEX)
                .query(awayQuery)
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
        
        return Stream.concat(homeEvents.stream(), awayEvents.stream())
                .sorted(Comparator.comparing(Event::getEventDate))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Event> findEventByTeamAndDate(String team, Long eventDate) {
        var key = Key.builder()
                .partitionValue(team)
                .sortValue(eventDate)
                .build();
        
        return Optional.ofNullable(eventTable.getItem(key));
    }

    @Override
    public void saveOrUpdateEvent(Event event) {
        eventTable.putItem(event);
    }

    @Override
    public void deleteEvent(String team, Long eventDate) {
        var key = Key.builder()
                .partitionValue(team)
                .sortValue(eventDate)
                .build();
        
        var deletedEvent = eventTable.deleteItem(key);
        if (deletedEvent == null) {
            log.error("Could not delete event, no such team and date combination");
            throw new IllegalArgumentException("Delete failed for nonexistent event");
        }
    }
}
