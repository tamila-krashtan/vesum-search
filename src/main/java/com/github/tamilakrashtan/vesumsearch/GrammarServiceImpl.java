package com.github.tamilakrashtan.vesumsearch;

import com.github.tamilakrashtan.vesumsearch.grammar.Form;
import com.github.tamilakrashtan.vesumsearch.grammar.Paradigm;
import com.github.tamilakrashtan.vesumsearch.grammar.Variant;
import com.github.tamilakrashtan.vesumsearch.grammar.DBTagsGroups;
import com.github.tamilakrashtan.vesumsearch.belarusian.BelarusianTags;
import com.github.tamilakrashtan.vesumsearch.belarusian.BelarusianWordNormalizer;
import com.github.tamilakrashtan.vesumsearch.belarusian.FormsReadyFilter;
import com.github.tamilakrashtan.vesumsearch.grammar.GrammarSearchResult;
import com.github.tamilakrashtan.vesumsearch.grammar.LemmaInfo;
import com.github.tamilakrashtan.vesumsearch.server.data.GrammarInitial;
import com.github.tamilakrashtan.vesumsearch.server.KorpusApplication;
import com.github.tamilakrashtan.vesumsearch.server.Settings;
import com.github.tamilakrashtan.vesumsearch.server.WordsDetailsChecks;
import com.github.tamilakrashtan.vesumsearch.utils.SetUtils;
import com.github.tamilakrashtan.vesumsearch.utils.StressUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Service for search by grammar database.
 */
@RestController
@RequestMapping("/grammar")
public class GrammarServiceImpl {

    public static Locale BE = new Locale("be");
    public static Collator BEL = Collator.getInstance(BE);
    private final KorpusApplication korpusApp = new KorpusApplication();

    private KorpusApplication getApp() {
        return korpusApp;
    }

    @GetMapping(value = "/initial", produces = MediaType.APPLICATION_JSON_VALUE)
    public GrammarInitial getInitialData() throws Exception {
        try {
            return getApp().grammarInitial;
        } catch (Exception ex) {
            System.err.println("getInitialData");
            ex.printStackTrace();
            throw ex;
        }
    }

    public static class GrammarRequest {
        public String word;
        public boolean multiForm;
        public String grammar;
        public String outputGrammar;
        public boolean orderReverse;
        public boolean outputGrouping;
        public boolean fullDatabase;
    }


    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public GrammarSearchResult search(@RequestBody GrammarRequest rq) throws Exception {
        System.out.println(">> Request: word=" + rq.word + " orderReverse=" + rq.orderReverse);
        try {
            GrammarSearchResult result = new GrammarSearchResult();
            if (rq.word != null) {
                rq.word = rq.word.trim();
            }
            if (rq.word.isEmpty()) {
                rq.word = null;
            }
            if (rq.grammar != null && rq.grammar.isEmpty()) {
                rq.grammar = null;
            }
            int grammarCount = 0;
            int lettersCount = 0;
            boolean hasStar = false;
            if (rq.word != null) {
                hasStar = WordsDetailsChecks.needWildcardRegexp(rq.word);
                for (char c : rq.word.toCharArray()) {
                    if ("абвгґдеєжзиіїйклмнопрстуфхцчшщьюяїи'".indexOf(Character.toLowerCase(c)) >= 0) {
                        lettersCount++;
                    }
                }
            }
            if (rq.grammar != null) {
                grammarCount = rq.grammar.replace(".", "").length();
            }
            if (grammarCount > 0) {
                // ok
            } else if (hasStar && lettersCount >= 2) {
                // ok
            } else if (!hasStar && lettersCount > 0) {
                // ok
            } else {
                result.error = "Введіть слово для пошуку або укладіть граматику";
                return result;
            }
            Pattern reGrammar = null, reOutputGrammar = null;
            if (rq.grammar != null && !rq.grammar.isEmpty()) {
                reGrammar = WordsDetailsChecks.getPatternRegexp(rq.grammar);
            }
            if (rq.outputGrammar != null && !rq.outputGrammar.isEmpty()) {
                reOutputGrammar = WordsDetailsChecks.getPatternRegexp(rq.outputGrammar);
            }
            Stream<LemmaInfo> output;
            if (rq.word == null || WordsDetailsChecks.needWildcardRegexp(rq.word)) {
                output = StreamSupport.stream(new SearchWidlcards(rq.word, reGrammar, rq.multiForm, rq.fullDatabase, reOutputGrammar),
                        false);
            } else {
                output = searchExact(rq.word, reGrammar, rq.multiForm, rq.fullDatabase, reOutputGrammar);
            }
            result.output = output.limit(Settings.GRAMMAR_SEARCH_RESULT_PAGE).collect(Collectors.toList());

            // remove duplicates
            Collections.sort(result.output, (a, b) -> {
                int r = Long.compare(a.pdgId, b.pdgId);
                if (r == 0) {
                    r = BEL.compare(a.output, b.output);
                }
                return r;
            });
            for (int i = 1; i < result.output.size(); i++) {
                LemmaInfo a = result.output.get(i - 1);
                LemmaInfo b = result.output.get(i);
                if (a.pdgId == b.pdgId && a.output.equals(b.output)) {
                    result.output.remove(i);
                    i--;
                }
            }
            for (int i = 1; i < result.output.size(); i++) {
                LemmaInfo a = result.output.get(i - 1);
                LemmaInfo b = result.output.get(i);
                if (a.pdgId == b.pdgId) {
                    result.hasDuplicateParadigms = true;
                    break;
                }
            }
            if (rq.outputGrouping) {
                for (int i = 1; i < result.output.size(); i++) {
                    LemmaInfo a = result.output.get(i - 1);
                    LemmaInfo b = result.output.get(i);
                    if (a.pdgId == b.pdgId) {
                        a.output += ", " + b.output;
                        a.meaning = null;
                        b.meaning = null;
                        result.output.remove(i);
                        i--;
                    }
                }
                Collections.sort(result.output, new Comparator<LemmaInfo>() {
                    @Override
                    public int compare(LemmaInfo o1, LemmaInfo o2) {
                        return BEL.compare(o1.output, o2.output);
                    }
                });
            } else if (rq.orderReverse) {
                Collections.sort(result.output, new Comparator<LemmaInfo>() {
                    @Override
                    public int compare(LemmaInfo o1, LemmaInfo o2) {
                        return BEL.compare(revert(o1.output), revert(o2.output));
                    }
                });
            } else {
                Collections.sort(result.output, new Comparator<LemmaInfo>() {
                    @Override
                    public int compare(LemmaInfo o1, LemmaInfo o2) {
                        return BEL.compare(o1.output, o2.output);
                    }
                });
            }

            if (result.output.isEmpty()) { // nothing found
                if (rq.word != null && rq.grammar == null && !rq.multiForm
                        && !WordsDetailsChecks.needWildcardRegexp(rq.word)) {// simple word search
                    Stream<LemmaInfo> output2 = searchExact(rq.word, null, true, rq.fullDatabase, null);
                    if (output2.anyMatch(p -> true)) {
                        result.hasMultiformResult = true;
                    }
                }
            }
            System.out.println("<< Result: " + result.output.size());
            return result;
        } catch (Throwable ex) {
            System.out.println(ex);
            throw ex;
        }
    }

