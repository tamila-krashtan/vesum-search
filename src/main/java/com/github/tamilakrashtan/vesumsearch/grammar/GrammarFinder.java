package com.github.tamilakrashtan.vesumsearch.grammar;

import com.github.tamilakrashtan.vesumsearch.ukrainian.UkrainianWordNormalizer;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrammarFinder {

    private static final int HASHTABLE_SIZE = 256 * 1024;
    private static final Paradigm[] EMPTY = new Paradigm[0];
    private final Paradigm[][] table;

    public GrammarFinder(GrammarDB2 gr) {
        long be = System.currentTimeMillis();
        final List<List<Paradigm>> prepare = new ArrayList<>(HASHTABLE_SIZE);
        for (int i = 0; i < HASHTABLE_SIZE; i++) {
            prepare.add(new ArrayList<>());
        }
        gr.getAllParadigms().parallelStream().forEach(p -> {
            p.getVariant().forEach(v -> {
                putToPrepare(v.getLemma(), prepare, p);
                v.getForm().forEach(f -> {
                    if (f.getValue() != null && !f.getValue().isEmpty()) {
                        putToPrepare(f.getValue(), prepare, p);
                    }
                });
            });
        });
        table = prepareToFinal(prepare);
        long af = System.currentTimeMillis();
        System.out.println("GrammarFinder prepare time: " + (af - be) + "ms");
    }

    private void putToPrepare(String w, List<List<Paradigm>> prepare, Paradigm p) {
        int hash = UkrainianWordNormalizer.hash(w);
        int indexByHash = Math.abs(hash) % HASHTABLE_SIZE;
        List<Paradigm> list = prepare.get(indexByHash);
        synchronized (list) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == p) {
                    return;
                }
            }
            list.add(p);
        }
    }

    private Paradigm[][] prepareToFinal(List<List<Paradigm>> prepare) {
        Paradigm[][] result = new Paradigm[prepare.size()][];
        int maxLen = 0;
        for (int i = 0; i < result.length; i++) {
            List<Paradigm> list = prepare.get(i);
            if (!list.isEmpty()) {
                result[i] = list.toArray(new Paradigm[list.size()]);
                maxLen = Math.max(maxLen, result[i].length);
            }
        }
        System.out.println("GrammarFinder max table tail: " + maxLen);
        return result;
    }

    /**
     * Find paradigms by lemma or form (lower case).
     */
    public List<Paradigm> getParadigms(DBFormService formService, DBLemmaService lemmaService, String word) {

        List<DBForm> matchedForms = formService.findByForm(word);
        Set<Integer> lemmaIds = matchedForms.stream().map(DBForm::getLemmaId).collect(Collectors.toSet());
        //List<DBForm> allMatchedForms = formService.findByLemma_ids(lemmaIds);
        return getParadigmsFromLemmas(lemmaService.findByIds(lemmaIds), matchedForms);
    }

    public List<Paradigm> getParadigmsByRegex(DBFormService formService, DBLemmaService lemmaService, Pattern pattern) {

        List<DBForm> matchedForms = formService.findByFormLike(pattern);
        Set<Integer> lemmaIds = matchedForms.stream().map(DBForm::getLemmaId).collect(Collectors.toSet());
        //List<DBForm> allMatchedForms = formService.findByLemma_ids(lemmaIds);
        return getParadigmsFromLemmas(lemmaService.findByIds(lemmaIds), matchedForms);
    }

    private List<Paradigm> getParadigmsFromLemmas(List<DBLemma> lemmas, List<DBForm> matchedForms) {

        return lemmas.parallelStream().map(l -> this.getParadigmFromLemma(l, matchedForms)).collect(Collectors.toList());
    }

    public Paradigm getParadigmById(DBFormService formService, DBLemmaService lemmaService, int id) {

        Optional<DBLemma> optionalLemma = lemmaService.findById(id);
        return optionalLemma.map(l->getParadigmFromLemma(formService, l)).orElse(null);
    }

    public Paradigm getParadigmFromLemma(DBLemma lemma, List<DBForm> matchedForms) {

        //TODO get rid of the Paradigm and such classes altogether and rewrite this
        Paradigm paradigm = new Paradigm();
        paradigm.setPdgId(lemma.getId());
        paradigm.setLemma(lemma.getLemma());
        paradigm.setTag(lemma.getTags());

        Variant variant = new Variant();
        variant.setLemma(lemma.getLemma());
        paradigm.setVariant(variant);

        List<Form> lemmaForms = new ArrayList<>();
        for (DBForm dbForm : matchedForms.stream().filter(dbForm -> dbForm.getLemmaId() == lemma.getId()).toList()) {
            Form form = new Form();
            form.setValue(dbForm.getForm());
            form.setTag(dbForm.getTags());
            lemmaForms.add(form);
        }
        variant.setForms(lemmaForms);
        return paradigm;
    }

    public Paradigm getParadigmFromLemma(DBFormService formService, DBLemma lemma) {

        //TODO get rid of the Paradigm and such classes altogether and rewrite this
        Paradigm paradigm = new Paradigm();
        paradigm.setPdgId(lemma.getId());
        paradigm.setLemma(lemma.getLemma());
        paradigm.setTag(lemma.getTags());

        Variant variant = new Variant();
        variant.setLemma(lemma.getLemma());
        paradigm.setVariant(variant);

        List<Form> lemmaForms = new ArrayList<>();
        for (DBForm dbForm : formService.findByLemma_id(lemma.getId())) {
            Form form = new Form();
            form.setValue(dbForm.getForm());
            form.setTag(dbForm.getTags());
            lemmaForms.add(form);
        }
        variant.setForms(lemmaForms);
        return paradigm;
    }
}
