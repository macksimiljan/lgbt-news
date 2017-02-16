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
    private final String QUERYTERM;

    private final String SQL_DOCUMENT;
    {
        SQL_DOCUMENT = "SELECT d.id FROM document d, queryterm q WHERE d.id_queryterm = q.id AND q.term = ?;";
    }

    private final String SQL_LEADPARAGRAPH;
    {
        SQL_LEADPARAGRAPH = "SELECT text FROM leadparagraph WHERE id_document = ? AND text LIKE ?;";
    }

    private String containedWord;


    public SentenceExtractor(Connection connection, String queryterm) {
        CONN = connection;
        QUERYTERM = queryterm;
        containedWord = queryterm;
    }


    public List<String> getAllSentenceContexts(String containedWord) {
        this.containedWord = containedWord;
        return getAllSentenceContexts();
    }

    public List<String> getAllSentenceContexts() {
        List<String> contexts = new ArrayList<>();
        try {
            contexts = querySentenceContexts();
        } catch (Exception e) {
            handleException(e);
        }
        return  contexts;
    }

    private List<String> querySentenceContexts() {
        List<String> sentences = new ArrayList<>();
        try {
            Set<Integer> docsOfQueryterm = queryDocumentIds();
            System.out.println("#docs:"+docsOfQueryterm.size());
            PreparedStatement prepStmt = createPreparedStatementForTexts();
            for (int idDocument : docsOfQueryterm) {
                List<String> sentencesPerDocument = querySentencesWithContainedWord(prepStmt, idDocument);
                sentences.addAll(sentencesPerDocument);
            }
            prepStmt.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return sentences;
    }

    private Set<Integer> queryDocumentIds() {
        Set<Integer> documentIds = new HashSet<>();
        try {
            PreparedStatement prepStmt = CONN.prepareStatement(SQL_DOCUMENT);
            prepStmt.setString(1, QUERYTERM);
            ResultSet resultSet = prepStmt.executeQuery();
            while (resultSet.next())
                documentIds.add(resultSet.getInt(1));
            prepStmt.close();
        } catch (SQLException e) {
            handleException(e);
        }

        return documentIds;
    }

    private PreparedStatement createPreparedStatementForTexts() throws SQLException {
        PreparedStatement prepStmt = CONN.prepareStatement(SQL_LEADPARAGRAPH);
        String condition = "%" + containedWord + "%";
        prepStmt.setString(2, condition);
        return prepStmt;
    }

    private List<String> querySentencesWithContainedWord(PreparedStatement prepStmt, int idDocument) throws SQLException {
        List<String> sentencesPerDocument = new ArrayList<>();

        prepStmt.setInt(1, idDocument);
        ResultSet texts = prepStmt.executeQuery();
        while (texts.next()) {
            String text = texts.getString(1);
            List<String> sentencesPerText = extractContainingSentencesFromText(text, containedWord);
            sentencesPerDocument.addAll(sentencesPerText);
        }

        return sentencesPerDocument;
    }

    public static List<String> extractContainingSentencesFromText(String text, String containedWord) {
        List<String> extractedSentences = new ArrayList<>();
        String[] sentences = text.split("\\.");
        for (String s : sentences) {
            if (s.contains(containedWord))
                extractedSentences.add(s.trim()+".");
        }
        return extractedSentences;
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        logger.error(this.getClass().getName()+"\t"+e.getMessage());
    }

}
