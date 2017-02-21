package org.lgbt_news.analysis.util;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
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


    public Set<Window> getSentenceContexts(String containedWord, NytDate pubDate) {
        return getWindowContexts(containedWord, pubDate,0);
    }

    public Set<Window> getWindowContexts(String containedWord, NytDate pubDate, int halfWindowSize) {
        this.pubDate = pubDate;
        this.containedWord = containedWord;
        this.halfWindowSize = halfWindowSize;
        Set<Window> contexts = new HashSet<>();
        try {
            contexts = queryContexts();
        } catch (Exception e) {
            handleException(e);
        }

        return  contexts;
    }

    public Set<Window> getWindowContexts(String containedWord, NytDate beginPubDate, NytDate endPubDate, int halfWindowSize) {
        int start = Integer.valueOf(beginPubDate.getYear());
        int end = Integer.valueOf(endPubDate.getYear());

        Set<Window> allWindows = new HashSet<>();
        for (int year = start; year <= end; year++) {
            NytDate pubDate = new NytDate.Builder().year(year).createDate();
            Set<Window> windows = getWindowContexts(containedWord, pubDate, halfWindowSize);
            allWindows.addAll(windows);
        }
        return allWindows;
    }

    private Set<Window> queryContexts() {
        Set<Window> contexts = new HashSet<>();
        try {
            Set<Integer> documentIds = queryDocumentIds();
            PreparedStatement stmtTexts = createPreparedStatementForTexts();
            for (int id : documentIds) {
                List<Window> documentWindows = queryWindowsWithContainedWord(stmtTexts, id);
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

    private List<Window> queryWindowsWithContainedWord(PreparedStatement stmtTexts, int documentId) throws SQLException {
        List<Window> windows = new ArrayList<>();

        stmtTexts.setInt(1, documentId);
        ResultSet texts = stmtTexts.executeQuery();
        while (texts.next()) {
            String text = texts.getString(1);
            List<Window> windowsPerText = extractContainingWindowsFromText(text, containedWord, halfWindowSize);
            windows.addAll(windowsPerText);
        }

        return windows;
    }

    private PreparedStatement createPreparedStatementForTexts() throws SQLException {
        PreparedStatement prepStmt = CONN.prepareStatement(SQL_LEADPARAGRAPH);
        String condition = "%" + containedWord + "%";
        prepStmt.setString(2, condition);
        return prepStmt;
    }

    public static List<Window> extractContainingSentencesFromText(String text, String containedWord) {
        return extractContainingWindowsFromText(text, containedWord, 0);
    }

    public static List<Window> extractContainingWindowsFromText(String text, String containedWord, int halfWindowSize) {
        List<Window> extractedWindows = new ArrayList<>();

        Document document = new Document(text);
        List<Sentence> sentences = document.sentences();
        for (int i = 0; i < sentences.size(); i++) {
            Sentence currentSentence = sentences.get(i);
            if (currentSentence.text().toLowerCase().contains(containedWord.toLowerCase())) {
                Window window = buildWindow(sentences, i, halfWindowSize);
                extractedWindows.add(window);
            }
        }

        return extractedWindows;
    }

    private static Window buildWindow(List<Sentence> sentences, int currentSentencePosition, int halfWindowSize) {
        int windowEnd = currentSentencePosition + halfWindowSize;
        windowEnd = (windowEnd >= sentences.size()) ? sentences.size()-1 : windowEnd;
        int windowStart = currentSentencePosition - halfWindowSize;
        windowStart = (windowStart < 0) ? 0 : windowStart;

        String[] windowSentences = new String[windowEnd-windowStart+1];
        for (int i = windowStart; i <= windowEnd; i++)
            windowSentences[i-windowStart] = sentences.get(i).text();
        int indexFocus = currentSentencePosition-windowStart;

        return new Window(windowSentences, indexFocus);
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        logger.error(this.getClass().getName()+"\t"+e.getMessage());
    }

}
