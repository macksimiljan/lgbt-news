package org.lgbt_news.analysis.util;

import org.junit.Test;
import org.lgbt_news.collect.insert.DatabaseAccess;
import org.lgbt_news.collect.request.QueryTerm;
import org.lgbt_news.collect.utils.NytDate;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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
        NytDate pubDate = new NytDate.Builder().year(2017).createDate();
        Set<String> sentences = extractor.getSentenceContexts(queryterm, pubDate);
        assertEquals(60, sentences.size());

        db.closeDbConnection();
    }

    @Test
    public void test_extractContainingSentencesFromText() {
        String text = "Ich gehe nach Hause. Ich mag Hunde von M. Musterman. Hunde sind wirklich toll. Ich mag Sonne.";
        String containedWord = "Hund";
        List<String> sentences = SentenceExtractor.extractContainingSentencesFromText(text, containedWord);
        assertEquals("Ich mag Hunde von M. Musterman.", sentences.get(0));
        assertEquals("Hunde sind wirklich toll.", sentences.get(1));
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