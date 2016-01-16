package io.nadron.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Birthday {
	
	@JsonProperty("bir")
    public String birthday;
    
    public Birthday(String birthday) {
        super();
        this.birthday = birthday;
    }
 
    public String getID(){
    	return "1";
    }
    //getter、setter
 
    public Birthday() {}
    
//    public String getBirthday() {
//		return birthday;
//	}
//
//	public void setBirthday(String birthday) {
//		this.birthday = birthday;
//	}

	@Override
    public String toString() {
        return this.birthday;
    }
}