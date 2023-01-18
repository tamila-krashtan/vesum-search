package com.github.tamilakrashtan.vesumsearch.text;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Paragraph implements Serializable {
    public int page;
    public Sentence[] sentences;
}
