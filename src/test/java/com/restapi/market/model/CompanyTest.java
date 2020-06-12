package com.restapi.market.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CompanyTest {

    @Test
    public void testgetId() throws NoSuchFieldException, IllegalAccessException {
        //given
        final Company obj = new Company();
        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(obj, "1");

        //when
        final String result = obj.getId();

        //then
        assertEquals( result, "1");
    }
	 
	 @Test
	    public void testsetName() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Company obj = new Company();

	        //when
	        obj.setName("APPLE");

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("name");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), "APPLE");
	    }
	 
	 
	 @Test
	    public void testseSector() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Company obj = new Company();

	        //when
	        obj.setSector("Technology");

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("sector");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), "Technology");
	    }
	 
	 
	 @Test
	    public void testsetTicker() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Company obj = new Company();

	        //when
	        obj.setTicker("BABA");

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("ticker");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), "BABA");
	    }
	 
	 
	 @Test
	    public void testsetId() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Company obj = new Company();

	        //when
	        obj.setId("1");

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("id");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), "1");
	    }
	 

	
}
