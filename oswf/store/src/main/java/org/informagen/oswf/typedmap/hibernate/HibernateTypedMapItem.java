package org.informagen.typedmap.hibernate;

/**
**  Pojo used by the HibernatePropertySet, in the implementation this file is 
**     mapped via an HBM but could use Hibernate Annotation  
*/

import java.io.Serializable;

public class HibernateTypedMapItem implements Serializable {

    // Composite Key: piid + key
     private Long piid;
     private String key;

     private int type;
     private String stringVal;
     private Integer intVal;
     private Double doubleVal;
     private Long longVal;
     private String textVal;

    public HibernateTypedMapItem() {}

	
    public HibernateTypedMapItem(Long piid, String key) {
        this.piid = piid;
        this.key = key;
    }
    public HibernateTypedMapItem(Long piid, String key, int type, String stringVal, Integer intVal, Double doubleVal, Long longVal, String textVal) {
       this.piid = piid;
       this.key = key;
       this.type = type;
       this.stringVal = stringVal;
       this.intVal = intVal;
       this.doubleVal = doubleVal;
       this.longVal = longVal;
       this.textVal = textVal;
    }
   
    public Long getProcessInstanceId() { 
        return piid;
    }
    
    public void setProcessInstanceId(Long piid) {
        this.piid = piid;
    }
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    public String getStringVal() {
        return stringVal;
    }
    
    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }
    public Integer getIntVal() {
        return intVal;
    }
    
    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }
    public Double getDoubleVal() {
        return doubleVal;
    }
    
    public void setDoubleVal(Double doubleVal) {
        this.doubleVal = doubleVal;
    }
    public Long getLongVal() {
        return longVal;
    }
    
    public void setLongVal(Long longVal) {
        this.longVal = longVal;
    }
    
    public String getTextVal() {
        return this.textVal;
    }
    
    public void setTextVal(String textVal) {
        this.textVal = textVal;
    }

    public String toString() {
     
        StringBuffer buffer = new StringBuffer();
     
       // buffer.append("piid: ").append(piid).append(", ");
        buffer.append("Key: ").append(key).append(", ");
        buffer.append("type: ").append(type).append(", ");
        
        if(stringVal != null)
            buffer.append("value: ").append(type);
            
        return buffer.toString();
    }

}


