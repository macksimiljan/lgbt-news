package org.lgbt_news.analysis.util;

import org.lgbt_news.analysis.simple_topic.cooccurrences.Cooccurrence;
import org.lgbt_news.analysis.simple_topic.cooccurrences.CooccurrenceType;

/**
 * @author max
 */
public class CooccurrenceEval {

    private final CooccurrenceType cooccurrence;
    private final double t;
    private final double mutualInformation;
    private final double poisson;
    private Boolean isSign;

    public CooccurrenceEval(CooccurrenceType cooccurrence, double mutualInformation, double poisson, double t) {
        this.cooccurrence = cooccurrence;
        this.mutualInformation = mutualInformation;
        this.poisson = poisson;
        this.t = t;
        isSign = null;
    }

    public void setIsSign(boolean isSign) {
        this.isSign = isSign;
    }

    public int getNoOccurrences() {
        return cooccurrence.getFrequency();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CooccurrenceEval that = (CooccurrenceEval) o;

        return cooccurrence.equals(that.cooccurrence);
    }

    @Override
    public int hashCode() {
        return cooccurrence.hashCode();
    }

    @Override
    public String toString() {
        return cooccurrence+"\tmi: "+mutualInformation+"\tpoisson: "+poisson+"\tt: "+t+"\tsign: "+isSign;
    }
}
