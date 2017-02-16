package org.lgbt_news.collect.request;

/**
 * LGBT terms for querying.
 *
 * @author max
 */
public enum QueryTerm {
    BISEXUAL,
    GAY_COMMUNITY,
    GAY_MARRIAGE,
    GAY_RIGHTS,
    HOMOSEXUAL,
    LESBIAN,
    TRANSGENDER,
    TRANSSEXUAL;
    // add: same-sex


    @Override
    public String toString() {
        String str = super.toString().toLowerCase();
        if (str.contains("_"))
            str = "\""+str.replace("_", "+")+"\"";
        return str;
    }
}
