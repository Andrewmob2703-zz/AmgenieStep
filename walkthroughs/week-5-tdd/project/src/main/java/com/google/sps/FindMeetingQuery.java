// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {
  private static final Collection<Event> NO_EVENTS = Collections.emptySet();
  private static final Collection<String> NO_ATTENDEES = Collections.emptySet();  
  private static final int endOfDay = TimeRange.END_OF_DAY; 
  
  private static int WHOLE_DAY = 1440;

    // sort and return busy times for requested attendees
    private static Collection<TimeRange> getCoveredTimesForAttendees(Collection<Event> events,
    Collection<String> mandatoryAttendees) {   
      Set<String> setOfAttendees = new HashSet<String>();
      for(String attendee : mandatoryAttendees) {
        setOfAttendees.add(attendee);
      }

      // define 'coveredTimesForAttendees' as ArrayList to use 
      // suitable method for sorting the time ranges
      ArrayList<TimeRange> coveredTimesForAttendees = new ArrayList <TimeRange> ();
      for(Event event: events) {
        Collection<String> attendeesInEvent = event.getAttendees();
        TimeRange timeCoveredByEvent = event.getWhen();
        for(String attendee : attendeesInEvent) {   
          if(setOfAttendees.contains(attendee)) {
            coveredTimesForAttendees.add(timeCoveredByEvent);
            break;
          } 
        }
      }
      Collections.sort(coveredTimesForAttendees, TimeRange.ORDER_BY_START);
      return coveredTimesForAttendees;
    }

    // calculate and return available time ranges for requested events
    private static Collection<TimeRange> getAvailableTimes(Collection<Event> events,
    Collection<String> mandatoryAttendees, long requestDuration) {
      Collection<TimeRange> availableTimes = new ArrayList<TimeRange>();

      int timeForNewEventsBegin = TimeRange.START_OF_DAY;
      int eventStart = 0;
      int eventEnd = 0;

      if (requestDuration > WHOLE_DAY) {
        return availableTimes;
      }

      if(mandatoryAttendees == NO_ATTENDEES) {
        availableTimes.add(TimeRange.WHOLE_DAY);
        return availableTimes;
      }

      if(events == NO_EVENTS) {
        availableTimes.add(TimeRange.WHOLE_DAY);
        return availableTimes;
      }
      
      Collection<TimeRange> coveredTimesForAttendees = 
        getCoveredTimesForAttendees(events, mandatoryAttendees);
      for(TimeRange coveredTime : coveredTimesForAttendees) {
        eventStart = coveredTime.start();
        eventEnd = coveredTime.end();
        if(eventStart < timeForNewEventsBegin) {
          timeForNewEventsBegin = Math.max(timeForNewEventsBegin, eventEnd);
          continue;            
        }
       
        if(eventStart >= timeForNewEventsBegin + requestDuration) {
          availableTimes.add(TimeRange.fromStartEnd(timeForNewEventsBegin, eventStart, false));
        }
        timeForNewEventsBegin = eventEnd;
      }
      
      if(endOfDay >= timeForNewEventsBegin + requestDuration) {
        availableTimes.add(TimeRange.fromStartEnd(timeForNewEventsBegin, endOfDay, true));
      }

      return availableTimes;
    }
    
    public Collection <TimeRange> query(Collection <Event> events, MeetingRequest request) {
      Collection<String> mandatoryAttendees = request.getAttendees();

      long requestDuration = request.getDuration();

      Collection<TimeRange> availableTimesForRequestedEvents = getAvailableTimes(events, mandatoryAttendees, requestDuration);
      return availableTimesForRequestedEvents;              
    }
}