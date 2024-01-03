package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.persistence.*;

@Entity
@Table(name = "forms")
public class DBForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //@ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name="lemmaId", nullable=false)
    //private DBLemma lemma;

    @Column(name="lemma_id")
    private int lemmaId;
    private String form;
    private String tags;

    public DBForm() {}

    public DBForm(int lemma_id, String form, String tags) {

        this.lemmaId = lemma_id;
        this.form = form;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

//    public DBLemma getLemma() {
//        return this.lemma;
//    }

    public int getLemmaId() {
        return this.lemmaId;
    }
}
