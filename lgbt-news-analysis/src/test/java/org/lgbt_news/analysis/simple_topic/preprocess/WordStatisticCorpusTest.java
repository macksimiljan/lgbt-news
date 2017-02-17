package org.lgbt_news.analysis.simple_topic.preprocess;

import org.junit.Test;
import org.lgbt_news.collect.insert.DatabaseAccess;

import java.sql.Connection;

import static org.junit.Assert.*;

/**
 * @author max
 */
public class WordStatisticCorpusTest {
    @Test
    public void test_readFromDatabase() throws Exception {
        DatabaseAccess db = new DatabaseAccess();
        Connection connection = db.getDbConnection();
        WordStatisticCorpus wordStatistic = new WordStatisticCorpus(connection);
        assertEquals(66328, wordStatistic.getNumberOfTokens());
        db.closeDbConnection();


    }

    @Test
    public void test_readFromFile() throws Exception {
        WordStatistic wordStatistic = new WordStatisticCorpus(WordStatisticCorpus.getFile());
        assertEquals(0.0013, wordStatistic.getProbability("transgender"),0.001);
        System.out.println(wordStatistic.getProbability("transgender"));
    }

}