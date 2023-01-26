package com.github.tamilakrashtan.vesumsearch.server;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Some methods for final checks.
 * 
 * Lucene can't process some complex criteria of search(like order of words).
 * This methods used for these complex check for filtering of results from
 * Lucene's search.
 */
public class WordsDetailsChecks {

    /**
     * Usually for words, like "нач*"
     */
    private static ThreadLocal<Map<String, Pattern>> WILDCARD_REGEXPS = new ThreadLocal<Map<String, Pattern>>() {
        @Override
        protected Map<String, Pattern> initialValue() {
            return new TreeMap<>();
        }
    };

    public static boolean needWildcardRegexp(String word) {
        return word.contains("*") || word.contains("?");
    }

    public static Pattern getWildcardRegexp(String wildcardWord) {
        Pattern p = WILDCARD_REGEXPS.get().get(wildcardWord);
        if (p == null) {
            p = Pattern.compile(wildcardWord.toLowerCase().replace("+", "").replace("*", ".*").replace('?', '.'));
            WILDCARD_REGEXPS.get().put(wildcardWord, p);
        }
        return p;
    }

    /**
     * Usually for grammar, like "N...[23]..."
     */
    private static ThreadLocal<Map<String, Pattern>> PATTERN_REGEXPS = new ThreadLocal<Map<String, Pattern>>() {
        @Override
        protected Map<String, Pattern> initialValue() {
            return new TreeMap<>();
        }
    };

    public static Pattern getPatternRegexp(String regexp) {
        Pattern p = PATTERN_REGEXPS.get().get(regexp);
        if (p == null) {
            p = Pattern.compile(regexp);
            PATTERN_REGEXPS.get().put(regexp, p);
        }
        return p;
    }
}
