package com.github.tamilakrashtan.vesumsearch.ukrainian;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TagLetter {

    public List<OneLetterInfo> letters = new ArrayList<OneLetterInfo>();

    /** true if letter must be latest in paradigm */
    private boolean latestInParadigm;

    /**
     * Add child info like:
     * 
     * Part => A:Noun;V:Verb;...
     */
    public TagLetter add(String text) {
        int pos = text.indexOf("=>");
        if (pos <= 0) {
            throw new RuntimeException("Error in add: " + text);
        }
        String groupName = text.substring(0, pos).trim();
        String values = text.substring(pos + 2).trim();

        TagLetter c = new TagLetter();
        for (String v : values.split(";")) {
            char code = v.charAt(0);
            if (!(code >= 'A' && code <= 'Z') && !(code >= '0' && code <= '9') && !(code == '+')) {
                throw new RuntimeException("Error in letters: " + values);
            }
            if (v.charAt(1) != ':') {
                throw new RuntimeException("Error in letters: " + values);
            }
            OneLetterInfo newLetter = new OneLetterInfo();
            newLetter.groupName = groupName;
            newLetter.letter = code;
            newLetter.description = v.substring(2);
            newLetter.nextLetters = c;
            for (OneLetterInfo li : letters) {
                if (li.letter == newLetter.letter) {
                    throw new RuntimeException("Already exist in letters: " + values);
                }
            }
            letters.add(newLetter);
        }
        return c;
    }

    public boolean isLatestInParadigm() {
        return latestInParadigm;
    }

    public TagLetter latestInParadigm() {
        latestInParadigm = true;
        return this;
    }

    public TagLetter next(char c) {
        for (OneLetterInfo li : letters) {
            if (li.letter == c) {
                return li.nextLetters;
            }
        }
        return null;
    }

    public String getLetterDescription(char c) {
        for (OneLetterInfo li : letters) {
            if (li.letter == c) {
                return li.description;
            }
        }
        return null;
    }

    public OneLetterInfo getLetterInfo(char c) {
        for (OneLetterInfo li : letters) {
            if (li.letter == c) {
                return li;
            }
        }
        return null;
    }

    public String getNextGroupNames() {
        if (letters.isEmpty()) {
            return null;
        }
        Set<String> uniqNames = new TreeSet<String>();
        for (OneLetterInfo li : letters) {
            uniqNames.add(li.groupName);
        }
        String t = "";
        for (String n : uniqNames) {
            t += " / " + n;
        }
        return t.substring(3);
    }

    public boolean isFinish() {
        return letters.isEmpty();
    }

    public static class OneLetterInfo {
        public String groupName;
        public char letter;
        public String description;
        public TagLetter nextLetters;
    }
}
