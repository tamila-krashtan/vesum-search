package com.github.tamilakrashtan.vesumsearch.grammar;

import com.github.tamilakrashtan.vesumsearch.utils.KorpusDateTime;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TextInfo implements Serializable {
    public transient String sourceFilePath; // ID зыходнага файлу
    public String subcorpus; // падкорпус
    public String textLabel;
    public String source; // крыніца: толькі для сайтаў і неразабраных
    public String url; // спасылка на знешні сайт, калі ёсць
    public String[] authors; // аўтары
    public String title; // назва: заўсёды
    public transient int textOrder; // толькі каб адсартаваць тэксты ў канкардансе
    public String[] translators; // перакладчыкі
    public String lang, langOrig; // мова тэксту, мова зыходнага тэксту
    public String[] styleGenres; // стылі і жанры
    public String edition; // выданне
    public String details; // дэталі
    public String file; // файл на зыходнай старонцы
    public String creationTime, publicationTime; // дата стварэння і публікацыі

    private transient Long creationTimeLatest, creationTimeEarliest;
    private transient Long publicationTimeLatest, publicationTimeEarliest;

    public Long creationTimeLatest() {
        if (creationTime == null) {
            return null;
        }
        if (creationTimeLatest == null) {
            KorpusDateTime dt = new KorpusDateTime(creationTime);
            creationTimeEarliest = dt.earliest();
            creationTimeLatest = dt.latest();
        }
        return creationTimeLatest;
    }

    public Long creationTimeEarliest() {
        if (creationTime == null) {
            return null;
        }
        if (creationTimeEarliest == null) {
            KorpusDateTime dt = new KorpusDateTime(creationTime);
            creationTimeEarliest = dt.earliest();
            creationTimeLatest = dt.latest();
        }
        return creationTimeEarliest;
    }

    public Long publicationTimeLatest() {
        if (publicationTime == null) {
            return null;
        }
        if (publicationTimeLatest == null) {
            KorpusDateTime dt = new KorpusDateTime(publicationTime);
            publicationTimeEarliest = dt.earliest();
            publicationTimeLatest = dt.latest();
        }
        return publicationTimeLatest;
    }

    public Long publicationTimeEarliest() {
        if (publicationTime == null) {
            return null;
        }
        if (publicationTimeEarliest == null) {
            KorpusDateTime dt = new KorpusDateTime(publicationTime);
            publicationTimeEarliest = dt.earliest();
            publicationTimeLatest = dt.latest();
        }
        return publicationTimeEarliest;
    }
}
