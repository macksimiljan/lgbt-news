package org.lgbt_news.analysis.util;

import org.apache.log4j.Logger;
import org.lgbt_news.collect.utils.NytDate;

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

    private static final Logger logger = Logger.getLogger("infoLogger");

    private final Connection CONN;

    private final String SQL_DOCUMENT;
    {
        SQL_DOCUMENT = "SELECT d.id FROM document d WHERE d.pubdate LIKE ?;";
    }

    private final String SQL_LEADPARAGRAPH;
    {
        SQL_LEADPARAGRAPH = "SELECT text FROM leadparagraph WHERE id_document = ? AND text LIKE ?;";
    }

    private NytDate pubDate;
    private String containedWord;
    private int halfWindowSize;


    public SentenceExtractor(Connection connection) {
        CONN = connection;
        pubDate = null;
        containedWord = "";
        halfWindowSize = 0;
    }


    public List<String> getSentenceContexts(String containedWord, NytDate pubDate) {
        return getWindowContexts(containedWord, pubDate,0);
    }

    public List<String> getWindowContexts(String containedWord, NytDate pubDate, int halfWindowSize) {
        this.pubDate = pubDate;
        this.containedWord = containedWord;
        this.halfWindowSize = halfWindowSize;
        List<String> contexts = new ArrayList<>();
        try {
            contexts = queryContexts();
        } catch (Exception e) {
            handleException(e);
        }

        return  contexts;
    }

    private List<String> queryContexts() {
        List<String> contexts = new ArrayList<>();
        try {
            Set<Integer> documentIds = queryDocumentIds();
            PreparedStatement stmtTexts = createPreparedStatementForTexts();
            for (int id : documentIds) {
                List<String> documentWindows = queryWindowsWithContainedWord(stmtTexts, id);
                contexts.addAll(documentWindows);
            }
            stmtTexts.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return contexts;
    }

    private Set<Integer> queryDocumentIds() {
        Set<Integer> documentIds = new HashSet<>();
        try {
            PreparedStatement prepStmt = CONN.prepareStatement(SQL_DOCUMENT);
            String pubDateString = pubDate.getYear()+"%";
            prepStmt.setString(1, pubDateString);
            ResultSet resultSet = prepStmt.executeQuery();
            while (resultSet.next())
                documentIds.add(resultSet.getInt(1));
            prepStmt.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return documentIds;
    }

    private List<String> queryWindowsWithContainedWord(PreparedStatement stmtTexts, int documentId) throws SQLException {
        List<String> sentences = new ArrayList<>();

        stmtTexts.setInt(1, documentId);
        ResultSet texts = stmtTexts.executeQuery();
        while (texts.next()) {
            String text = texts.getString(1);
            List<String> windowsPerText = extractContainingWindowsFromText(text, containedWord, halfWindowSize);
            sentences.addAll(windowsPerText);
        }

        return sentences;
    }

    private PreparedStatement createPreparedStatementForTexts() throws SQLException {
        PreparedStatement prepStmt = CONN.prepareStatement(SQL_LEADPARAGRAPH);
        String condition = "%" + containedWord + "%";
        prepStmt.setString(2, condition);
        return prepStmt;
    }

    public static List<String> extractContainingSentencesFromText(String text, String containedWord) {
        return extractContainingWindowsFromText(text, containedWord, 0);
    }

    public static List<String> extractContainingWindowsFromText(String text, String containedWord, int halfWindowSize) {
        List<String> extractedWindows = new ArrayList<>();
        final String[] sentences = text.split("\\.");
        for (int i = 0; i < sentences.length; i++) {
            String currentSentence = sentences[i];
            if (currentSentence.toLowerCase().contains(containedWord.toLowerCase())) {
                String window = buildWindow(sentences, i, halfWindowSize);
                extractedWindows.add(window);
            }
        }

        return extractedWindows;
    }

    private static String buildWindow(String[] sentences, int currentSentencePosition, int halfWindowSize) {
        String window = "";

        int windowEnd = currentSentencePosition + halfWindowSize;
        windowEnd = (windowEnd >= sentences.length) ? sentences.length-1 : windowEnd;
        int windowStart = currentSentencePosition - halfWindowSize;
        windowStart = (windowStart < 0) ? 0 : windowStart;

        for (int i = windowStart; i <= windowEnd; i++)
            window += sentences[i].trim()+". ";

        return window.trim();
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        logger.error(this.getClass().getName()+"\t"+e.getMessage());
    }

}
