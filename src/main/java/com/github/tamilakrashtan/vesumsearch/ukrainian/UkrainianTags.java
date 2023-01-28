package com.github.tamilakrashtan.vesumsearch.ukrainian;

import com.github.tamilakrashtan.vesumsearch.ukrainian.TagLetter.OneLetterInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Grammar marks are shown in the following placecs:
 * 1) Grammar filter for a word - checkboxes.
 * 2) Showing grammar tables for a word.
 * 3) Showing grammar characteristics for one word.
 */
public class UkrainianTags {
    public static final String NO_GROUP_ITEM = "not used";
    public static final String VOWELS = "уеїіаоєяиюУЕЇІАОЄЯИЮ";

    private static UkrainianTags INSTANCE = new UkrainianTags();

    public static UkrainianTags getInstance() {
        return INSTANCE;
    }

    private TagLetter root;

    public static void main(String[] a) {
        INSTANCE.isValidParadigmTag("VTPN1", "err p");
        INSTANCE.isValidFormTag("VTPN1PG", "err f");
    }

    private UkrainianTags() {
        root = new TagLetter();

        noun(root);
        numeral(root);
        //pronoun(root);
        adjective(root);
        verb(root);
        //participle(root);
        adverb(root);
        conjunction(root);
        preposition(root);
        particle(root);
        interjection(root);
        other(root);
        //predicative(root);
        //abbreviation(root);
        //parts(root);

        checkParadigmMarks(root, "", 0);
        checkDuplicateGroups(root, new ArrayList<>());
        // check which groups are not used
    }

    /**
     * searching in all tags count(latestInParadigm)==1
     */
    private void checkParadigmMarks(TagLetter tl, String code, int pmCount) {
        if (tl.isLatestInParadigm()) {
            pmCount++;
        }
        if (tl.isFinish()) {
            if (pmCount != 1) {
                throw new RuntimeException("pmCount=" + pmCount + " for " + code);
            }
        } else {
            for (OneLetterInfo letterInfo : tl.letters) {
                checkParadigmMarks(letterInfo.nextLetters, code + letterInfo.letter, pmCount);
            }
        }
    }

    /**
     * Checks whether there are any duplicate groups in the hierarchy
     */
    private void checkDuplicateGroups(TagLetter tl, List<String> path) {
        for (OneLetterInfo li : tl.letters) {
            if (path.contains(li.groupName) && !"Невідомо".equals(li.groupName)) {
                throw new RuntimeException("Duplicate group '" + li.groupName + "' in " + path);
            }
            path.add(li.groupName);
            checkDuplicateGroups(li.nextLetters, path);
            path.remove(path.size() - 1);
        }
    }

    /**
     * Is the paradigm tag correct? latestInParadigm==true
     */
    public boolean isValidParadigmTag(String code, String w) {
        TagLetter after = getTagLetterAfter(code, w);
        if (after == null) {
            return false;
        }
        if (!after.isLatestInParadigm()) {
            if (w != null) {
                System.out.println(code + " " + w + " - wrong paradigm tag");
            }
            return false;
        }
        return true;
    }

    public boolean isValidFormTag(String code, String w) {
        TagLetter after = getTagLetterAfter(code, w);
        if (after == null) {
            return false;
        }
        if (!after.isFinish()) {
            if (w != null) {
                System.out.println(code + " " + w + " - code too small");
            }
            return false;
        }
        return true;
    }

    private TagLetter getTagLetterAfter(String code, String w) {
        TagLetter tags = root;
        for (char c : code.toCharArray()) {
            if (c == 'x') { // TODO
                if (tags.isFinish()) {
                    if (w != null) {
                        System.out.println(code + " " + w + " - too many letters in the code");
                    }
                    return null;
                }
                TagLetter first = tags.letters.get(0).nextLetters;
                for (TagLetter.OneLetterInfo li : tags.letters) {
                    if (li.nextLetters != first) {
                        if (w != null) {
                            System.out.println(code + " " + w + " - unclear how to decode");
                        }
                        return null;
                    }
                }
                tags = first;
            } else {
                tags = tags.next(c);
                if (tags == null) {
                    if (w != null) {
                        System.out.println(code + " " + w + " - unknown letters in the code");
                    }
                    return null;
                }
            }
        }
        return tags;
    }

