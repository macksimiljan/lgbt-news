package org.lgbt_news.analysis;

import org.apache.log4j.Logger;

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
public class SentenceExtractor {

    static final Logger logger = Logger.getLogger("infoLogger");

    private final Connection CONN;

    private final String SQL_LEADPARAGRAPH;
    {
        SQL_LEADPARAGRAPH = "SELECT text FROM leadparagraph WHERE text LIKE ?;";
    }

    private String containedWord;


    public SentenceExtractor(Connection connection) {
        CONN = connection;
        containedWord = "";
    }


    public List<String> getAllSentenceContexts(String containedWord) {
        this.containedWord = containedWord;
        List<String> contexts = new ArrayList<>();
        try {
            contexts = querySentencesWithContainedWord();
        } catch (Exception e) {
            handleException(e);
        }

        return  contexts;
    }

    private List<String> querySentencesWithContainedWord() throws SQLException {
        List<String> sentences = new ArrayList<>();

        PreparedStatement prepStmt = createPreparedStatementForTexts();
        ResultSet texts = prepStmt.executeQuery();
        int count = 0;
        while (texts.next()) {
            count++;
            String text = texts.getString(1);
            List<String> sentencesPerText = extractContainingSentencesFromText(text, containedWord);
            sentences.addAll(sentencesPerText);
        }
        System.out.println(count);
        System.out.println(sentences.size());
        prepStmt.close();

        return sentences;
    }

    private PreparedStatement createPreparedStatementForTexts() throws SQLException {
        PreparedStatement prepStmt = CONN.prepareStatement(SQL_LEADPARAGRAPH);
        String condition = "%" + containedWord + "%";
        prepStmt.setString(1, condition);
        return prepStmt;
    }

    public static List<String> extractContainingSentencesFromText(String text, String containedWord) {
        List<String> extractedSentences = new ArrayList<>();
        String[] sentences = text.split("\\.");
        for (String s : sentences) {
            if (s.toLowerCase().contains(containedWord.toLowerCase()))
                extractedSentences.add(s.trim()+".");
        }

        return extractedSentences;
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        logger.error(this.getClass().getName()+"\t"+e.getMessage());
    }

}
