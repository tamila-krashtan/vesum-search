package com.github.tamilakrashtan.vesumsearch.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.tamilakrashtan.vesumsearch.grammar.DBTagsGroups;
import com.github.tamilakrashtan.vesumsearch.grammar.DBTagsGroups.KeyValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
public class GrammarInitial {
    public Map<Character, GrammarLetter> grammarTree;
    public List<KeyValue> grammarWordTypes;
    public Map<Character, DBTagsGroups> grammarWordTypesGroups;
    public Map<Character, Set<String>> skipGrammar;
    public List<GrammarDict> dictionaries;
    public List<Stat> stat;
    public Map<String, Map<String, String>> localization;

    public static class Stat {
        public String title;
        public int paradigmCount;
        public int formCount;
    }

    @JsonInclude(Include.NON_NULL)
    public static class GrammarLetter {
        public String name;
        public String desc;
        public Map<Character, GrammarLetter> ch;
    }

    @JsonInclude(Include.NON_NULL)
    public static class GrammarDict {
        public String name;
        public String desc;
    }
}
