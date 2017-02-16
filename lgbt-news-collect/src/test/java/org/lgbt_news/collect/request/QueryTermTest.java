package org.lgbt_news.collect.request;

import org.junit.Assert.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author max
 */
public class QueryTermTest {

    @Test
    public void test_toString() {
        assertEquals("lesbian", QueryTerm.LESBIAN.toString());
        assertEquals("TRANSGENDER", QueryTerm.TRANSGENDER.name());
        assertEquals("\\\"gay community\\\"", QueryTerm.GAY_COMMUNITY.toString());
    }

}