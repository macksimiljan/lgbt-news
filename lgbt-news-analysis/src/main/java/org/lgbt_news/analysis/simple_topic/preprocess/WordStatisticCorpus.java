package org.lgbt_news.analysis.simple_topic.preprocess;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.lgbt_news.analysis.util.ExceptionHandler;

import javax.naming.directory.InvalidAttributesException;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author max
 */
public class WordStatisticCorpus implements WordStatistic {

    private static final String FILE = "./src/main/resources/wordStatistics.tsv";
    private final String KEY_SUM = "##sum##";
    private final String SQL = "SELECT text FROM leadparagraph";
    private final Connection CONN;

    private Map<String, Integer> occurrences;
    private int sum;

    public WordStatisticCorpus(Connection connection) {
        CONN = connection;
        occurrences = new HashMap<>();
        sum = 0;
        createStatistic();
    }

    public WordStatisticCorpus(String file) {
        CONN = null;
        occurrences = new HashMap<>();
        sum = 0;
        loadStatisticFromFile(file);
    }

    private void createStatistic() {
       try {
            Set<String> texts = getTexts();
            countOccurrences(texts);
            writeOccurrencesToFile();
        } catch (Exception e) {
            ExceptionHandler.processFatalException(this, e, "Could not create statistics from database.");
        }

    }

    private Set<String> getTexts() throws Exception {
        Set<String> texts = new HashSet<>();
        Statement query = CONN.createStatement();
        ResultSet resultSet = query.executeQuery(SQL);
        while (resultSet.next()) {
            String text = resultSet.getString(1);
            texts.add(text);
        }
        return texts;
    }

    private void countOccurrences(Set<String> texts) throws Exception {
        StopWordList stopWordList = new StopWordList();
        for (String text : texts) {
            Document document = new Document(text);
            for (Sentence sentence : document.sentences())
                countOccurencesInSentence(sentence, stopWordList);
        }
        occurrences.put(KEY_SUM, sum);
    }

    private void countOccurencesInSentence(Sentence sentence, StopWordList stopWordList) throws Exception {
        for (String word : sentence.words()) {
            word = word.toLowerCase();
            if (word.length() > 1 && !stopWordList.isStopword(word) && !stopWordList.isANumber(word)) {
                Integer oldValue = occurrences.get(word);
                Integer newValue = (oldValue == null) ? 1 : oldValue+1;
                occurrences.put(word, newValue);
                sum += 1;
            }
        }
    }

    private void writeOccurrencesToFile() {
        try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(FILE)))) {
            for (String word : occurrences.keySet()) {
                w.println(word+"\t"+occurrences.get(word));
            }
        } catch (Exception e) {
            ExceptionHandler.processException(this, e, "Could not write word statistics! You are here: "+System.getProperty("user.dir"));
        }
    }

    private void loadStatisticFromFile(String file) {
        try {
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = r.readLine()) != null) {
                    String[] data = line.split("\t");
                    String word = data[0].trim();
                    int count = Integer.valueOf(data[1].trim());
                    if (occurrences.containsKey(word))
                        throw new InvalidAttributesException("The word '"+word+"' occurs twice in the statistics!");
                    occurrences.put(word, count);
                }
            }
        } catch (Exception e) {
            ExceptionHandler.processFatalException(this, e);
        }
    }

    @Override
    public double getProbability(String word) {
        double probability = 0;
        try {
            int count = occurrences.get(word);
            probability = 1.0*count / occurrences.get(KEY_SUM);
        } catch (Exception e) {
            ExceptionHandler.processException(this, e, "Word '"+word+"' not found or missing sum key '"+KEY_SUM+"'!");
        }
        return probability;
    }

    @Override
    public int getNumberOfTokens() {
        return occurrences.size();
    }

    public static String getFile() {
        return FILE;
    }
}
