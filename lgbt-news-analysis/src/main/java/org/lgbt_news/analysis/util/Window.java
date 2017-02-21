package org.lgbt_news.analysis.util;

import java.util.Arrays;

/**
 * A window is built by consecutive sentences around a target word within a document.
 *
 * @author max
 */
public class Window {

    private final String[] sentences;
    private final int indexFocus;
    private String targetWord;

    public Window(String[] sentences, int indexFocus) {
        this.sentences = sentences;
        this.indexFocus = indexFocus;
    }

    public Window(String[] sentences, int indexFocus, String targetWord) {
        this.sentences = sentences;
        this.indexFocus = indexFocus;
        this.targetWord = targetWord;
    }

    public String[] getSentences() {
        return sentences;
    }

    public String getSentencesAsText() {
        String text = "";
        for (String sentence : sentences)
            text += sentence+" ";

        return text.trim();
    }

    public int getIndexFocus() {
        return indexFocus;
    }

    public String getTargetWord() {
        return targetWord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Window window = (Window) o;

        if (indexFocus != window.indexFocus) return false;

        return this.getSentencesAsText().equals(window.getSentencesAsText());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(sentences);
        result = 31 * result + indexFocus;
        return result;
    }

    @Override
    public String toString() {
        return "window focus:"+ indexFocus +", window:"+getSentencesAsText();
    }
}
