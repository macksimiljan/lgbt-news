package org.lgbt_news.collect.request;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Manages the request parameters and creates an URL connection
 * based on these parameters.
 *
 * @author max
 */
public class RequestNewYorkTimes implements  Request {

    public enum Sort {
        NEWEST, OLDEST;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private final String apiKey;
    private final String queryTerm;
    private final String filterQuery;
    private final String beginDate;
    private final String endDate;
    private final Sort sort;
    private final String returnedFields;
    private final boolean isHighlightedResult;
    private final int page;
    private final String facetFields;
    private final boolean hasFacetCount;

    private HttpURLConnection conn = null;
    private String responseString = null;
    private JSONObject responseJson;

    public static class Builder {

        private final String apiKey;
        private final String queryTerm;

        private String filterQuery;
        private String beginDate;
        private String endDate;
        private Sort sort;
        private String returnedFields;
        private boolean isHighlightedResult = false;
        private int page = 1;
        private String facetFields;
        private boolean hasFacetCount = false;

        public Builder (String queryTerm) {
            apiKey = "db91d31f57ce4b179374110e96429b82"; // TODO: read from file
            this.queryTerm = queryTerm;
        }

        public Builder filterQuery(String val) {
            filterQuery = val;
            return this;
        }

        public Builder beginDate(String val) {
            beginDate = val;
            return this;
        }

        public Builder endDate(String val) {
            endDate = val;
            return this;
        }

        public Builder sort(RequestNewYorkTimes.Sort val) {
            sort = val;
            return this;
        }

        public Builder returnedFields(String val) {
            returnedFields = val;
            return this;
        }

        public Builder isHighlightedResult(boolean val) {
            isHighlightedResult = val;
            return this;
        }

        public Builder page(int val) {
            page = val;
            return this;
        }

        public Builder facetFields(String val) {
            facetFields = val;
            return this;
        }

        public Builder hasFacetCount(boolean val) {
            hasFacetCount = val;
            return this;
        }

        public RequestNewYorkTimes build() {
            return new RequestNewYorkTimes(this);
        }
    }

    private RequestNewYorkTimes(Builder builder) {
        apiKey = builder.apiKey;
        queryTerm = builder.queryTerm;
        filterQuery = builder.filterQuery;
        beginDate = builder.beginDate;
        endDate = builder.endDate;
        sort = builder.sort;
        returnedFields = builder.returnedFields;
        isHighlightedResult = builder.isHighlightedResult;
        page = builder.page;
        facetFields = builder.facetFields;
        hasFacetCount = builder.hasFacetCount;
    }

    public String getUsedApi() {
        return "New York Times, Article Search";
    }

    public String getResponseAsString() throws IOException {
        if (responseString == null) {
            InputStream response = firingRequest();
            if (response == null)
                throw new IOException("No response for '"+toString()+"'!");
            responseString = convertToString(response);
            closeConnection();
        }

        return responseString;
    }

    public JSONObject getResponseAsJson() throws IOException {
        if (responseJson == null)
            responseJson = new JSONObject(getResponseAsString());

        return responseJson;
    }

    public void printResponse(PrintWriter w) {
        try {
            w.println(getResponseAsString());
        } catch (Exception e) {
            w.println("ERROR,"+toString()+","+e.getMessage());
        }
    }

    @Override
    public String toString() {
        String request = "https://api.nytimes.com/svc/search/v2/articlesearch.json?";
        request += "q="+queryTerm;
        if (filterQuery != null)
            request += "&fq="+filterQuery;
        if (beginDate != null)
            request += "&begin_date="+beginDate;
        if (endDate != null)
            request += "&end_date="+endDate;
        if (sort != null)
            request += "&sort="+sort.toString();
        if (returnedFields != null)
            request += "&fl="+returnedFields;
        if (isHighlightedResult == true)
            request += "&hl=" + String.valueOf(isHighlightedResult);
        if (page != 1)
            request += "&p="+String.valueOf(page);
        if (facetFields != null)
            request += "&facet_field="+facetFields;
        if (hasFacetCount == true)
            request += "&facet_filter="+String.valueOf(hasFacetCount);

        return request;
    }

    private InputStream firingRequest() {
        InputStream response = null;
        createUrlConnection();
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        try {
            response = conn.getInputStream();
        } catch (IOException e) {
            if (e.getMessage().contains("Server returned HTTP response code: 429")) {
                System.err.println("You have reached your limit while querying for '"+toString()+"':\n\t"+
                        conn.getHeaderField("X-RateLimit-Remaining-day")+"/"+conn.getHeaderField("X-RateLimit-Limit-day")+" per day, "+
                        conn.getHeaderField("X-RateLimit-Remaining-second")+"/"+conn.getHeaderField("X-RateLimit-Limit-second")+" per second.");
            }
            else
                e.printStackTrace();
        }

        return response;
    }

    private void closeConnection() {
        conn.disconnect();
    }

    private String convertToString(InputStream response) {
        // http://stackoverflow.com/questions/18073849/get-a-json-object-from-a-http-response
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void createUrlConnection() {
        try {
            URL url = new URL(toString());
            conn = (HttpURLConnection)url.openConnection();
            conn.addRequestProperty("api-key", apiKey);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
