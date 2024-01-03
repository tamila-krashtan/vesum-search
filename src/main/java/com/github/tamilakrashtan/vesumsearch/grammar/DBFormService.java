package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DBFormService implements IDBFormService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DBFormRepository repository;

    @Override
    public List<DBForm> findByForm(String form) {

        //entityManager.clear();
        return repository.findByForm(form);
    }

    @Override
    public List<DBForm> findByFormLike(String regex) {

        //entityManager.clear();
        return repository.findByFormLike(regex);
    }

    public List<DBForm> findByFormLike(Pattern pattern) {

        //entityManager.clear();
        return repository.findByFormLike(pattern.pattern().replaceAll("\\.*\\*", "%"));
    }

    @Override
    public List<DBForm> findByLemma_id(int lemma_id) {

        //entityManager.clear();
        return repository.findByLemmaId(lemma_id);
    }

    @Override
    public List<DBForm> findByLemma_ids(Set<Integer> lemma_ids) {

        //entityManager.clear();
        return repository.findByLemmaIds(lemma_ids);
    }

    public Set<Integer> findLemmasByForm(String form) {

        //entityManager.clear();
        return findByForm(form).stream().map(DBForm::getLemmaId).collect(Collectors.toSet());
    }
}
