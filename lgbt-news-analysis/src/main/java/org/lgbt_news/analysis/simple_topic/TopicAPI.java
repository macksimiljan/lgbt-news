package org.lgbt_news.analysis.simple_topic;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.lgbt_news.analysis.simple_topic.cooccurrences.ContextVector;
import org.lgbt_news.analysis.simple_topic.cooccurrences.CooccurrenceType;
import org.lgbt_news.analysis.simple_topic.model.StatisticCalculation;
import org.lgbt_news.analysis.simple_topic.preprocess.StopWordList;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatistic;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatisticCorpus;
import org.lgbt_news.analysis.util.CooccurrenceEval;
import org.lgbt_news.analysis.util.ExceptionHandler;
import org.lgbt_news.analysis.util.Window;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author max
 */
public class TopicAPI {

   private String queryterm;
    private int maxDistanceCooccurrence;

    private Set<CooccurrenceEval> evaluations;

    public TopicAPI(String queryterm, int maxDistanceCooccurrence) {
        this.queryterm = queryterm;
        this.maxDistanceCooccurrence = maxDistanceCooccurrence;
        evaluations = new HashSet<>();
    }



    public void start(Set<Window> contexts) {
        System.out.println("Building context vector ... ");
        Set<List<String>> corpus = getCorpus(contexts);
        ContextVector contextVector = new ContextVector(3, corpus, maxDistanceCooccurrence);
        contextVector.buildContextVector(queryterm);
        int[] vector = contextVector.getContextVector(queryterm);
        String[] contextWords = contextVector.getContextWords();

        WordStatistic wordStatistic = new WordStatisticCorpus(WordStatisticCorpus.getFile());
        System.out.println("#corpus: "+contextVector.getContextWords().length);
        StatisticCalculation calc = new StatisticCalculation(wordStatistic);
        for (int i = 0; i < vector.length; i++) {
            int frequency = vector[i];
            if (frequency > 0) {
                String contextWord = contextWords[i];
                CooccurrenceType cooccurrence = new CooccurrenceType(queryterm, contextWord);
                cooccurrence.setFrequency(frequency);
                double mutualInformation = calc.executeCalculationMutualInformation(cooccurrence, contextVector.getContextWords().length);
                mutualInformation = Math.round(mutualInformation * 1000) / 1000.0;
                double poisson = calc.executeCalculationLogLikelihood(cooccurrence, contextVector.getContextWords().length);
                poisson = Math.round(poisson * 1000) / 1000.0;
                double t = calc.executeCalculationTTest(cooccurrence, contextVector.getContextWords().length);
                t = Math.round(t * 1000) / 1000.0;
                CooccurrenceEval eval = new CooccurrenceEval(cooccurrence, mutualInformation, poisson, t);
                eval.setIsSign(calc.isSignificantAccToTTest());
                evaluations.add(eval);
            }
        }


    }

    private Set<List<String>> getCorpus(Set<Window> contexts) {
        Set<List<String>> corpus = new HashSet<>();
        for (Window window : contexts) {
            List<String> words = getWords(new Document(window.getSentencesAsText()).sentences());
            corpus.add(words);
        }
        return corpus;
    }

    private List<String> getWords(List<Sentence> sentences) {
        List<String> words = new ArrayList<>();
        StopWordList stopWordList = new StopWordList();
        for (int i = 0; i < sentences.size(); i++) {
            List<String> wordsInS = sentences.get(i).words();
            for (String w : wordsInS) {
                if (!stopWordList.isStopWord(w) && !stopWordList.isANumber(w))
                    words.add(w);
            }
        }
        return words;
    }

    public Set<CooccurrenceEval> getEvaluations() {
        return evaluations;
    }

    public void printTopicAnalysis() {
        final String path = "./lgbt-news-analysis/src/main/resources/topics_"+queryterm+"_"+maxDistanceCooccurrence+".csv";
        System.out.println("\nWriting to "+path+" ... ");

        try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            for (CooccurrenceEval eval : evaluations) {
                if (eval.getNoOccurrences() > 0)
                    w.println(eval);
            }
        } catch (IOException e) {
            ExceptionHandler.processException(this, e, "You are here: "+System.getProperty("user.dir"));
        }
    }
}
