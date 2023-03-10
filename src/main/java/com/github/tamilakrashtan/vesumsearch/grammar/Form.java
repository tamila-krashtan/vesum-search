package com.github.tamilakrashtan.vesumsearch.grammar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="tag" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{}FormType" />
 *       &lt;attribute name="dictionaries" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="orthography" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="options" type="{}FormOptions" />
 *       &lt;attribute name="govern" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="todo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "Form")
public class Form {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "tag", required = true)
    protected String tag;
    @XmlAttribute(name = "type")
    protected FormType type;
    @XmlAttribute(name = "dictionaries")
    protected String dictionaries;
    @XmlAttribute(name = "orthography")
    protected String orthography;
    @XmlAttribute(name = "options")
    protected FormOptions options;
    @XmlAttribute(name = "govern")
    protected String govern;
    @XmlAttribute(name = "todo")
    protected String todo;
    @XmlAttribute(name = "comment")
    protected String comment;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link FormType }
     *     
     */
    public FormType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormType }
     *     
     */
    public void setType(FormType value) {
        this.type = value;
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
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link FormOptions }
     *     
     */
    public FormOptions getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormOptions }
     *     
     */
    public void setOptions(FormOptions value) {
        this.options = value;
    }

    /**
     * Gets the value of the govern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGovern() {
        return govern;
    }

    /**
     * Sets the value of the govern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGovern(String value) {
        this.govern = value;
    }

    /**
     * Gets the value of the todo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTodo() {
        return todo;
    }

    /**
     * Sets the value of the todo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTodo(String value) {
        this.todo = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

}
