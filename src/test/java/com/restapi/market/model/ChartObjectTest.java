package com.restapi.market.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

public class ChartObjectTest {

	
	@InjectMocks //injects the mock dependencies
	ChartObject chartObject;
		
	 @Test
	    public void testgetLabel() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final ChartObject obj = new ChartObject();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("label");
	        field.setAccessible(true);
	        field.set(obj, "Abbott Laboratories");

	        //when
	        final String result = obj.getLabel();

	        //then
	        assertEquals( "Abbott Laboratories",result);
	    }

	    @Test
	    public void testgetBackgroundColor() throws NoSuchFieldException, IllegalAccessException {
	        //given
	    	final ChartObject obj = new ChartObject();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("backgroundColor");
	        field.setAccessible(true);
	        field.set(obj, "#FF0088");
	        //when
	        final String result = obj.getBackgroundColor();

	        //then
	        assertEquals( "#FF0088",result);
	    }
	    
	    @Test
	    public void testgetBorderColor() throws NoSuchFieldException, IllegalAccessException {
	        //given
	    	final ChartObject obj = new ChartObject();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("borderColor");
	        field.setAccessible(true);
	        field.set(obj, "#FF8800");
	        //when
	        final String result = obj.getBorderColor();

	        //then
	        assertEquals("#FF8800",result);
	    }
	    
	    @Test
	    public void isFilltest() throws NoSuchFieldException, IllegalAccessException {
	        //given
	    	final ChartObject obj = new ChartObject();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("fill");
	        field.setAccessible(true);
	        field.set(obj, false);
	        
	        //when
	        final boolean result = obj.isFill();

	        //then
	        assertEquals( false,result);
	    }
	    
	    
	    @Test
	    public void testgetData() throws NoSuchFieldException, IllegalAccessException {
	        //given
	    	final ChartObject obj = new ChartObject();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("data");
	        field.setAccessible(true);
	        
	        List<Double>val = new ArrayList<>();
	        val.add(90.89);
	        
	        field.set(obj,val );
	        
	        //when
	        final List<Double> result = obj.getData();

	        //then
	        assertEquals(val,result);
	    }
	    
	    @Test
	    public void testsetFill() throws NoSuchFieldException, IllegalAccessException {
	        //given
	    	final ChartObject obj = new ChartObject();

	        //when
	        obj.setFill();

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("fill");
	        field.setAccessible(true);
	        assertEquals(false, field.get(obj));
	    }
	    
}
