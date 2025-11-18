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


package com.amazonaws.serverless.domain;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbTable;

@DynamoDbBean
@DynamoDbTable(tableName = "EVENT")
public class Event {
    
    public static final String TABLE_NAME = "EVENT";
    
    public static final String CITY_INDEX = "City-Index";
    public static final String AWAY_TEAM_INDEX = "AwayTeam-Index";

    private Long eventId;
    private Long eventDate;
    private String sport;
    private String homeTeam;
    private String awayTeam;
    private String city;
    private String country;

    public Event() {}

    public Event(String homeTeam, Long eventDate) {
        this.homeTeam = homeTeam;
        this.eventDate = eventDate;
    }

    public Event(Long eventId, Long eventDate, String sport, String homeTeam, 
                 String awayTeam, String city, String country) {
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.sport = sport;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.city = city;
        this.country = country;
    }

    @DynamoDbAttribute("eventId")
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    @DynamoDbSortKey
    @DynamoDbAttribute("eventDate")
    public Long getEventDate() { return eventDate; }
    public void setEventDate(Long eventDate) { this.eventDate = eventDate; }

    @DynamoDbAttribute("sport")
    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("homeTeam")
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    @DynamoDbSecondaryPartitionKey(indexNames = {AWAY_TEAM_INDEX})
    @DynamoDbAttribute("awayTeam")
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    @DynamoDbSecondaryPartitionKey(indexNames = {CITY_INDEX})
    @DynamoDbAttribute("city")
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @DynamoDbAttribute("country")
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
