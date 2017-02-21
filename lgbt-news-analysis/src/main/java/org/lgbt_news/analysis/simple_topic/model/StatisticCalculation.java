package org.lgbt_news.analysis.simple_topic.model;

import org.lgbt_news.analysis.simple_topic.cooccurrences.CooccurrenceType;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatistic;

/**
 * @author max
 */
public class StatisticCalculation {

    private final WordStatistic WORD_STATISTIC;
    private float alpha;

    private CooccurrenceType cooccurrence;
   private boolean isSignificant;

    public StatisticCalculation(WordStatistic wordStatistic) {
        WORD_STATISTIC = wordStatistic;
        cooccurrence = null;
        isSignificant = false;
    }

    public double executeCalculationMutualInformation(CooccurrenceType cooccurrence, int corpusSize) {
        String wordA = cooccurrence.getTarget();
        String wordB = cooccurrence.getCoWord();

        int frequencyA = WORD_STATISTIC.getFrequency(wordA);
        int frequencyB = WORD_STATISTIC.getFrequency(wordB);
        int frequencyCooccurrence = cooccurrence.getFrequency();

        double mutualInformation = log2((corpusSize * frequencyCooccurrence) / (frequencyA * frequencyB));
        return Math.round(mutualInformation*1000)/1000.0;
    }

    private double log2(int x) {
        return Math.log10(x) / Math.log10(2);
    }

    public double executeCalculationLogLikelihood(CooccurrenceType cooccurrence, int corpusSize) {
        String wordA = cooccurrence.getTarget();
        String wordB = cooccurrence.getCoWord();

        int frequencyA = WORD_STATISTIC.getFrequency(wordA);
        int frequencyB = WORD_STATISTIC.getFrequency(wordB);
        int frequencyCooccurrence = cooccurrence.getFrequency();

        double l = -2 * Math.log(getLambda(corpusSize, frequencyA, frequencyB, frequencyCooccurrence));
        //l = (frequencyCooccurrence < (frequencyA * frequencyB / corpusSize)) ? -1*l : l;

        return l;
    }

    private double getLambda(int n, int nA, int nB, int nAB) {
        double lambda = n * Math.log10(n) - nA * Math.log10(nA) - nB * Math.log10(nB) + nAB * Math.log10(nAB)
                + (n-nA-nB+nAB) * Math.log10(n-nA-nB+nAB)
                + (nA-nAB) * Math.log10(nA - nAB) + (nB-nAB) * Math.log10(nB-nAB)
                - (n-nA) * Math.log10(n-nA) - (n-nB) * Math.log10(n-nB);
        return lambda;
    }

    public double executeCalculationTTest(CooccurrenceType cooccurrence, int corpusSize) {
        isSignificant = false;

        String wordA = cooccurrence.getTarget();
        String wordB = cooccurrence.getCoWord();

        long frequencyA = WORD_STATISTIC.getFrequency(wordA);
        long frequencyB = WORD_STATISTIC.getFrequency(wordB);
        long frequencyCooccurrence = cooccurrence.getFrequency();

        double part1 = frequencyCooccurrence - (frequencyA * frequencyB / Math.pow(corpusSize, 2));
        double part2 = Math.sqrt(frequencyCooccurrence);

        double t = part1 / part2;

        if (t > 2.576)
            isSignificant = true;

        return t;
    }

    public boolean isSignificantAccToTTest() {
        return isSignificant;
    }


}
