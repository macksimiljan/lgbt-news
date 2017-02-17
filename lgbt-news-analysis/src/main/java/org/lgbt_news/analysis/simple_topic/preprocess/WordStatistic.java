package org.lgbt_news.analysis.simple_topic.preprocess;

/**
 * @author max
 */
public interface WordStatistic {

    double getProbability(String word);
    int getNumberOfTokens();
}
