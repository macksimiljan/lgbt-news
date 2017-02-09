package org.lgbt_news.collect.request;

import java.io.IOException;
import java.io.InputStream;
import org.json.*;

/**
 * @author max
 */
public interface Request {


    String getUsedApi();
    String getResponseAsString() throws IOException;
    JSONObject getResponseAsJson() throws IOException;
}
