package com.restapi.market.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ChartObjectCustomTest {


	    @Test
	    public void testgetLabels() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final ChartObjectCustom obj = new ChartObjectCustom();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("labels");
	        field.setAccessible(true);
	        
	        
	        List<String>val = new ArrayList<>();
	        val.add("Abbott Laboratories");
	        field.set(obj, val);
	        
	        //when
	        final List<String> result = obj.getLabels();
	        
	        //then
	        assertEquals(val,result);
	    }
		 
	
}
