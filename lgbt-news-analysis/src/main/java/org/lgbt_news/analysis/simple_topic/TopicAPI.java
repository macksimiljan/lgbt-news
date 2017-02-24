package org.lgbt_news.analysis.simple_topic;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.lgbt_news.analysis.simple_topic.cooccurrences.ContextVector;
import org.lgbt_news.analysis.simple_topic.cooccurrences.CooccurrenceType;
import org.lgbt_news.analysis.simple_topic.model.StatisticCalculation;
import org.lgbt_news.analysis.simple_topic.preprocess.StopWordList;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatistic;
import org.lgbt_news.analysis.simple_topic.preprocess.WordStatisticCorpus;
import org.lgbt_news.analysis.simple_topic.cooccurrences.CooccurrenceEval;
import org.lgbt_news.analysis.util.ExceptionHandler;
import org.lgbt_news.analysis.util.Window;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author max
 */
public class TopicAPI {

    private final String QUERY_TERM;
    private int MAX_DISTANCE_COOCCURRENCE;

    private double avgFrequency;
    private Set<CooccurrenceEval> evaluations;

    private String path;

    public TopicAPI(String queryTerm, int maxDistanceCooccurrence) {
        this.QUERY_TERM = queryTerm;
        this.MAX_DISTANCE_COOCCURRENCE = maxDistanceCooccurrence;
        avgFrequency = 0;
        evaluations = new HashSet<>();
        path = "./lgbt-news-analysis/src/main/resources/topics_"+ QUERY_TERM +"_"+MAX_DISTANCE_COOCCURRENCE +".csv";
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void start(Set<Window> contexts) {
        System.out.print("Building context vector ... ");
        Set<List<String>> corpus = getCorpus(contexts);
        ContextVector contextVector = new ContextVector(3, corpus, MAX_DISTANCE_COOCCURRENCE);
        contextVector.buildContextVector(QUERY_TERM);
        int[] vector = contextVector.getContextVector(QUERY_TERM);
        String[] contextWords = contextVector.getContextWords();
        System.out.println("(size: "+contextWords.length+")");

        System.out.print("Evaluating cooccurrences ... ");
        WordStatistic wordStatistic = new WordStatisticCorpus(WordStatisticCorpus.getFile());
        StatisticCalculation calc = new StatisticCalculation(wordStatistic);
        for (int i = 0; i < vector.length; i++) {
            int frequency = vector[i];
            if (frequency > 0) {
                avgFrequency += frequency;
                String contextWord = contextWords[i];
                CooccurrenceType cooccurrence = new CooccurrenceType(QUERY_TERM, contextWord);
                cooccurrence.setFrequency(frequency);
                double mutualInformation = calc.executeCalculationMutualInformation(cooccurrence, contextVector.getContextWords().length);
                mutualInformation = Math.round(mutualInformation * 1000) / 1000.0;
                double poisson = calc.executeCalculationLogLikelihood(cooccurrence, contextVector.getContextWords().length);
                poisson = Math.round(poisson * 1000) / 1000.0;
                double t = calc.executeCalculationTTest(cooccurrence, contextVector.getContextWords().length);
                t = Math.round(t * 1000) / 1000.0;
                CooccurrenceEval eval = new CooccurrenceEval(cooccurrence, mutualInformation, poisson, t);
                eval.setSignificance(calc.isSignificantAccToTTest());
                evaluations.add(eval);
            }
        }
        avgFrequency /= 1.0*evaluations.size();
        System.out.println("(number: "+evaluations.size()+")");
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
        for (Sentence sentence : sentences) {
            List<String> wordsInS = sentence.words();
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

    public void printTopicAnalysis(boolean onlyImportantCooccurrences, boolean onlyTabSeperatedValues) {
        System.out.println("\nWriting to "+path+" ... ");

        try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            for (CooccurrenceEval eval : evaluations) {
                if (!onlyImportantCooccurrences || isImportantCooccurrence(eval)) {
                    if (onlyTabSeperatedValues)
                        w.println(eval.toSimpleString());
                    else
                        w.println(eval);
                }
            }
        } catch (IOException e) {
            ExceptionHandler.processException(this, e, "You are here: "+System.getProperty("user.dir"));
        }
    }

    private boolean isImportantCooccurrence(CooccurrenceEval cooccurrence) {
        int frequency = cooccurrence.getFrequency();
        boolean hasImportantFrequency = frequency > 2 && frequency > avgFrequency;
        return hasImportantFrequency || cooccurrence.isSignificant();
    }

    public void printParagraphsWithCooccurrences(Connection connection, boolean onlyImportantCooccurrences) {
        String sql = "SELECT text FROM leadparagraph WHERE LOCATE(?, text) > 0 AND LOCATE(?, text) > 0;";
        try {
            PreparedStatement query = connection.prepareStatement(sql);
            Set<String> paragraphs = new HashSet<>();
            for (CooccurrenceEval eval : evaluations) {
                if (isImportantCooccurrence(eval))
                    paragraphs.addAll(getParagraphs(query, eval.getTarget(), eval.getTarget()));
            }
            query.close();
            writeParagraphsToFileSystem(paragraphs);
        } catch (Exception e) {
            ExceptionHandler.processException(this, e, "Could not print paragraphs for "+ QUERY_TERM);
        }
    }

    private Set<String> getParagraphs(PreparedStatement query, String word0, String word1) throws SQLException {
        Set<String> paragraphs = new HashSet<>();
        query.setString(1, word0);
        query.setString(2, word1);
        ResultSet result = query.executeQuery();
        while ((result.next())) {
            String text = result.getString(1);
            paragraphs.add(text);
        }
        return paragraphs;
    }

    private void writeParagraphsToFileSystem(Set<String> paragraphs) {
        final String path = "./lgbt-news-analysis/src/main/resources/topicsParagraphs_"+ QUERY_TERM +"_"+ MAX_DISTANCE_COOCCURRENCE +".csv";
        System.out.println("\nWriting to "+path+" ... ");

        try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            for (String p : paragraphs)
                w.println(p);
        } catch (IOException e) {
            ExceptionHandler.processException(this, e, "You are here: "+System.getProperty("user.dir"));
        }
    }
}
