package org.lgbt_news.analysis.sentiment.filter;

import org.lgbt_news.analysis.sentiment.SentimentCategory;

/**
 * @author max
 */
public interface Filter {

    SentimentCategory apply(double[] predictions);
}
