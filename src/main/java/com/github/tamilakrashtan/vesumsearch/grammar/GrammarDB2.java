package com.github.tamilakrashtan.vesumsearch.grammar;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.tamilakrashtan.vesumsearch.ukrainian.UkrainianWordNormalizer;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public class GrammarDB2 {
    public static final String CACHE_FILE = "db.cache";

    private List<Paradigm> allParadigms = new ArrayList<>();

    private static JAXBContext CONTEXT;

    public static synchronized JAXBContext getContext() throws Exception {
        if (CONTEXT == null) {
            CONTEXT = JAXBContext.newInstance(Wordlist.class);
        }
        return CONTEXT;
    }

    public List<Paradigm> getAllParadigms() {
        return allParadigms;
    }

    public long getNumberOfParadigms(DBLemmaService lemmaService) {
        return lemmaService.count();
    }

    public static GrammarDB2 initializeFromJar() throws Exception {
        long be = System.currentTimeMillis();
        GrammarDB2 r = null;
        try (InputStream in = GrammarDB2.class.getResourceAsStream("/" + CACHE_FILE)) {
            if (in != null) {
                Input input = new Input(in, 65536);
                r = loadFromCache(input);
            }
        }
        if (r == null) {
            r = empty();
        }
        long af = System.currentTimeMillis();
        System.out.println("GrammarDB deserialization time: " + (af - be) + "ms");
        return r;
    }

    public static GrammarDB2 empty() {
        return new GrammarDB2();
    }

    public static GrammarDB2 initializeFromFile(File file) throws Exception {
        GrammarDB2 r = new GrammarDB2(file);
        return r;
    }

    public void makeCache(String dir) throws IOException {
        File cacheFile = new File(dir, CACHE_FILE);

        long be = System.currentTimeMillis();
        Kryo kryo = new Kryo();
        try (Output output = new Output(new FileOutputStream(cacheFile), 65536)) {
            kryo.writeObject(output, this);
        }
        long af = System.currentTimeMillis();
        System.out.println("GrammarDB serialization time: " + (af - be) + "ms");
    }

    private static GrammarDB2 loadFromCache(Input input) throws Exception {
        Kryo kryo = new Kryo();
        return kryo.readObject(input, GrammarDB2.class);
    }

    /**
     * Minimize memory usage.
     */
    public static void optimize(Paradigm p) {
        p.setLemma(optimizeString(fix(p.getLemma())));
        p.setTag(optimizeString(p.getTag()));
        for (Variant v : p.getVariant()) {
            v.setLemma(optimizeString(fix(v.getLemma())));
            v.setOrthography(optimizeString(v.getOrthography()));
            for (Form f : v.getForm()) {
                f.setTag(optimizeString(f.getTag()));
                f.setValue(optimizeString(fix(f.getValue())));
                f.setDictionaries(optimizeString(f.getDictionaries()));
                f.setOrthography(optimizeString(f.getOrthography()));
            }
        }
    }

    /**
     * Remove duplicate strings from memory. 
     */
    public static String optimizeString(String s) {
        return s == null ? null : s.intern();
    }

    /**
     * Changes stress and apostrophe
     */
    public static String fix(String s) {
        return s == null ? null : s.replace('\'', UkrainianWordNormalizer.correct_apostrophe).replace('+', UkrainianWordNormalizer.correct_stress);
    }

    public void addXMLFile(File file) throws Exception {
        Unmarshaller unm = getContext().createUnmarshaller();

        InputStream in;
        if (file.getName().endsWith(".gz")) {
            in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(file), 16384), 65536);
        } else {
            in = new BufferedInputStream(new FileInputStream(file), 65536);
        }
        try {
            Wordlist words = (Wordlist) unm.unmarshal(in);
            for (Paradigm p : words.getParadigm()) {
                optimize(p);
            }
            synchronized (this) {
                allParadigms.addAll(words.getParadigm());
            }
        } finally {
            in.close();
        }
    }

    /**
     * Only for kryo instantiation.
     */
    private GrammarDB2() {
    }

    /**
     * Read xml files for initialize.
     */
    private GrammarDB2(File... forLoads) throws Exception {
        long be = System.currentTimeMillis();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(16);
        long latest = 0;
        Future<?>[] results = new Future<?>[forLoads.length];
        for (int i = 0; i < forLoads.length; i++) {
            latest = Math.max(latest, forLoads[i].lastModified());
            System.out.println(forLoads[i].getPath());
            final File process = forLoads[i];
            results[i] = executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        addXMLFile(process);
                    } catch (Exception ex) {
                        throw new RuntimeException("Error in " + process, ex);
                    }
                }
            });
        }
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
            throw new Exception("Load GrammarDB2 timeout");
        }
        for (Future<?> f : results) {
            f.get();
        }
        long af = System.currentTimeMillis();
        System.out.println("GrammarDB loading time: " + (af - be) + "ms");
    }
}
