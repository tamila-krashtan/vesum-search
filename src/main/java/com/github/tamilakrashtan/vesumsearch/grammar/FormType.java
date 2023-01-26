package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FormType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FormType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="nonstandard"/>
 *     &lt;enumeration value="potential"/>
 *     &lt;enumeration value="numeral"/>
 *     &lt;enumeration value="short"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FormType")
@XmlEnum
public enum FormType {


    /**
     * Nonstandard
     */
    @XmlEnumValue("nonstandard")
    NONSTANDARD("nonstandard"),

    /**
     * Potential
     */
    @XmlEnumValue("potential")
    POTENTIAL("potential"),

    /**
     * With numerals 2, 3, 4
     */
    @XmlEnumValue("numeral")
    NUMERAL("numeral"),

    /**
     * Short form (in adjectives)
     */
    @XmlEnumValue("short")
    SHORT("short");
    private final String value;

    FormType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FormType fromValue(String v) {
        for (FormType c: FormType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
