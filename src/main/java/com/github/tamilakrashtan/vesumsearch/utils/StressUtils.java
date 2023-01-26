package com.github.tamilakrashtan.vesumsearch.utils;

import com.github.tamilakrashtan.vesumsearch.ukrainian.UkrainianTags;
import com.github.tamilakrashtan.vesumsearch.ukrainian.UkrainianWordNormalizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Some utilities methods for stress processing.
 * 
 * Stress syll index started from 0.
 */
public class StressUtils {

    public static char STRESS_CHAR = UkrainianWordNormalizer.correct_stress;
    public static String STRESS_CHARS = "+\u0301\u00b4";

    public static String unstress(String stressedWord) {
        if (!hasStress(stressedWord)) {
            return stressedWord;
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < stressedWord.length(); i++) {
            char c = stressedWord.charAt(i);
            if (STRESS_CHARS.indexOf(c) < 0) {
                s.append(c);
            }
        }
        return s.toString();
    }

    public static boolean hasStress(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (STRESS_CHARS.indexOf(word.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignable(String destination, String withStress) {
        if (destination.equals(unstress(destination))) {
            return destination.equals(unstress(withStress));
        } else {
            return destination.equals(withStress);
        }
    }

    public static int getStressFromStart(String word) {
        int r = 0;
        for (int i = 0; i < word.length() - 1; i++) {
            char c = word.charAt(i);
            char c1 = word.charAt(i + 1);
            if (STRESS_CHARS.indexOf(c1) >= 0) {
                return r;
            }
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                r++;
            }
        }
        return -1;
    }

    public static List<Integer> getAllStressesFromStart(String word) {
        List<Integer> result = new ArrayList<>();
        int r = 0;
        for (int i = 0; i < word.length() - 1; i++) {
            char c = word.charAt(i);
            char c1 = word.charAt(i + 1);
            if (STRESS_CHARS.indexOf(c1) >= 0) {
                result.add(r);
            }
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                r++;
            }
        }
        return result;
    }

    public static int getStressFromEnd(String word) {
        int r = 0;
        for (int i = word.length() - 1; i >= 0; i--) {
            char c = word.charAt(i);
            if (STRESS_CHARS.indexOf(c) >= 0) {
                return r;
            }
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                r++;
            }
        }
        return -1;
    }

    public static List<Integer> getAllStressesFromEnd(String word) {
        List<Integer> result = new ArrayList<>();
        int r = 0;
        for (int i = word.length() - 1; i >= 0; i--) {
            char c = word.charAt(i);
            if (STRESS_CHARS.indexOf(c) >= 0) {
                result.add(r);
            }
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                r++;
            }
        }
        return result;
    }

    public static String setStressFromStart(String word, int pos) {
        if (pos < 0) {
            return word;
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                if (pos == 0) {
                    return word.substring(0, i + 1) + STRESS_CHAR + word.substring(i + 1);
                } else {
                    pos--;
                }
            }
        }
        return word;
    }

    public static String setStressFromEnd(String word, int pos) {
        if (pos < 0) {
            return word;
        }
        for (int i = word.length() - 1; i >= 0; i--) {
            char c = word.charAt(i);
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                if (pos == 0) {
                    return word.substring(0, i + 1) + STRESS_CHAR + word.substring(i + 1);
                } else {
                    pos--;
                }
            }
        }
        return word;
    }

    public static void checkStress(String word) throws Exception {
        for (String w : word.split("[\\-, \\.]")) {
            int pos = -1;
            int mainStresses = 0;
            while ((pos = w.indexOf(STRESS_CHAR, pos + 1)) >= 0) {
                if (UkrainianTags.VOWELS.indexOf(w.charAt(pos - 1)) < 0) {
                    throw new Exception("Stress not on the vowel");
                }
                mainStresses++;
            }
            if (mainStresses > 1) {
                throw new Exception("Too many main stresses in " + word);
            }
        }
    }

    public static int syllCount(String word) {
        int r = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                r++;
            }
        }
        return r;
    }

    public static char syllVowel(String word, int pos) {
        int r = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                if (pos == r) {
                    return c;
                }
                r++;
            }
        }
        return 0;
    }

    public static String setSyllVowel(String word, int pos, char cr) {
        int r = 0;
        StringBuilder s = new StringBuilder(word);
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            boolean vowel = UkrainianTags.VOWELS.indexOf(c) >= 0;
            if (vowel) {
                if (pos == r) {
                    s.setCharAt(i, cr);
                    return s.toString();
                }
                r++;
            }
        }
        throw new RuntimeException("No syll #" + pos + " in the " + word);
    }

    public static String combineAccute(String word) {
        return word.replace(STRESS_CHAR, '\u0301');
    }
}
