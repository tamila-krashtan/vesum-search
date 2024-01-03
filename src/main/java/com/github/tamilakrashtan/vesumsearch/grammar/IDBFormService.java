package com.github.tamilakrashtan.vesumsearch.grammar;

import java.util.List;
import java.util.Set;

public interface IDBFormService {

    List<DBForm> findByForm(String form);

    List<DBForm> findByLemma_id(int lemma_id);

    List<DBForm> findByFormLike(String regex);

    List<DBForm> findByLemma_ids(Set<Integer> lemma_ids);
}
