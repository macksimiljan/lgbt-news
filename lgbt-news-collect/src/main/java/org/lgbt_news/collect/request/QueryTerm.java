package org.lgbt_news.collect.request;

/**
 * LGBT terms for querying.
 *
 * @author max
 */
public enum QueryTerm {
    GAY,
    LESBIAN,
    HOMOSEXUAL;


    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
