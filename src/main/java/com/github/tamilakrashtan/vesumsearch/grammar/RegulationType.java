package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RegulationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RegulationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="add"/>
 *     &lt;enumeration value="mistake"/>
 *     &lt;enumeration value="fantasy"/>
 *     &lt;enumeration value="undesirable"/>
 *     &lt;enumeration value="limited"/>
 *     &lt;enumeration value="rare"/>
 *     &lt;enumeration value="rare_branch"/>
 *     &lt;enumeration value="obscenism"/>
 *     &lt;enumeration value="invective"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RegulationType")
@XmlEnum
public enum RegulationType {


    /**
     * Absent in the dictionaries, but should be there
     */
    @XmlEnumValue("add")
    ADD("add"),

    /**
     * Contain mistakes in some dictionaries
     */
    @XmlEnumValue("mistake")
    MISTAKE("mistake"),

    /**
     * Strange words
     */
    @XmlEnumValue("fantasy")
    FANTASY("fantasy"),

    /**
     * Better to be avoided
     */
    @XmlEnumValue("undesirable")
    UNDESIRABLE("undesirable"),

    /**
     * Better to be limited in usage
     */
    @XmlEnumValue("limited")
    LIMITED("limited"),

    /**
     * Rare
     */
    @XmlEnumValue("rare")
    RARE("rare"),

    /**
     * Rare narrow-field terminology
     */
    @XmlEnumValue("rare_branch")
    RARE_BRANCH("rare_branch"),

    /**
     * Obscene
     */
    @XmlEnumValue("obscenism")
    OBSCENISM("obscenism"),

    /**
     * Invective
     */
    @XmlEnumValue("invective")
    INVECTIVE("invective");
    private final String value;

    RegulationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RegulationType fromValue(String v) {
        for (RegulationType c: RegulationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
