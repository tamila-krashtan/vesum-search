package com.github.tamilakrashtan.vesumsearch.grammar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DBLemmaRepository extends CrudRepository<DBLemma, Integer> {

    List<DBLemma> findByLemma(String lemma);

    @Query(value="select * from lemmas a where a.id in :ids", nativeQuery=true)
    List<DBLemma> findByIds(Set<Integer> ids);
}
