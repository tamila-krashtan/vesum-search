package com.github.tamilakrashtan.vesumsearch.grammar;

import java.util.ArrayList;
import java.util.List;

public class GrammarSearchResult {
    public String error;
    public boolean hasDuplicateParadigms;
    public List<LemmaInfo> output = new ArrayList<>();
    public boolean hasMultiformResult;
}
