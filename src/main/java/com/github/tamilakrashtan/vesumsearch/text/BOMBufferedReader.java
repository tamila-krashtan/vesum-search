package com.github.tamilakrashtan.vesumsearch.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class BOMBufferedReader extends BufferedReader {
    private int lineNumber = 0;

    public BOMBufferedReader(Reader rd) throws IOException {
        super(rd);
        mark(4);

        int char1 = read();
        if (char1 != 65279) { // BOM: EF BB BF
            reset();
        }
    }

    public String readLine() throws IOException {
        lineNumber++;
        return super.readLine();
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
