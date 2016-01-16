/*
 * Playdom, Inc. (c) 2009 - 2010 All Rights Reserved
 */
package com.dol.cdf.common.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 
 * 
 * If you feel the need to change this be sure to update the serialization because 
 * it is NOT generated.
 * 
 */
 
public class Entity implements Comparable<Object> { 

    @JsonProperty("id")
    public String guid = "defalutGuidXXXXXXXX";
    
    public Entity(String guid) {
        this.guid = guid;
    }

    public Entity() {
    }

    
    @Override
	public int compareTo(Object o) {
        Entity e = (Entity) o;
        return this.guid.compareTo(e.guid);
    }

    @Override
	public String toString() {
        return this.getClass() + "=" + guid;
    }

   
    
}
