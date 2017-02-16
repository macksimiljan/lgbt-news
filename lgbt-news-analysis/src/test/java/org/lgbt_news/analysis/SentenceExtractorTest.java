package org.lgbt_news.analysis;

import org.junit.Test;
import org.lgbt_news.collect.insert.DatabaseAccess;
import org.lgbt_news.collect.request.QueryTerm;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class SentenceExtractorTest {
    @Test
    public void test_getAllSentenceContexts() throws Exception {
        DatabaseAccess db = new DatabaseAccess();
        Connection connection = db.getDbConnection();
        String queryterm = QueryTerm.TRANSGENDER.toString();

        SentenceExtractor extractor = new SentenceExtractor(connection, queryterm);
        List<String> sentences = extractor.getAllSentenceContexts();
        System.out.println("There are "+sentences.size()+" sentence(s) for "+queryterm+".");

        // TODO: scheint nicht korrekt zu sein

        db.closeDbConnection();

    }

    @Test
    public void test_extractContainingSentencesFromText() {
        String text = "Ich gehe nach Hause. Ich mag Hunde. Hunde sind wirklich toll. Ich mag Sonne.";
        String containedWord = "Hund";
        int actualNoSentences = SentenceExtractor.extractContainingSentencesFromText(text, containedWord).size();
        assertEquals(2, actualNoSentences);
    }

}