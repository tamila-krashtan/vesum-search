package com.github.tamilakrashtan.vesumsearch.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * DTO for grammar database search results.
 */
public class LemmaInfo {
    public long pdgId;
    public String output;
    public String meaning;
    public String grammar;

    public static class LemmaParadigm {
        public String lemma;
        public String tag;
        public String meaning;
        public List<LemmaVariant> variants = new ArrayList<>();
    }

    public static class LemmaVariant {
        public String id;
        public String tag;
        public List<LemmaForm> forms = new ArrayList<>();
        public Set<String> dictionaries = new TreeSet<>();
        public List<Author> authors = new ArrayList<>();
    }

    public static class LemmaForm {
        public String tag;
        public String value;
        public String options;
        public String type;
    }

    public static class Author {
        public String name;
        public String displayName;
    }
}
