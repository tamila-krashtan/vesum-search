package com.github.tamilakrashtan.vesumsearch.utils;

import com.github.tamilakrashtan.vesumsearch.grammar.Form;
import com.github.tamilakrashtan.vesumsearch.grammar.Paradigm;
import com.github.tamilakrashtan.vesumsearch.grammar.Variant;

import java.util.*;

public class SetUtils {

    public static String set2string(Set<String> set) {
        if (set.isEmpty()) {
            return null;
        }
        StringBuilder r = new StringBuilder();
        for (String s : set) {
            if (r.length() > 0) {
                r.append('_');
            }
            r.append(s);
        }
        return r.toString();
    }

    public static String addTag(String orig, String tag) {
        if (orig == null || orig.trim().isEmpty()) {
            return tag;
        }
        if (hasTag(orig, tag)) {
            return orig;
        }
        return orig + ',' + tag;
    }

    public static boolean hasTag(String orig, String tag) {
        if (orig == null || orig.trim().isEmpty()) {
            return false;
        }
        for (String v : orig.split(",")) {
            if (v.trim().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public static String removeTag(String orig, String tag) {
        if (orig == null || orig.trim().isEmpty()) {
            return null;
        }
        List<String> out = new ArrayList<>();
        for (String v : orig.split(",")) {
            if (!v.trim().equals(tag)) {
                out.add(v);
            }
        }
        return out.isEmpty() ? null : String.join(",", out);
    }

    public static boolean hasDictionary(Form f, String dictionary) {
        return hasTag(f.getDictionaries(), dictionary);
    }

    public static boolean hasDictionary(Variant v, String dictionary) {
        return hasTag(v.getDictionaries(), dictionary);
    }

    public static void addDictionary(Form f, String dictionary) {
        f.setDictionaries(addTag(f.getDictionaries(), dictionary));
    }

    public static void addDictionary(Variant v, String dictionary) {
        v.setDictionaries(addTag(v.getDictionaries(), dictionary));
    }

    public static Map<String, String> getDictionaries(String dictionaries) {
        if (dictionaries == null) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new TreeMap<>();
        for (String s : dictionaries.split(",")) {
            s = s.trim();
            if (s.isEmpty()) {
                continue;
            }
            int p = s.indexOf(':');
            if (p < 0) {
                result.put(s, null);
            } else {
                result.put(s.substring(0, p), s.substring(p + 1));
            }
        }
        return result;
    }

    public static void removeDictionary(Form f, String dictionary) {
        f.setDictionaries(removeTag(f.getDictionaries(), dictionary));
    }

    public static boolean hasOrthography(Form f, String orthography) {
        return hasTag(f.getOrthography(), orthography);
    }

    public static boolean hasOrthography(Variant v, String orthography) {
        return hasTag(v.getOrthography(), orthography);
    }

    public static void addOrthography(Form f, String orthography) {
        f.setOrthography(addTag(f.getOrthography(), orthography));
    }

    public static void addOrthography(Variant v, String orthography) {
        v.setOrthography(addTag(v.getOrthography(), orthography));
    }

    public static String tag(Paradigm p, Variant v) {
        String pt = p.getTag() != null ? p.getTag() : "";
        String vt = v.getTag() != null ? v.getTag() : "";
        return pt + vt;
    }

    public static String tag(Paradigm p, Variant v, Form f) {
        String pt = p.getTag() != null ? p.getTag() : "";
        String vt = v.getTag() != null ? v.getTag() : "";
        return pt + vt + f.getTag();
    }

    /**
     * Checks if value exist in str, where str is list of values, like
     * "value1;value2;value3".
     */
    public static boolean inSeparatedList(CharSequence str, String value) {
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == ';') {
                if (j == value.length()) {
                    return true;
                } else {
                    j = 0;
                }
            } else {
                if (j >= 0 && j < value.length() && c == value.charAt(j)) {
                    j++;
                } else {
                    j = -1;
                }
            }
        }
        if (j == value.length()) {
            return true;
        }
        return false;
    }

    public static String toString(Paradigm p) {
        return "Paradigm: " + p.getTag() + "/" + p.getLemma() + "[" + p.getPdgId() + "]";
    }

    public static String concatNullable(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return null;
        } else if (s1 == null && s2 != null) {
            return s2;
        } else if (s1 != null && s2 == null) {
            return s1;
        } else {
            return s1 + s2;
        }
    }
}
