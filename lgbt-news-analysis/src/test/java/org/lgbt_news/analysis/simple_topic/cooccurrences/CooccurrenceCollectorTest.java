package org.lgbt_news.analysis.simple_topic.cooccurrences;

import org.junit.Test;
import org.lgbt_news.analysis.util.SentenceExtractor;
import org.lgbt_news.analysis.util.Window;
import org.lgbt_news.collect.insert.DatabaseAccess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class CooccurrenceCollectorTest {

    static final String testDocument = "Snakes are great. She loves a man. The child is lovely. The man is a transgender. " +
            "A transgender man is lovely.";

    @Test
    public void test_findCooccurrences() throws Exception {
        String word = "transgender";

        List<Window> windows = SentenceExtractor.extractContainingWindowsFromText(testDocument, word, 1);
        assertEquals("The child is lovely. The man is a transgender. A transgender man is lovely.",windows.get(0).getSentencesAsText());
        assertEquals("The man is a transgender. A transgender man is lovely.", windows.get(1).getSentencesAsText());

        CooccurrenceCollector collector = new CooccurrenceCollector(null, word);
        Collection<CooccurrenceType> cooccurrences = collector.findCooccurrences(new HashSet<>(windows));
        assertEquals(3, cooccurrences.size());

    }

}