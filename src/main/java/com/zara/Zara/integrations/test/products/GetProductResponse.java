//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.08.20 at 11:18:15 PM EAT 
//


package com.zara.Zara.integrations.test.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GetProductResult" type="{http://tempuri.org/}Product" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getProductResult"
})
@XmlRootElement(name = "GetProductResponse")
public class GetProductResponse {

    @XmlElement(name = "GetProductResult")
    protected Product getProductResult;

    /**
     * Gets the value of the getProductResult property.
     * 
     * @return
     *     possible object is
     *     {@link Product }
     *     
     */
    public Product getGetProductResult() {
        return getProductResult;
    }

    /**
     * Sets the value of the getProductResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Product }
     *     
     */
    public void setGetProductResult(Product value) {
        this.getProductResult = value;
    }

}
