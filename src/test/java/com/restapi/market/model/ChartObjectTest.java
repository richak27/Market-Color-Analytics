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
	        assertEquals( result,"Abbott Laboratories");
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
	        assertEquals( result, "#FF0088");
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
	        assertEquals( result, "#FF8800");
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
	        assertEquals( result, false);
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
	        assertEquals( result, val);
	    }
	    
	    @Test
	    public void testsetFill() throws NoSuchFieldException, IllegalAccessException {
	        //given
	    	final ChartObject obj = new ChartObject();

	        //when
	        obj.setFill(false);

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("fill");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), false);
	    }
	    
}