/*
 * Copyright 2016 geoint.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geoint.canon.server.servlet;

import java.io.IOException;
import java.util.StringTokenizer;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.geoint.canon.Canon;

/**
 * REST interface to interact with the event stream.
 */
@WebServlet(name = "eventStream")
public class EventStreamServlet extends HttpServlet {

    @Inject
    Canon canon;


    /*
     *  GET /streams : returns a meta about the streams available
     *  GET /streams/[streamName] : returns meta about the stream
     *  GET /streams/[streamName]/events : returns events 
     *  GET /streams/[streamName]/events/[eventId] : returns a specific event
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final StringTokenizer path = new StringTokenizer(req.getPathInfo(), "/");
        switch (path.countTokens()) {
            case 0:
            // GET /streams
                canon.
            case 1:
            // GET /streams/[streamName]
            case 2:
            // GET /streams/[streamName]/events
            case 3:
            // GET /streams/[streamName]/events/[eventId]
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /*
     *  HEAD /streams/[streamName] : returns meta about the stream
     *  HEAD /streams/[streamName]/events/[eventId] : returns a specific event
     */
    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final StringTokenizer path = new StringTokenizer(req.getPathInfo(), "/");
        switch (path.countTokens()) {
            case 1:
            //HEAD /streams/[streamName]
            case 3:
            //HEAD /streams/[streamName]/events/[eventId]
            default:
                super.doHead(req, resp);
        }
    }

    /*
     *  /streams/[streamName] : last event appended
     *  /streams/[streamName]/events/[eventId] : time of event
     */
    @Override
    protected long getLastModified(HttpServletRequest req) {
        final StringTokenizer path = new StringTokenizer(req.getPathInfo(), "/");
        switch (path.countTokens()) {
            case 1:
            //HEAD /streams/[streamName]
            case 3:
            //HEAD /streams/[streamName]/events/[eventId]
            default:
                return super.getLastModified(req);
        }
    }

    /*
     * POST /streams/[streamName]/events : append a new event to the stream
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final StringTokenizer path = new StringTokenizer(req.getPathInfo(), "/");
        switch (path.countTokens()) {
            case 2:
            //POST /streams/[streamName]/events
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /*
     * PUT /streams/[streamName] : creates stream
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final StringTokenizer path = new StringTokenizer(req.getPathInfo(), "/");
        switch (path.countTokens()) {
            case 1:
            //PUT /streams/[streamName]
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