    public TagLetter getRoot() {
        return root;
    }

    public TagLetter getNextAfter(String codeBegin) {
        TagLetter tags = root;
        for (char c : codeBegin.toCharArray()) {
            tags = tags.next(c);
            if (tags == null) {
                throw new RuntimeException("Error code: " + codeBegin);
            }
        }
        return tags;
    }

    public List<String> describe(String codeBegin, Set<String> excludeGroups) {
        List<String> result = new ArrayList<String>();
        TagLetter tags = root;
        for (char c : codeBegin.toCharArray()) {
            OneLetterInfo info = tags.getLetterInfo(c);
            if (info == null) {
                throw new RuntimeException("Wrong tag: " + codeBegin);
            }
            tags = tags.next(c);
            if (tags == null) {
                throw new RuntimeException("Wrong tag: " + codeBegin);
            }
            if (excludeGroups == null || !excludeGroups.contains(info.groupName)) {
                result.add(info.description);
            }
        }
        return result;
    }

    public char getValueOfGroup(String code, String group) {
        TagLetter tags = root;
        for (char c : code.toCharArray()) {
            OneLetterInfo li = tags.getLetterInfo(c);
            if (li == null) {
                throw new RuntimeException("Wrong tag: " + code);
            }
            if (group.equals(li.groupName)) {
                return li.letter;
            }
            tags = tags.next(c);
            if (tags == null) {
                throw new RuntimeException("Wrong tag: " + code);
            }
        }
        return 0;
    }

    public String setValueOfGroup(String code, String group, char newValue) {
        TagLetter tags = root;
        for (int i=0;i<code.length();i++) {
            char c= code.charAt(i);
            OneLetterInfo li = tags.getLetterInfo(c);
            if (li == null) {
                throw new RuntimeException("Wrong tag: " + code);
            }
            if (group.equals(li.groupName)) {
                return code.substring(0, i) + newValue + code.substring(i + 1);
            }
            tags = tags.next(c);
            if (tags == null) {
                throw new RuntimeException("Wrong tag: " + code);
            }
        }
        return null;
    }

    private void noun(TagLetter t) {
        t = t.add("Частина мови => N:іменник");
        t.add("Новий=>+:новий").latestInParadigm();
        t = t.add("Власна назва => C:загальна назва;P:власна назва;X:-");
        t = t.add("Істота => A:істота;I:неістота;X:-");
        //t = t.add("Асабовасць => P:асабовы;I:неасабовы;X:-");
        t = t.add("Абревіатура => B:абревіатура;N:не абревіатура;X:-");

        TagLetter z = t.add("Рід => M:чоловічий рід;F:жіночий рід;N:середній рід род;X:-");
//        z = z.add(
//                "Скланенне => 1:1-е скланенне;2:2-е скланенне;3:3-е скланенне;0:нескланяльны;4:рознаскланяльны;6:змешанае скланенне;X:-")
//                .latestInParadigm();
        z = z.add("Відмінок => N:називний відмінок;G:родовий відмінок;D:давальний відмінок;A:знахідний відмінок;I:орудний відмінок;L:місцевий відмінок;V:кличний відмінок");
        z = z.add("Число => S:однина;P:множина").latestInParadigm();;

        TagLetter p = t.add("Множинні => P:множина");
//        p = p.add("Скланенне => 0:нескланяльны;7:множналікавы").latestInParadigm();
        p = p.add("Відмінок => N:називний відмінок;G:родовий відмінок;D:давальний відмінок;A:знахідний відмінок;I:орудний відмінок;L:місцевий відмінок;V:кличний відмінок");
        p = p.add("Число => S:однина;P:множина").latestInParadigm();;

//        TagLetter su = t.add("Субстантываванасць => S:субстантываваны;U:субстантываваны множналікавы");
//        su = su.add("Скланенне => 5:ад’ектыўнае скланенне").latestInParadigm();
//        su = su.add("Род => M:мужчынскі род;F:жаночы род;N:ніякі род;P:адсутнасць роду ў множным ліку;X:-"); // для субстантываваных
//        su = su.add("Склон => N:назоўны склон;G:родны склон;D:давальны склон;A:вінавальны склон;I:творны склон;L:месны склон;V:клічны склон");
//        su = su.add("Лік => S:адзіночны лік;P:множны лік");
    }

