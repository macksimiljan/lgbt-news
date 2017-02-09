package org.lgbt_news.collect.insert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import javax.xml.crypto.Data;

import java.sql.Connection;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class ResponseImportTest {
    @Test
    public void test_excecuteImport() {
        boolean success = false;
        try {
            DatabaseAccess db = new DatabaseAccess();
            Connection conn = db.getDbConnection();
            ResponseImport responseImport = new ResponseImport(conn, "NYT", "queryterm");
            responseImport.excecuteImport(buildDocument1());
            responseImport.excecuteImport(buildDocument2());

            db.closeDbConnection();

            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(success);
    }


    private JSONObject buildDocument1() {
        JSONObject document = new JSONObject();
        document.put("web_url", "http://example.com");
        document.put("snippet", "Test Text Snippet.");
        JSONObject byline = new JSONObject();
        byline.put("original", "By HERBERT HENDIN");
        JSONArray person = new JSONArray();
        JSONObject p = new JSONObject();
        p.put("firstname", "Herbert");
        p.put("role", "reported");
        p.put("organization", "");
        p.put("rank", 1);
        p.put("lastname", "HENDIN");
        person.put(p);
        byline.put("person", person);
        document.put("source", "NYT");
        JSONObject headline = new JSONObject();
        headline.put("main", "Headline...");
        headline.put("kicker", "Kicker...");
        document.put("headline", headline);
        document.put("lead_paragraph", "lead paragraph ...");
        JSONArray keywords = new JSONArray();
        JSONObject keyword = new JSONObject();
        keyword.put("name", "kw_name1");
        keyword.put("value", "kw_val1");
        keywords.put(keyword);
        JSONObject keyword2 = new JSONObject();
        keyword2.put("name", "kw_name2");
        keyword2.put("value", "kw_val2");
        keywords.put(keyword2);
        document.put("keywords", keywords);
        document.put("_id", "4fc4703145c1498b0d9cf29f");

        return document;
    }

    private JSONObject buildDocument2() {
        JSONObject document = new JSONObject();
        document.put("web_url", "http://exampleexample.com");
        document.put("snippet", "Test Text Snippet.Test Text Snippet.Test Text Snippet.Test Text Snippet.Test Text Snippet.");
        JSONObject byline = new JSONObject();
        byline.put("original", "By HERBERT HENDIN & MAX MUSTERMAN");
        JSONArray person = new JSONArray();
        JSONObject p = new JSONObject();
        p.put("firstname", "Herbert");
        p.put("role", "reported");
        p.put("organization", "");
        p.put("rank", 1);
        p.put("lastname", "HENDIN");
        person.put(p);
        JSONObject p2 = new JSONObject();
        p2.put("firstname", "Max");
        p2.put("role", "reported");
        p2.put("organization", "");
        p2.put("rank", 1);
        p2.put("lastname", "Musterman");
        person.put(p2);
        byline.put("person", person);
        document.put("source", "NYT");
        JSONObject headline = new JSONObject();
        headline.put("main", "Headline......");
        headline.put("kicker", "Kicker......");
        document.put("headline", headline);
        document.put("lead_paragraph", "");
        JSONArray keywords = new JSONArray();
        JSONObject keyword = new JSONObject();
        keyword.put("name", "kw_name1");
        keyword.put("value", "kw_val1");
        keywords.put(keyword);
        JSONObject keyword2 = new JSONObject();
        keyword2.put("name", "kw_name3");
        keyword2.put("value", "kw_val3");
        keywords.put(keyword2);
        document.put("keywords", keywords);
        document.put("_id", "4fc4703145c1498b0d9cf27f");
        document.put("word_count", 50);

        return document;
    }

}