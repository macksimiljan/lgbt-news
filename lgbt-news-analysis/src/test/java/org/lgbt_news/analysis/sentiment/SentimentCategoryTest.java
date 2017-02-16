package org.lgbt_news.analysis.sentiment;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class SentimentCategoryTest {
    @Test
    public void test_categoryToId() throws Exception {
        assertEquals(0, SentimentCategory.VERY_NEG.categoryToId());
        assertEquals(1, SentimentCategory.NEG.categoryToId());
        assertEquals(2, SentimentCategory.NEUTRAL.categoryToId());
        assertEquals(3, SentimentCategory.POS.categoryToId());
        assertEquals(4, SentimentCategory.VERY_POS.categoryToId());
    }

    @org.junit.Test
    public void test_idToCategory() throws Exception {
        assertEquals(SentimentCategory.VERY_NEG, SentimentCategory.idToCategory(-5));
        assertEquals(SentimentCategory.VERY_NEG, SentimentCategory.idToCategory(0));
        assertEquals(SentimentCategory.NEG, SentimentCategory.idToCategory(1));
        assertEquals(SentimentCategory.NEUTRAL, SentimentCategory.idToCategory(2));
        assertEquals(SentimentCategory.POS, SentimentCategory.idToCategory(3));
        assertEquals(SentimentCategory.VERY_POS, SentimentCategory.idToCategory(4));
        assertEquals(SentimentCategory.VERY_POS, SentimentCategory.idToCategory(100));
    }

    @org.junit.Test
    public void test_toString() throws Exception {
        assertEquals("very negative", SentimentCategory.VERY_NEG.toString());
        assertEquals("positive", SentimentCategory.POS.toString());
        assertEquals("neutral", SentimentCategory.NEUTRAL.toString());
    }

}