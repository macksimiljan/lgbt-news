package org.lgbt_news.analysis.simple_topic.preprocess;

import java.util.HashSet;
import java.util.Set;

/**
 * @author max
 */
public class StopWordList {

    private Set<String> stopWords;

    public StopWordList() {
        stopWords = new HashSet<>();
        loadStopWords();
    }

    public boolean isStopword(String word) {
        word = word.toLowerCase().trim();
        return (stopWords.contains(word)) ? true : false;
    }

    public boolean isANumber(String word) {
        return ( word.matches("-*\\d+[\\.,-]*\\d*") || word.startsWith("-lrb-") );
    }

    private void loadStopWords() {
        // length > 1
        stopWords.add("?!");
        stopWords.add("!!");
        stopWords.add("''");
        stopWords.add("--");
        stopWords.add(">>");
        stopWords.add("<<");
        stopWords.add("``");
        stopWords.add("...");

        stopWords.add("an");
        stopWords.add("that");
        stopWords.add("the");
        stopWords.add("these");
        stopWords.add("this");
        stopWords.add("those");

        stopWords.add("he");
        stopWords.add("it");
        stopWords.add("its");
        stopWords.add("him");
        stopWords.add("his");
        stopWords.add("her");
        stopWords.add("mine");
        stopWords.add("my");
        stopWords.add("our");
        stopWords.add("she");
        stopWords.add("their");
        stopWords.add("them");
        stopWords.add("they");
        stopWords.add("us");
        stopWords.add("we");
        stopWords.add("what");
        stopWords.add("which");
        stopWords.add("who");
        stopWords.add("whom");
        stopWords.add("whose");
        stopWords.add("you");
        stopWords.add("yours");

        stopWords.add("about");
        stopWords.add("after");
        stopWords.add("at");
        stopWords.add("by");
        stopWords.add("for");
        stopWords.add("from");
        stopWords.add("in");
        stopWords.add("into");
        stopWords.add("of");
        stopWords.add("on");
        stopWords.add("over");
        stopWords.add("to");
        stopWords.add("under");
        stopWords.add("while");
        stopWords.add("with");

        stopWords.add("and");
        stopWords.add("but");
        stopWords.add("or");

        stopWords.add("don't");
        stopWords.add("no");
        stopWords.add("not");
        stopWords.add("n't");

        stopWords.add("although");
        stopWords.add("as");
        stopWords.add("because");
        stopWords.add("if");
        stopWords.add("since");
        stopWords.add("then");
        stopWords.add("when");
        stopWords.add("where");
        stopWords.add("whether");

        stopWords.add("are");
        stopWords.add("be");
        stopWords.add("been");
        stopWords.add("did");
        stopWords.add("do");
        stopWords.add("done");
        stopWords.add("does");
        stopWords.add("is");
        stopWords.add("was");
        stopWords.add("were");
        stopWords.add("had");
        stopWords.add("has");
        stopWords.add("have");
        stopWords.add("will");
        stopWords.add("would");

        stopWords.add("also");
        stopWords.add("so");
        stopWords.add("than");

        stopWords.add("'s");
        stopWords.add("'d");
        stopWords.add("'re");
        stopWords.add("'m");
        stopWords.add("'t");
    }
}
