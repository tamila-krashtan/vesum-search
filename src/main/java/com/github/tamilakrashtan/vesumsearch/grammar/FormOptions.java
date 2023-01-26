package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FormOptions.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FormOptions">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="anim"/>
 *     &lt;enumeration value="inanim"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FormOptions")
@XmlEnum
public enum FormOptions {


    /**
     * Animate
     */
    @XmlEnumValue("anim")
    ANIM("anim"),

    /**
     * Inanimate
     */
    @XmlEnumValue("inanim")
    INANIM("inanim");
    private final String value;

    FormOptions(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FormOptions fromValue(String v) {
        for (FormOptions c: FormOptions.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
