package org.lgbt_news.collect.request;

/**
 * LGBT terms for querying.
 *
 * @author max
 */
public enum QueryTerm {
    GAY, // apparently we are not allowed to query for 'gay'
    LESBIAN,
    HOMOSEXUAL;


    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