    class SearchWidlcards extends Spliterators.AbstractSpliterator<LemmaInfo> {
        private final Pattern re;
        private final Pattern reGrammar;
        private final boolean multiform;
        private final boolean fullDatabase;
        private final Pattern reOutputGrammar;
        private final LinkedList<LemmaInfo> buffer = new LinkedList<>();
        private final List<Paradigm> data;
        private int dataPos;

        public SearchWidlcards(String word, Pattern reGrammar, boolean multiform, boolean fullDatabase,
                Pattern reOutputGrammar) {
            super(Long.MAX_VALUE, 0);
            this.re = word == null ? null : WordsDetailsChecks.getWildcardRegexp(word.trim());
            this.reGrammar = reGrammar;
            this.multiform = multiform;
            this.fullDatabase = fullDatabase;
            this.reOutputGrammar = reOutputGrammar;
            data = getApp().gr.getAllParadigms();
        }

        @Override
        public boolean tryAdvance(Consumer<? super LemmaInfo> action) {
            while (dataPos < data.size() && buffer.isEmpty()) {
                Paradigm p = data.get(dataPos);
                dataPos++;
                createLemmaInfoFromParadigm(p, s -> re == null || re.matcher(StressUtils.unstress(s)).matches(),
                        multiform, fullDatabase, reOutputGrammar, reGrammar, buffer);
            }
            if (buffer.isEmpty()) {
                return false;
            }
            action.accept(buffer.removeFirst());
            return true;
        }
    }

    private Stream<LemmaInfo> searchExact(String word, Pattern reGrammar, boolean multiform, boolean fullDatabase,
            Pattern reOutputGrammar) {
        String normWord = BelarusianWordNormalizer.lightNormalized(word.trim());
        Paradigm[] data = getApp().grFinder.getParadigms(normWord);
        List<LemmaInfo> result = new ArrayList<>();
        for (Paradigm p : data) {
            createLemmaInfoFromParadigm(p, s -> BelarusianWordNormalizer.equals(normWord, s), multiform, fullDatabase,
                    reOutputGrammar, reGrammar, result);
        }
        return result.stream();
    }

    private void createLemmaInfoFromParadigm(Paradigm p, Predicate<String> checkWord, boolean multiform, boolean fullDatabase,
            Pattern reOutputGrammar, Pattern reGrammar, List<LemmaInfo> result) {
        Set<String> found = new TreeSet<>();
        for (Variant v : p.getVariant()) {
            List<Form> forms = fullDatabase ? v.getForm()
                    : FormsReadyFilter.getAcceptedForms(FormsReadyFilter.MODE.SHOW, p, v);
            if (forms == null) {
                return;
            }
            if (multiform) {
                for (Form f : forms) {
                    if (checkWord.test(f.getValue())) {
                        if (reGrammar != null
                                && !reGrammar.matcher(DBTagsGroups.getDBTagString(SetUtils.tag(p, v, f))).matches()) {
                            continue;
                        }
                        found.add(f.getValue());
                    }
                }
            } else {
                if (checkWord.test(v.getLemma())) {
                    if (reGrammar != null) {
                        boolean tagFound = false;
                        for (Form f : forms) {
                            System.out.println(f.getValue());
                            if (reGrammar.matcher(DBTagsGroups.getDBTagString(SetUtils.tag(p, v, f))).matches()) {
                                tagFound = true;
                                break;
                            }
                        }
                        if (!tagFound) {
                            continue;
                        }
                    }
                    found.add(v.getLemma());
                }
            }
            for (String f : found) {
                createLemmaInfo(p, v, f, reOutputGrammar, result);
            }
        }
    }

