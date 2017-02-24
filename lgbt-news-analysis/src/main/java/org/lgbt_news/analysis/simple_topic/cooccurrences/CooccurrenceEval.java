package org.lgbt_news.analysis.simple_topic.cooccurrences;

/**
 * @author max
 */
public class CooccurrenceEval extends Cooccurrence {

    private final int frequency;
    private final double t;
    private final double mutualInformation;
    private final double poisson;
    private Boolean isSign;

    public CooccurrenceEval(CooccurrenceType cooccurrence, double mutualInformation, double poisson, double t) {
        super(cooccurrence.getTarget(), cooccurrence.getCoWord());
        this.frequency = cooccurrence.getFrequency();
        this.mutualInformation = mutualInformation;
        this.poisson = poisson;
        this.t = t;
        isSign = null;
    }

    public void setSignificance(boolean isSign) {
        this.isSign = isSign;
    }

    public boolean isSignificant() {
        return (isSign == null) ? false : isSign;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return super.toString()+"\tmi: "+mutualInformation+"\tpoisson: "+poisson+"\tt: "+t+"\tsign: "+isSign;
    }

    public String toSimpleString() {
        return super.toSimpleString()+"\t"+frequency+"\t"+isSign;
    }
}
