package io.nadron.util;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unchecked")
public class JacksonTest {
    private ObjectMapper objectMapper = null;
    private AccountBean bean = null;
    
    @Before
    public void init() {
        bean = new AccountBean();
        bean.setAddress("china-Guangzhou");
        bean.setEmail("hoojo_@126.com");
//        bean.setId(1);
//        bean.setName("hoojo");
        bean.setBirthday(new Birthday());
        
        objectMapper = new ObjectMapper();
        objectMapper.setVisibilityChecker(objectMapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(Include.NON_DEFAULT);  
    }
    
    @After
    public void destory() {
        try {
            objectMapper = null;
            bean = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   @Test
   public void writeEntityJSON() {
       
       try {
           //writeObject可以转换java对象，eg:JavaBean/Map/List/Array等 
           System.out.println(objectMapper.writeValueAsString(bean));
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
   
   @Test
   public void readEntityJSON() {
       
       try {
           //writeObject可以转换java对象，eg:JavaBean/Map/List/Array等
    	   String json = "{\"id\":1,\"bir\":{\"bir\":\"10-1\",\"xx\":1}}";
    	   AccountBean bean = objectMapper.readValue(json, AccountBean.class);
           System.out.println(bean.toString());
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
}