    private void numeral(TagLetter t) {

        t = t.add("Частина мови => M:числівник").latestInParadigm();

//        TagLetter t0 = t.add("Часціна мовы => M:лічэбнік");
//        t = t0.add("Словазмяненне => N:словазмяненне як у назоўніка;A:словазмяненне як у прыметніка;X:-");
//        TagLetter t2=t0.add("Словазмяненне => 0:няма словазмянення");
//        t = t.add("Значэнне => C:колькасны;O:парадкавы;K:зборны;F:дробавы");
//        t2 = t2.add("Значэнне => C:колькасны;O:парадкавы;K:зборны;F:дробавы");
//        t = t.add("Форма => S:просты;C:складаны").latestInParadigm();
//        t2 = t2.add("Форма => S:просты;C:складаны").latestInParadigm();
//
//        TagLetter z = t.add("Род => M:мужчынскі род;F:жаночы род;N:ніякі род;P:няма;X:-");
//        t2.add("Нескланяльны => 0:нескланяльны");
//        z = z.add("Склон => N:назоўны склон;G:родны склон;D:давальны склон;A:вінавальны склон;I:творны склон;L:месны склон;X:-");
//        z = z.add("Лік => S:адзіночны лік;P:множны лік;X:-");
    }

    private void pronoun(TagLetter t) {
        t = t.add("Частина мови => S:займенник");
        t = t.add("Словазмяненне => N:N:словазмяненне як у назоўніка;A:N:словазмяненне як у прыметніка;0:нязменны");
        t = t.add(
                "Разрад => P:асабовы;R:зваротны;S:прыналежны;D:указальны;E:азначальны;L:пытальна-адносны;N:адмоўны;F:няпэўны");
        t = t.add("Асоба => 1:1-я асоба;2:2-я асоба;3:3-я асоба;0:безасабовы;X:-").latestInParadigm();

        TagLetter z = t.add("Род => M:мужчынскі род;F:жаночы род;N:ніякі род;X:-;0:-");
        t.add("Формы => 1:-");
        z = z.add("Склон => N:назоўны склон;G:родны склон;D:давальны склон;A:вінавальны склон;I:творны склон;L:месны склон;X:-");
        z = z.add("Лік => S:адзіночны лік;P:множны лік;X:-");
    }

    private void adjective(TagLetter t) {
        t = t.add("Частина мови => A:прикметник");
        t.add("Тип => 0:невідмінюваний").latestInParadigm();
        //t = t.add("Тып => Q:якасны;R:адносны;P:прыналежны;X:-");
        TagLetter a = t.add("Ступінь порівняння => P:базова форма;C:порівняльна форма;S:найвища форма").latestInParadigm();

        //a.add("Прикметник у функцыі прыслоўя => R:прыслоў'е");
        t = a.add("Рід => M:чоловічий рід;F:жіночий рід;N:середній рід;P:множина;X:-");
        t = t.add("Відмінок => N:називний відмінок;G:родовий відмінок;D:давальний відмінок;A:знахідний відмінок;I:орудний відмінок;L:місцевий відмінок;V:кличний відмінок");
        t = t.add("Число => S:однина;P:множина");
    }

