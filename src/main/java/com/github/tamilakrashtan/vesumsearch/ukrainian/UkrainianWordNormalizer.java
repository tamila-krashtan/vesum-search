package com.github.tamilakrashtan.vesumsearch.ukrainian;

import java.util.Locale;

public class UkrainianWordNormalizer {
    public static final Locale UKR = new Locale("uk");
    public static final char correct_stress = '\u0301';
    public static final String all_stresses = correct_stress + "\u00B4";
    public static final char correct_apostrophe = '\u02BC';
    public static final String all_apostrophes = correct_apostrophe + "\'\u2019";
    public static final String letters = all_stresses + all_apostrophes
            + "-йцукенгшщзхїґфівапролджєячсмитьбюЙЦУКЕНГШЩЗХЇҐФІВАПРОЛДЖЄЯЧСМИТЬБЮ";

    private static final int CHARS_LEN = 0x2020;
    // Maximum normalization - turning into general hash or for indexing in
    // Lucene. Requires additional validation.
    private static final char[] SUPERNORMALIZE = new char[CHARS_LEN];
    // Minimum normalization - only stress and apostrophe to a correct form.
    private static final char[] LITENORMALIZE = new char[CHARS_LEN];
    // Turning to lowercase letters
    private static final char[] TOLOWER = new char[CHARS_LEN];
    // Is an uppercase letter?
    private static final boolean[] ISUPPER = new boolean[CHARS_LEN];

    static {
        for (char c = 0; c < CHARS_LEN; c++) {
            if (Character.isLetterOrDigit(c)) {
                SUPERNORMALIZE[c] = Character.toLowerCase(c);
                TOLOWER[c] = Character.toLowerCase(c);
                LITENORMALIZE[c] = c;
            }
            ISUPPER[c] = Character.isUpperCase(c);
        }
        TOLOWER[correct_stress] = correct_stress;
        TOLOWER['\''] = '\'';
        TOLOWER['-'] = '-';

        LITENORMALIZE['ґ'] = 'г'; // ґ -> г
        LITENORMALIZE['Ґ'] = 'Г';
        // Correct apostrophe - 02BC
        LITENORMALIZE['\''] = correct_apostrophe;
        LITENORMALIZE['\u02BC'] = correct_apostrophe;
        LITENORMALIZE['\u2019'] = correct_apostrophe;
        // Stress
        LITENORMALIZE[correct_stress] = correct_stress;
        LITENORMALIZE['\u00B4'] = correct_stress;
        LITENORMALIZE['\u0301'] = correct_stress; // combined accent
        LITENORMALIZE['-'] = '-';
        // search
        LITENORMALIZE['?'] = '?';
        LITENORMALIZE['*'] = '*';

        SUPERNORMALIZE['ґ'] = 'г'; // ґ -> г
        SUPERNORMALIZE['Ґ'] = 'г';
        SUPERNORMALIZE['ў'] = 'у'; // ў -> у
        SUPERNORMALIZE['Ў'] = 'у';
        // Correct apostrophe - 02BC, but we use the Latin-script everywhere
        SUPERNORMALIZE['\''] = correct_apostrophe;
        SUPERNORMALIZE['\u02BC'] = correct_apostrophe;
        SUPERNORMALIZE['\u2019'] = correct_apostrophe;
        SUPERNORMALIZE['-'] = '-';
        SUPERNORMALIZE['?'] = '?';
        SUPERNORMALIZE['*'] = '*';
    }

    public static int hash(String word) {
        if (word == null) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            c = c < SUPERNORMALIZE.length ? SUPERNORMALIZE[c] : 0;
            if (c > 0) {
                result = 31 * result + c;
            }
        }
        return result;
    }

    public static String lightNormalized(CharSequence word) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            c = c < LITENORMALIZE.length ? LITENORMALIZE[c] : 0;
            if (c > 0) {
                str.append(c);
            }
        }
        return str.toString();
    }

    /**
     * Normalization, but allows asterisks - for searching several.
     */
    public static String lightNormalizedWithStars(CharSequence word) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            c = c < LITENORMALIZE.length ? LITENORMALIZE[c] : 0;
            if (c > 0) {
                str.append(c);
            }
        }
        return str.toString();
    }

    public static String superNormalized(String word) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            c = c < SUPERNORMALIZE.length ? SUPERNORMALIZE[c] : 0;
            if (c > 0) {
                str.append(c);
            }
        }
        return str.toString();
    }

    /**
     * Compares words in the database(or the input word) to a word in the text.
     * Checks all "inconsistencies": stress, uppercase letters, apostrophes, ґ
     * 
     * Stresses can be present or absent both in the database and in the text.
     */
    public static boolean equals(String dbWord, String anyWord) {
        byte stressWasEquals = 0;
        byte stressWasMissedInWord = 0;
        byte stressWasMissedInDb = 0;
        for (int iDb = 0, iAny = 0;; iDb++, iAny++) {
            char cDb = iDb < dbWord.length() ? dbWord.charAt(iDb) : Character.MAX_VALUE;
            if (cDb < LITENORMALIZE.length) {
                cDb = LITENORMALIZE[cDb];
            } else if (cDb != Character.MAX_VALUE) {
                cDb = 0;
            }
            char cAny = iAny < anyWord.length() ? anyWord.charAt(iAny) : Character.MAX_VALUE;
            if (cAny < LITENORMALIZE.length) {
                cAny = LITENORMALIZE[cAny];
            } else if (cAny != Character.MAX_VALUE) {
                cAny = 0;
            }
            if (cDb == Character.MAX_VALUE && cAny == Character.MAX_VALUE) {
                return stressWasEquals + stressWasMissedInWord + stressWasMissedInDb <= 1;
            }
            if (cAny == Character.MAX_VALUE || cDb == Character.MAX_VALUE) {
                if (cDb == correct_stress) {
                    stressWasMissedInWord = 1;
                    continue;
                } else if (cAny == correct_stress) {
                    stressWasMissedInDb = 1;
                    continue;
                }
                return false;
            }
            if (cAny == 0 || cDb == 0) {
                return false;
            }
            // first character - maybe capital ?
            boolean vialikiDb = ISUPPER[cDb];
            boolean vialikiAny = ISUPPER[cAny];
            if (vialikiDb && !vialikiAny) {
                return false;
            }
            cDb = TOLOWER[cDb];
            cAny = TOLOWER[cAny];
            if (cDb == correct_stress && cAny == correct_stress) {
                stressWasEquals = 1;
                continue;
            } else if (cDb == correct_stress) {
                stressWasMissedInWord = 1;
                iAny--;
                continue;
            } else if (cAny == correct_stress) {
                stressWasMissedInDb = 1;
                iDb--;
                continue;
            }
            if (cDb != cAny) {
                return false;
            }
        }
    }

    public static boolean isLetter(char c) {
        return letters.indexOf(c) >= 0;
    }
}
