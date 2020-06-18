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

import com.google.sps.Event;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  private static final Collection<Event> NO_EVENTS = Collections.emptySet();
  private static final Collection<String> NO_ATTENDEES = Collections.emptySet();

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = getRequestAttendees(request);
    long requestDuration = getRequestDuration(request);

    int eventStart = getEventTimeSpan(events).start();
    int eventEnd = getEventTimeSpan(events).end();

    if (attendees == NO_ATTENDEES){
      return Arrays.asList(TimeRange.WHOLE_DAY);
    } 
    
    if (requestDuration > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    if (events != NO_EVENTS) {
      Collection<TimeRange> availableTimes = 
          Arrays.asList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, eventStart, false),
            TimeRange.fromStartEnd(eventEnd, TimeRange.END_OF_DAY, true));
    }
    
    // TODO: Finish Method Implementation...

  }

  public long getRequestDuration(MeetingRequest request) {
    return request.getDuration();
  }

  public Collection<String> getRequestAttendees(MeetingRequest request) {
    return request.getAttendees();
  }

  public TimeRange getEventTimeSpan(Collection<Event> event) {
    return event.getWhen();
  }
}
