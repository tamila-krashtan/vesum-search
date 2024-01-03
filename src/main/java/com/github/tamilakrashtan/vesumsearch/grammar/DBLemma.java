package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "lemmas")
public class DBLemma {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String lemma;
    private String tags;

//    @OneToMany(mappedBy = "lemma", fetch = FetchType.EAGER)
//    private Set<DBForm> forms;

    public DBLemma() {}

    public DBLemma(int id, String lemma, String tags) {

        this.id = id;
        this.lemma = lemma;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

//    public Set<DBForm> getForms() {
//        return this.forms;
//    }
}
