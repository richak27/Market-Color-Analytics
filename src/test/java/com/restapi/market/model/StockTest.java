package com.restapi.market.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StockTest {

	
	 @Test
	    public void testsetVolume() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Stock obj = new Stock();

	        //when
	        obj.setVolume(50);

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("volume");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), Double.valueOf(50));
	    }
	 
	 @Test
	    public void testsetClose() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Stock obj = new Stock();

	        //when
	        obj.setClose(50);

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("close");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), Double.valueOf(50));
	    }
	 
	 @Test
	    public void testsetWeek() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Stock obj = new Stock();

	        //when
	        obj.setWeek(5);

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("week");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), Integer.valueOf(5));
	    }
	 
	 @Test
	    public void testsetMonth() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Stock obj = new Stock();

	        //when
	        obj.setMonth("Feb");

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("month");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), "Feb");
	    }
	 
	 @Test
	    public void testsetDate() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final Stock obj = new Stock();

	        //when
	        obj.setDate("2020-02-02");

	        //then
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("date");
	        field.setAccessible(true);
	        assertEquals( field.get(obj), "2020-02-02");
	    }
	 
}
