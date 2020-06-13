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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
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
  private final String NAME = "name";
  private final String GET_LOAD_COMMENT_QUANTITY = "loadcommentquantity";
  private final String USER_EMAIL = "email"; 

  public static final String COMMENT = "comment";
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // server side Get check: login required to load comments
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/login_out");
      return;
    }

    // user specified comments load quantity should be a number
    int maxCommentsLoad = getUserInput(request);
    if (maxCommentsLoad == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer.");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();

    /** search for comments made with the current user's email address
      start with last comment made
    */
    Query query = 
        new Query(COMMENT).setFilter(new Query.FilterPredicate(
            USER_EMAIL, Query.FilterOperator.EQUAL, userEmail))
                .addSort("timestamp", SortDirection.DESCENDING);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> messages = new ArrayList<>();
    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(maxCommentsLoad))) {
      long messageId = entity.getKey().getId();
      String comment = (String) entity.getProperty(COMMENT);
      String email = (String) entity.getProperty(USER_EMAIL);
      long timestamp = (long) entity.getProperty("timestamp");

      Comment message = new Comment(messageId, comment, email, timestamp);
      messages.add(message);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(messages));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // server side Post check: login required to comment
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/login_out");
      return;
    }

    String comment = getParameter(request, COMMENT, "");
    String languageCode = getParameter(request, "lang", "");
    String user = getParameter(request, NAME, "");
    String userEmail = userService.getCurrentUser().getEmail();
    long timestamp = System.currentTimeMillis();

    // Translate comment.
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    Translation translation =
        translate.translate(comment, Translate.TranslateOption.targetLanguage(languageCode));
    String translatedComment = translation.getTranslatedText();
    
    // store comment details
    Entity commentEntity = new Entity(COMMENT);
    commentEntity.setProperty(NAME, user);
    commentEntity.setProperty(COMMENT, translatedComment);
    commentEntity.setProperty(USER_EMAIL, userEmail);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    
    // send appropriate confirmation response
    String finalResponse = "";
    
    response.setContentType("text/html;");

    if (comment.equals("")) {
      finalResponse = "Got no comment.";
      response.getWriter().println("<h3>" + finalResponse + "</h3>");
    } else {
      System.out.println(languageCode);
      finalResponse = "Submitted! Thank you " + user;
      response.getWriter().println("<h3>" + finalResponse + "</h3>");
      response.getWriter().println("<p> <b>message sent:</b> <i>" + translatedComment + "</i></p>");
    }    
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value.equals("")) {
      return defaultValue;
    }
    return value;
  }

  private int getUserInput(HttpServletRequest request) {
    int maxCommentsLoad = 0;

    // get input and convert to int
    try {
      maxCommentsLoad = Integer.parseInt(request.getParameter(GET_LOAD_COMMENT_QUANTITY));
    } catch (NumberFormatException nfe) {
      System.err.println("Input exception: " + nfe.getMessage());
      return -1;
    }
    return maxCommentsLoad;
  }
}