    private void verb(TagLetter t) {
        t = t.add("Частина мови => V:дієслово");
        t.add("Новий=>+:новий").latestInParadigm();
        //t = t.add("Перехідність => T:перехідний;I:неперехідний;X:-");
        t = t.add("Доконаність => P:доконане;M:недоконане;X:-");
        t = t.add("Зворотність => R:зворотне;N:незворотне").latestInParadigm();;
        //t = t.add("Спражэнне => 1:1-е спражэнне;2:2-е спражэнне;3:рознаспрагальны;X:-").latestInParadigm();

        TagLetter casR = t.add("Час => R:теперішній час");
        TagLetter casM = t.add("Час => P:минулий час");
        TagLetter casO = t.add("Час => F:майбутній час");
        TagLetter zah = t.add("Наказовий спосіб => I:наказовий спосіб");
        t.add("Інфінітив => 0:інфінітив");
        t.add("Невідомо => X:-").add("Невідомо => X:-").add("Невідомо => X:-")
                .add("Невідомо => X:-");

        TagLetter casRL = casR.add("Особа => 1:1-а особа;2:2-а особа;3:3-я особа;0:безособове");
        //casR.add("Дзеепрыслоўе => G:дзеепрыслоўе");
        //casM.add("Дзеепрыслоўе => G:дзеепрыслоўе");
        TagLetter casOL = casO.add("Особа => 1:1-а особа;2:2-а особа;3:3-я особа;0:безособове");
        //casO.add("Дзеепрыслоўе => G:дзеепрыслоўе");
        zah = zah.add("Особа => 1:1-а особа;2:2-а особа;3:3-я особа;0:безособове");

        casRL = casRL.add("Число => S:однина;P:множина");
        casOL = casOL.add("Число => S:однина;P:множина");
        zah = zah.add("Число => S:однина;P:множина");

        casM = casM.add("Рід => M:чоловічий рід;F:жіночий рід;N:середній рід;X:-");
        casM = casM.add("Число => S:однина;P:множина");
    }

    private void participle(TagLetter t) {
        t = t.add("Часціна мовы => P:дзеепрыметнік");
        t = t.add("Стан => A:незалежны стан;P:залежны стан");
        t = t.add("Час => R:цяперашні час;P:прошлы час");
        TagLetter pt = t.add("Трыванне => P:закончанае трыванне;M:незакончанае трыванне;X:-")
                .latestInParadigm();

        t = pt.add("Род => M:мужчынскі род;F:жаночы род;N:ніякі род;P:множны лік;X:-");
        t = t.add("Склон => N:назоўны склон;G:родны склон;D:давальны склон;A:вінавальны склон;I:творны склон;L:месны склон;H:-");
        t = t.add("Лік => S:адзіночны лік;P:множны лік;X:-");
        pt.add("Кароткая форма => R:ж. і н.");
    }

    private void adverb(TagLetter t) {
        t = t.add("Частина мовы => R:прислівник");
        t.add("Новий=>+:новий").latestInParadigm();
//        t = t.add(
//                "Спосаб утварэння => N:утворана ад назоўніка;A:утворана ад прыметніка;M:утворана ад лічэбніка;S:утворана ад займенніка;G:утворана ад дзеепрыслоўя;V:утворана ад дзеяслова;E:утворана ад часціцы;I:утворана ад прыназоўніка;X:-")
//                .latestInParadigm();

        t = t.add("Ступінь порівняння => P:базова форма;C:порівняльна форма;S:найвища форма").latestInParadigm();;
    }

    private void conjunction(TagLetter t) {
        t = t.add("Частина мови => C:сполучник");
        TagLetter s = t.add("Тип => S:підрядний").latestInParadigm();;
        TagLetter k = t.add("Тип => K:сурядний").latestInParadigm();;
//        t.add("Тып => P:паясняльны").latestInParadigm();
//        s.add("Падпарадкавальны => B:прычынны;C:часавы;D:умоўны;F:мэтавы;G:уступальны;H:параўнальны;K:следства;X:-")
//                .latestInParadigm();
//        k.add("Злучальны => A:спалучальны;E:супастаўляльны;O:пералічальна-размеркавальны;L:далучальны;U:градацыйны;X:-")
//                .latestInParadigm();
    }

    private void preposition(TagLetter t) {
        t.add("Частина мови => I:прийменник").latestInParadigm();
    }

    private void particle(TagLetter t) {
        t.add("Частина мови => E:частка").latestInParadigm();
    }

    private void interjection(TagLetter t) {
        t.add("Частина мови => Y:вигук").latestInParadigm();
    }

    private void other(TagLetter t) {
        t.add("Частина мови => Z:інше").latestInParadigm();
    }

    private void predicative(TagLetter t) {
        t.add("Частина мови => W:прэдыкатыў").latestInParadigm();
    }

    private void abbreviation(TagLetter t) {
        t = t.add("Частина мови => K:абрэвіятуры").latestInParadigm();
    }

    private void parts(TagLetter t) {
        t = t.add("Частина мови => F:частка слова").latestInParadigm();
//        t.add("Тып => P:прыстаўка;F:1-я састаўная частка складаных слоў;S:2-я састаўная частка складаных слоў")
//                .latestInParadigm();
    }
}
