package com.github.tamilakrashtan.vesumsearch.server.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.tamilakrashtan.vesumsearch.grammar.DBTagsGroups.KeyValue;

import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class InitialData {
    public List<KeyValue> subcorpuses;
    public Map<String, List<String>> authors; // map by subcorpus name
    public Map<String, List<String>> sources; // map by subcorpus name
    public List<String> styleGenresParts;
    public Map<String, List<String>> styleGenres;
    public GrammarInitial grammar;
    public List<Stat> stat;
    public String[] kankardansnyjaSpisy;
    public String preselectedSubcorpuses;
    public Map<String, Map<String, String>> localization;

    public static class Stat {
        public String name;
        public long texts;
        public long words;
    }
}