    private void createLemmaInfo(Paradigm p, Variant v, String output, Pattern reOutputGrammar,
            List<LemmaInfo> result) {
    	String tag = SetUtils.tag(p, v);
        if (reOutputGrammar != null) {
            Set<String> found = new TreeSet<>();
            for (Form f : v.getForm()) {
                if (reOutputGrammar.matcher(DBTagsGroups.getDBTagString(SetUtils.tag(p, v, f))).matches()) {
                    found.add(f.getValue());
                }
            }
            for (String f : found) {
                LemmaInfo w = new LemmaInfo();
                w.pdgId = p.getPdgId();
                w.meaning = p.getMeaning();
                w.output = StressUtils.combineAccute(f);
                w.grammar = String.join(", ",
                        BelarusianTags.getInstance().describe(tag, getApp().grammarInitial.skipGrammar.get(tag.charAt(0))));
                result.add(w);
            }
        } else {
            LemmaInfo w = new LemmaInfo();
            w.pdgId = p.getPdgId();
            w.meaning = p.getMeaning();
            w.output = StressUtils.combineAccute(output);
            w.grammar = String.join(", ",
                    BelarusianTags.getInstance().describe(tag, getApp().grammarInitial.skipGrammar.get(tag.charAt(0))));
            result.add(w);
        }
    }

    @GetMapping(value = "/details/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LemmaInfo.LemmaParadigm getLemmaDetails(@PathVariable(name="id") long pdgId) throws Exception {
        try {
            for (Paradigm p : getApp().gr.getAllParadigms()) {
                if (p.getPdgId() == pdgId) {
                    return conv(p, false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }

    @GetMapping(value = "/detailsFull/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LemmaInfo.LemmaParadigm getLemmaFullDetails(@RequestParam(name="id", required=true) long pdgId) throws Exception {
        try {
            for (Paradigm p : getApp().gr.getAllParadigms()) {
                if (p.getPdgId() == pdgId) {
                    return conv(p, true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }

    @GetMapping(value = "lemmas/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String[] getLemmasByForm(@RequestParam(name="form", required=true) String form) throws Exception {
        System.out.println(">> Find lemmas by form " + form);
        Set<String> result = Collections.synchronizedSet(new TreeSet<>());
        try {
            form = BelarusianWordNormalizer.lightNormalized(form);
            for (Paradigm p : getApp().grFinder.getParadigms(form)) {
                for (Variant v : p.getVariant()) {
                    for (Form f : v.getForm()) {
                        if (BelarusianWordNormalizer.equals(f.getValue(), form)) {
                            result.add(p.getLemma());
                            break;
                        }
                    }
                }
            }
            System.out.println("<< Find lemmas by form result: " + result);
            List<String> resultList = new ArrayList<>(result);
            Collections.sort(resultList, BEL);
            return resultList.toArray(new String[result.size()]);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    LemmaInfo.LemmaParadigm conv(Paradigm p, boolean fullDatabase) {
        LemmaInfo.LemmaParadigm r = new LemmaInfo.LemmaParadigm();
        r.lemma = StressUtils.combineAccute(p.getLemma());
        r.tag = p.getTag();
        r.meaning = p.getMeaning();
        for (Variant v : p.getVariant()) {
            List<Form> forms = fullDatabase ? v.getForm()
                    : FormsReadyFilter.getAcceptedForms(FormsReadyFilter.MODE.SHOW, p, v);
            if (forms == null) {
                continue;
            }
            LemmaInfo.LemmaVariant rv = new LemmaInfo.LemmaVariant();
            rv.id = v.getId();
            rv.tag = v.getTag();
            rv.dictionaries.addAll(SetUtils.getSlouniki(v.getSlouniki()).keySet());
            r.variants.add(rv);
            for (Form f : forms) {
                LemmaInfo.LemmaForm rf = new LemmaInfo.LemmaForm();
                rf.value = StressUtils.combineAccute(f.getValue());
                rf.tag = f.getTag();
                rf.options = f.getOptions() != null ? f.getOptions().name() : null;
                rf.type = f.getType() != null ? f.getType().name() : null;
                rv.dictionaries.addAll(SetUtils.getSlouniki(f.getSlouniki()).keySet());
                rv.forms.add(rf);
            }
        }
        return r;
    }

    String revert(String s) {
        StringBuilder r = new StringBuilder(s);
        return r.reverse().toString();
    }
}
