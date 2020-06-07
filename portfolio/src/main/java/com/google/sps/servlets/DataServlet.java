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

import com.google.gson.Gson;
import java.io.IOException;
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
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String finalResponse = "";
    String getMessage = getParameter(request, COMMENT, "");
    
    response.setContentType("text/html;");

    if (getMessage.equals("")) {
      finalResponse = "Got no comment.";
      response.getWriter().println("<h3>" + finalResponse + "</h3>");
    }else {
      finalResponse = "Submitted! Thank you " + getParameter(request, NAME, "");
      response.getWriter().println("<h3>" + finalResponse + "</h3>");
      response.getWriter().println("<p> <b>message sent:</b> <i>" + getMessage + "</i></p>");
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
