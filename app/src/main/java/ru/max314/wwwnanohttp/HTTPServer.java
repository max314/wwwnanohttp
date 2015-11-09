package ru.max314.wwwnanohttp;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by max on 06.11.2015.
 */
public class HTTPServer extends NanoHTTPD {
    public HTTPServer() {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Response r =  newFixedLengthResponse(
                "<html><body>Redirected: <a href=\"" + uri + "\">" +
                        uri + "</a></body></html>");
        r.addHeader( "Location", uri );
        return r;

//        Response stdout =  super.serve(session);
//        return stdout;
    }
}
