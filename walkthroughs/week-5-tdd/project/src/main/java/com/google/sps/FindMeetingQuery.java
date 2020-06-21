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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {
  private static final Collection<Event> NO_EVENTS = Collections.emptySet();
  private static final Collection<String> NO_ATTENDEES = Collections.emptySet();

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> availableTimes = new ArrayList<TimeRange>();
    Collection<Event> totalEvents = new ArrayList<Event>();
    Collection<String> attendeesInRequest = request.getAttendees();
    long requestDuration = request.getDuration();

    int TimeForNewEventsBegin = TimeRange.START_OF_DAY;
    int endOfDay = TimeRange.END_OF_DAY;
    int eventStarts = 0;
    int eventDuration = 0;
    int eventEnds = 0;
    String eventTitle = "";

    if (attendeesInRequest == NO_ATTENDEES){
      return Arrays.asList(TimeRange.WHOLE_DAY);
    } 
    
    if (requestDuration > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    for (Event event : events){
      eventStarts = event.getWhen().start();
      eventDuration = event.getWhen().duration();
      eventEnds = event.getWhen().end();
      eventTitle = event.getTitle();
      if (attendeesInRequest.size() == 1){
        for (String attendeeInRequest : attendeesInRequest) {
          for (String attendeeInEvent : event.getAttendees()) {
            if (!attendeeInRequest.equals(attendeeInEvent)) {
              availableTimes.add(TimeRange.fromStartEnd(TimeForNewEventsBegin, endOfDay, true));
              return availableTimes;
            }
          }
        }
      }
      if (TimeForNewEventsBegin >= eventStarts && TimeForNewEventsBegin >= eventEnds) 
      {continue;}
      if (TimeForNewEventsBegin > eventStarts) {
        totalEvents.add(new Event(eventTitle, TimeRange.fromStartEnd(
            TimeForNewEventsBegin, eventEnds, false), attendeesInRequest));
      } else {
          totalEvents.add(new Event(eventTitle, TimeRange.fromStartDuration(
              eventStarts, eventDuration), attendeesInRequest));
      }
      TimeForNewEventsBegin = eventEnds;
    }

    TimeForNewEventsBegin = TimeRange.START_OF_DAY;
    for (Event event : totalEvents) {
      eventStarts = event.getWhen().start();
      eventEnds = event.getWhen().end();
      TimeRange beforeNextEvent = TimeRange.fromStartEnd(TimeForNewEventsBegin, eventStarts, false);
      if (beforeNextEvent.duration() >= requestDuration) {
        availableTimes.add(beforeNextEvent);
      }
      TimeForNewEventsBegin = eventEnds;
    }

    TimeRange afterLastEventInDay = TimeRange.fromStartEnd(
        TimeForNewEventsBegin, endOfDay, true);

    // From last event to the end of the day could be available 
    // if there's some time in-between  
    if (afterLastEventInDay.start() - 1 != endOfDay) {
      availableTimes.add(afterLastEventInDay);
    }
    return availableTimes;
  }
}