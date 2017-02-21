package org.lgbt_news.analysis.simple_topic.cooccurrences;

/**
 * @author max
 */
public class Cooccurrence {

    private static int count = 0;

    private final int ID;
    private String target;
    private String coWord;
    private int distanceInWords;

    public Cooccurrence(String target, String coWord) {
        ID = count++;
        this.target = target;
        this.coWord = coWord;
    }

    public String getTarget() {
        return target;
    }

    public String getCoWord() {
        return coWord;
    }

    public int getDistanceInWords() {
        return distanceInWords;
    }

    public void setDistanceInWords(int distanceInWords) {
        this.distanceInWords = distanceInWords;
    }

    @Override
    public String toString() {
        return target +" -- "+ coWord +" (distance: "+distanceInWords+")";
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cooccurrence that = (Cooccurrence) o;

        return ID == that.ID;
    }
}
