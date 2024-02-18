package com.github.tamilakrashtan.vesumsearch.server;

import com.github.tamilakrashtan.vesumsearch.grammar.*;
import com.github.tamilakrashtan.vesumsearch.ukrainian.UkrainianTags;
import com.github.tamilakrashtan.vesumsearch.ukrainian.TagLetter;
import com.github.tamilakrashtan.vesumsearch.server.GrammarInitial.GrammarLetter;
import com.github.tamilakrashtan.vesumsearch.utils.SetUtils;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GrammarApplication extends ResourceConfig {
    public String grammarDb;

    List<String> settings;
    Properties stat;
    public GrammarDB2 gr;
    public GrammarFinder grFinder;
    private Map<String, Map<String,String>> localization;
    public GrammarInitial grammarInitial;


    public static GrammarApplication instance;
    public ResourceBundle messagesEn, messagesUk;

    public GrammarApplication() {
        instance = this;
        messagesUk = ResourceBundle.getBundle("messages", new Locale("uk"));
        messagesEn = ResourceBundle.getBundle("messages", new Locale("en"));

        System.out.println("Starting...");
        try {
            //grammarDb = "artifact/DictionaryDB";
            grammarDb = "src/main/resources/DictionaryDB";
            settings = new ArrayList<>();

            gr = GrammarDB2.empty();
            System.out.println("GrammarDB loaded with " + gr.getAllParadigms().size() + " paradigms. Used memory: "
                    + getUsedMemory());
            grFinder = new GrammarFinder(gr);
            System.out.println("GrammarDB indexed. Used memory: " + getUsedMemory());

            localization = new TreeMap<>();
            prepareLocalization("uk", messagesUk);
            prepareLocalization("en", messagesEn);
            prepareInitialGrammar();
            System.out.println("Initialization finished. Used memory: " + getUsedMemory());
        } catch (Throwable ex) {
            System.err.println("Startup error");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    private String getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        runtime.gc();
        runtime.gc();
        return Math.round((runtime.totalMemory() - runtime.freeMemory()) / 1024.0 / 1024.0) + "mb";
    }

    void prepareLocalization(String lang, ResourceBundle messages) {
        Map<String, String> lines = new HashMap<>();
        messages.getKeys().asIterator().forEachRemaining(key -> lines.put(key, messages.getString(key)));
        localization.put(lang, lines);
    }

    void prepareInitialGrammar() throws Exception {
        grammarInitial = new GrammarInitial();
        grammarInitial.grammarTree = new TreeMap<>();
        grammarInitial.grammarTree = addGrammar(UkrainianTags.getInstance().getRoot());
        grammarInitial.grammarWordTypes = DBTagsGroups.wordTypes;
        grammarInitial.grammarWordTypesGroups = DBTagsGroups.tagGroupsByWordType;
        grammarInitial.localization = localization;

        grammarInitial.skipGrammar = new TreeMap<>();
        for (String line : settings) {
            if (line.startsWith("grammar.skip.") && line.charAt(14) == '=') {
                char part = line.charAt(13);
                Set<String> vs = Arrays.stream(line.substring(15).split(";")).map(s -> s.trim())
                        .collect(Collectors.toSet());
                grammarInitial.skipGrammar.put(part, vs);
            }
        }
        grammarInitial.dictionaries = new ArrayList<>();
        for (String d : Files.readAllLines(Paths.get(grammarDb + "/dictionaries.list"))) {
            GrammarInitial.GrammarDict dict = new GrammarInitial.GrammarDict();
            int p = d.indexOf('=');
            if (p < 0 || !d.substring(0, p).matches("[a-z0-9]+")) {
                throw new Exception("Wrong dictionary name format: " + d);
            }
            dict.name = d.substring(0, p);
            dict.desc = d.substring(p + 1);
            grammarInitial.dictionaries.add(dict);
        }
        grammarInitial.stat = new ArrayList<>();
        GrammarInitial.Stat grStatTotal = new GrammarInitial.Stat();
        grammarInitial.stat.add(grStatTotal);
        Map<Character, GrammarInitial.Stat> grStats = new TreeMap<>();
        for (TagLetter.OneLetterInfo li : UkrainianTags.getInstance().getRoot().letters) {
            GrammarInitial.Stat gs = new GrammarInitial.Stat();
            gs.title = "&nbsp;&nbsp;&nbsp;&nbsp;" + li.description;
            grammarInitial.stat.add(gs);
            grStats.put(li.letter, gs);
        }
        gr.getAllParadigms().parallelStream().forEach(p -> {
            int formsInParadigm = p.getVariant().stream().mapToInt(v -> v.getForm().size()).sum();
            synchronized (grStatTotal) {
                grStatTotal.paradigmCount++;
                grStatTotal.formCount += formsInParadigm;
            }
            p.getVariant().stream().map(v -> SetUtils.tag(p, v).charAt(0)).sorted().distinct().forEach(c -> {
                GrammarInitial.Stat st = grStats.get(c);
                synchronized (st) {
                    st.paradigmCount++;
                }
            });
            for (Variant v : p.getVariant()) {
                char c = SetUtils.tag(p, v).charAt(0);
                GrammarInitial.Stat st = grStats.get(c);
                synchronized (st) {
                    st.formCount += v.getForm().size();
                }
            }
        });
    }

    private Map<Character, GrammarLetter> addGrammar(TagLetter letters) {
        if (letters.letters.isEmpty()) {
            return null;
        }
        Map<Character, GrammarLetter> result = new TreeMap<>();
        for (TagLetter.OneLetterInfo lt : letters.letters) {
            GrammarLetter g = new GrammarLetter();
            g.name = lt.groupName;
            g.desc = lt.description;
            g.ch = addGrammar(lt.nextLetters);
            result.put(lt.letter, g);
        }
        return result;
    }

    private Properties loadSettings(String path) throws IOException {
        Properties result = new Properties();
        try (BufferedReader input = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            result.load(input);
        }
        return result;
    }
}
