package com.github.tamilakrashtan.vesumsearch.grammar;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IDBLemmaService {

    List<DBLemma> findAll();

    List<DBLemma> findByLemma(String lemma);

    Optional<DBLemma> findById(int id);

    List<DBLemma> findByIds(Set<Integer> ids);

    long count();
}
