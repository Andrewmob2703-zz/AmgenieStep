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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private final String COMMENT = "comment";
  private final String NAME = "name";
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> messages = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String comment = (String) entity.getProperty(COMMENT);
      long timestamp = (long) entity.getProperty("timestamp");

      Comment message = new Comment(id, comment, timestamp);
      messages.add(message);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(messages));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = getParameter(request, COMMENT, "");
    String user = getParameter(request, NAME, "");
    long timestamp = System.currentTimeMillis();
    
    Entity commentEntity = new Entity("comment");
    commentEntity.setProperty(NAME, user);
    commentEntity.setProperty(COMMENT, comment);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    
    //send appropriate confirmation response
    String finalResponse = "";
    
    response.setContentType("text/html;");

    if (comment.equals("")) {
      finalResponse = "Got no comment.";
      response.getWriter().println("<h3>" + finalResponse + "</h3>");
    } else {
      finalResponse = "Submitted! Thank you " + user;
      response.getWriter().println("<h3>" + finalResponse + "</h3>");
      response.getWriter().println("<p> <b>message sent:</b> <i>" + comment + "</i></p>");
    }    
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value.equals("")) {
      return defaultValue;
    }
    return value;
  }
}
