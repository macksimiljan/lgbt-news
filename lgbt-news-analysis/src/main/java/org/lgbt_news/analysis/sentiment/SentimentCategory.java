package org.lgbt_news.analysis.sentiment;

/**
 * @author max
 */
public enum SentimentCategory {

    VERY_NEG(0),
    NEG(1),
    NEUTRAL(2),
    POS(3),
    VERY_POS(4);

    private final int categoryId;

    SentimentCategory(int id) {
        categoryId = id;
    }

    public int categoryToId() {
        return categoryId;
    }

    public static SentimentCategory idToCategory(int id) {
        id = (id < 0) ? 0 : id;
        id = (id > 4) ? 4 : id;

        return SentimentCategory.values()[id];
    }

    public static SentimentCategory getCategoryFromPredictions(double[] predictions) {
        double maxPrediction = -1;
        SentimentCategory category = null;
        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] > maxPrediction) {
                maxPrediction = predictions[i];
                category = SentimentCategory.idToCategory(i);
            }
        }
        return  category;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", " ").replace("neg", "negative").replace("pos", "positive");
    }
}
