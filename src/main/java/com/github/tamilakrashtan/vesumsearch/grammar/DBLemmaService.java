package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DBLemmaService implements IDBLemmaService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DBLemmaRepository repository;

    @Override
    public List<DBLemma> findAll() {

        List<DBLemma> result = (List<DBLemma>) repository.findAll();
        entityManager.clear();
        return result;
    }

    @Override
    public List<DBLemma> findByLemma(String lemma) {

        //entityManager.clear();
        return repository.findByLemma(lemma);
    }

    @Override
    public Optional<DBLemma> findById(int id) {

        //entityManager.clear();
        return repository.findById(id);
    }

    @Override
    public List<DBLemma> findByIds(Set<Integer> ids) {

        //entityManager.clear();
        return repository.findByIds(ids);
    }

    @Override
    public long count() {

        //entityManager.clear();
        return repository.count();
    }
}
