package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}Dictionary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}Form" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{}latin_char" />
 *       &lt;attribute name="lemma" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tag" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dictionaries" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="orthography" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{}VariantType" />
 *       &lt;attribute name="rules" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="regulation" type="{}RegulationType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "note",
    "dictionary",
    "form"
})
@XmlRootElement(name = "Variant")
public class Variant {

    @XmlElement(name = "Note")
    protected List<String> note;
    @XmlElement(name = "Dictionary")
    protected List<Dictionary> dictionary;
    @XmlElement(name = "Form")
    protected List<Form> form;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "lemma", required = true)
    protected String lemma;
    @XmlAttribute(name = "tag")
    protected String tag;
    @XmlAttribute(name = "dictionaries")
    protected String dictionaries;
    @XmlAttribute(name = "orthography")
    protected String orthography;
    @XmlAttribute(name = "type")
    protected VariantType type;
    @XmlAttribute(name = "rules")
    protected String rules;
    @XmlAttribute(name = "regulation")
    protected RegulationType regulation;

    /**
     * Gets the value of the note property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the note property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNote() {
        if (note == null) {
            note = new ArrayList<String>();
        }
        return this.note;
    }

    /**
     * Gets the value of the dictionary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dictionary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDictionary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dictionary }
     * 
     * 
     */
    public List<Dictionary> getDictionary() {
        if (dictionary == null) {
            dictionary = new ArrayList<Dictionary>();
        }
        return this.dictionary;
    }

    /**
     * Gets the value of the form property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the form property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getForm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Form }
     * 
     * 
     */
    public List<Form> getForm() {
        if (form == null) {
            form = new ArrayList<Form>();
        }
        return this.form;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the lemma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLemma() {
        return lemma;
    }

    /**
     * Sets the value of the lemma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLemma(String value) {
        this.lemma = value;
    }

    /**
     * Gets the value of the tag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the value of the tag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTag(String value) {
        this.tag = value;
    }

    /**
     * Gets the value of the dictionaries property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDictionaries() {
        return dictionaries;
    }

    /**
     * Sets the value of the dictionaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDictionaries(String value) {
        this.dictionaries = value;
    }

    /**
     * Gets the value of the orthography property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrthography() {
        return orthography;
    }

    /**
     * Sets the value of the orthography property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrthography(String value) {
        this.orthography = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link VariantType }
     *     
     */
    public VariantType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link VariantType }
     *     
     */
    public void setType(VariantType value) {
        this.type = value;
    }

    /**
     * Gets the value of the rules property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRules() {
        return rules;
    }

    /**
     * Sets the value of the rules property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRules(String value) {
        this.rules = value;
    }

    /**
     * Gets the value of the regulation property.
     * 
     * @return
     *     possible object is
     *     {@link RegulationType }
     *     
     */
    public RegulationType getRegulation() {
        return regulation;
    }

    /**
     * Sets the value of the regulation property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegulationType }
     *     
     */
    public void setRegulation(RegulationType value) {
        this.regulation = value;
    }

    public void setForms(List<Form> forms) {

        this.form = forms;
    }

}
