package org.lgbt_news.analysis.simple_topic.cooccurrences;

import java.util.ArrayList;
import java.util.List;

/**
 * @author max
 */
public class CooccurrenceType extends Cooccurrence {

    private List<Integer> distances;
    private int frequency;

    public CooccurrenceType(String word0, String word1) {
        super(word0, word1);
        distances = new ArrayList<>();
        frequency = -1;
    }

    public void addDistance(int distance) {
        distances.add(distance);
    }

    public double aggregateDistances() {
        int sum = 0;
        for (int d : distances)
            sum += d;
        return 1.0*sum / distances.size();
    }

    public int getFrequency() {
        return frequency;
    }
    public void increaseCountOccurrences() {
        frequency++;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }


    @Override
    public String toString() {
        return getTarget()+" -- "+ getCoWord()+"\t#:"+ frequency;
    }

    public String toSimpleString() {
        return getTarget()+"\t"+getCoWord()+frequency;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(aggregateDistances());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
