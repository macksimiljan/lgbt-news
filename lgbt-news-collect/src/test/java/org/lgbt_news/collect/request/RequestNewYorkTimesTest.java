package org.lgbt_news.collect.request;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lgbt_news.collect.utils.PropertyPoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author max
 */
public class RequestNewYorkTimesTest {

    static Request request = null;

    @BeforeClass
    public static void initialize() {
        String apiKey = PropertyPoint.getNytKeys().get(0);
        String term = QueryTerm.HOMOSEXUAL.toString();
        request = (new RequestNewYorkTimes.Builder(term.toString(), apiKey)
                    .beginDate("19771125")
                    .endDate("19771125")
                    .returnedFields("snippet").build());
    }

    @Test
    public void test_getUsedApi() throws Exception {
        String usedApi = request.getUsedApi();
        assertTrue(usedApi.toLowerCase().contains("new york times"));
    }

    @Test
    public void test_getResponseAsString() throws Exception {
       assertTrue(request.getResponseAsString().contains("\"docs\":[{\"snippet\":\"It's Not the Homosexual.... (Movie): Rev...\"},{\"snippet\":null}]}"));
    }

    @Test
    public void test_getResponseAsJson() throws Exception {
        JSONObject json = request.getResponseAsJson();
        int hits = (Integer)json.getJSONObject("response").getJSONObject("meta").get("hits");
        assertEquals(2, hits);
    }

    @Test
    public void test_toString() {
        String expected = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=homosexual&begin_date=19771125&end_date=19771125&fl=snippet";
        assertEquals(expected, request.toString());

    }

}