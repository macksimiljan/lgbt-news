package org.lgbt_news.analysis.util;

import org.junit.Test;
import org.lgbt_news.analysis.util.SentenceExtractor;
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

        SentenceExtractor extractor = new SentenceExtractor(connection);
        List<String> sentences = extractor.getSentenceContexts(queryterm);
        db.closeDbConnection();

        assertEquals(3065, sentences.size());
    }

    @Test
    public void test_extractContainingSentencesFromText() {
        String text = "Ich gehe nach Hause. Ich mag Hunde. Hunde sind wirklich toll. Ich mag Sonne.";
        String containedWord = "Hund";
        int actualNoSentences = SentenceExtractor.extractContainingSentencesFromText(text, containedWord).size();
        assertEquals(2, actualNoSentences);
    }

    @Test
    public void test_extractContainingWindowsFromText() {
        String text = "Heute ist Sonntag. Ich gehe nach Hause. Ich mag Hunde. Hunde sind wirklich toll. Ich mag Sonne. Der Mond scheint.";
        String containedWord = "Hund";
        List<String> windows = SentenceExtractor.extractContainingWindowsFromText(text, containedWord, 1);
        int actualNoSentences = windows.size();
        assertEquals(2, actualNoSentences);
        assertEquals("Ich gehe nach Hause. Ich mag Hunde. Hunde sind wirklich toll.", windows.get(0));
        assertEquals("Ich mag Hunde. Hunde sind wirklich toll. Ich mag Sonne.", windows.get(1));

        windows = SentenceExtractor.extractContainingWindowsFromText(text, containedWord, 3);
        actualNoSentences = windows.size();
        assertEquals(2, actualNoSentences);
        assertEquals("Heute ist Sonntag. Ich gehe nach Hause. Ich mag Hunde. Hunde sind wirklich toll. Ich mag Sonne. Der Mond scheint.", windows.get(0));

    }

}