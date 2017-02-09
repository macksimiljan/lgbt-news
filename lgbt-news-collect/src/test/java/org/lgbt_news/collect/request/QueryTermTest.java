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
        assertEquals("gay", QueryTerm.GAY.toString());
        assertEquals("GAY", QueryTerm.GAY.name());
    }

}