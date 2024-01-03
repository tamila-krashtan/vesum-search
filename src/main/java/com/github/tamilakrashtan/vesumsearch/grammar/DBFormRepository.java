package com.github.tamilakrashtan.vesumsearch.grammar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DBFormRepository extends CrudRepository<DBForm, Integer> {

    @Query(value="select * from forms a where a.form= :form", nativeQuery=true)
    List<DBForm> findByForm(String form);

    List<DBForm> findByFormLike(String regex);

    @Query(value="select * from forms a where a.lemma_id= :lemma_id", nativeQuery=true)
    List<DBForm> findByLemmaId(int lemma_id);

    @Query(value="select * from forms a where a.lemma_id in :lemma_ids", nativeQuery=true)
    List<DBForm> findByLemmaIds(Set<Integer> lemma_ids);
}
