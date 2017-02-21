package org.lgbt_news.analysis.simple_topic.preprocess;

import java.util.Map;

/**
 * @author max
 */
public interface WordStatistic {

    double getProbability(String word);
    int getFrequency(String word);
    int getNoOfWordTypes();
    String[] getContextWords(int thresholdContexWord);
    Map<String, Integer> getInverseIndexContextWords(int thresholdContextWord);
}
