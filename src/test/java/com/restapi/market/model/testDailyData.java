package com.restapi.market.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.restapi.market.service.CompanyService;

public class testDailyData {
	
	@InjectMocks //injects the mock dependencies
	DailyData dailyData;
	
	 
	 @Test
	    public void testgetCompanyName() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final DailyData obj = new DailyData();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("companyName");
	        field.setAccessible(true);
	        field.set(obj, "APPLE");

	        //when
	        final String result = obj.getCompanyName();

	        //then
	        assertEquals( result, "APPLE");
	    }

	    @Test
	    public void testgetTicker() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final DailyData obj = new DailyData();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("ticker");
	        field.setAccessible(true);
	        field.set(obj, "BABA");

	        //when
	        final String result = obj.getTicker();

	        //then
	        assertEquals( result, "BABA");
	    }
	    
	    @Test
	    public void testgetVolume() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final DailyData obj = new DailyData();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("volume");
	        field.setAccessible(true);
	        field.set(obj, "55");

	        //when
	        final String result = obj.getVolume();

	        //then
	        assertEquals( result, "55");
	    }
	    
	    @Test
	    public void testgetSector() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final DailyData obj = new DailyData();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("sector");
	        field.setAccessible(true);
	        field.set(obj, "Technology");

	        //when
	        final String result = obj.getSector();

	        //then
	        assertEquals( result, "Technology");
	    }
	    
	    @Test
	    public void testgetPrice() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final DailyData obj = new DailyData();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("price");
	        field.setAccessible(true);
	        field.set(obj, "60");

	        //when
	        final String result = obj.getPrice();

	        //then
	        assertEquals( result, "60");
	    }	  
	    
	    @Test
	    public void testgetDate() throws NoSuchFieldException, IllegalAccessException {
	        //given
	        final DailyData obj = new DailyData();
	        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("date");
	        field.setAccessible(true);
	        field.set(obj, "2020-02-02");

	        //when
	        final String result = obj.getDate();

	        //then
	        assertEquals( result, "2020-02-02");
	    }
	    
		
		 @Test
		    public void testsetPrice() throws NoSuchFieldException, IllegalAccessException {
		        //given
		        final DailyData obj = new DailyData();

		        //when
		        obj.setPrice("50");

		        //then
		        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("price");
		        field.setAccessible(true);
		        assertEquals( field.get(obj), "50");
		    }	
		 
		 @Test
		    public void testsetDate() throws NoSuchFieldException, IllegalAccessException {
		        //given
		        final DailyData obj = new DailyData();

		        //when
		        obj.setDate("2020-02-02");

		        //then
		        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("date");
		        field.setAccessible(true);
		        assertEquals( field.get(obj), "2020-02-02");
		    }	
		 
		 @Test
		    public void testsetTicker() throws NoSuchFieldException, IllegalAccessException {
		        //given
		        final DailyData obj = new DailyData();

		        //when
		        obj.setTicker("BABA");

		        //then
		        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("ticker");
		        field.setAccessible(true);
		        assertEquals( field.get(obj), "BABA");
		    }	
		 
		 @Test
		    public void testsetSector() throws NoSuchFieldException, IllegalAccessException {
		        //given
		        final DailyData obj = new DailyData();

		        //when
		        obj.setSector("Technology");

		        //then
		        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("sector");
		        field.setAccessible(true);
		        assertEquals( field.get(obj), "Technology");
		    }	
		 
		 @Test
		    public void testsetVolume() throws NoSuchFieldException, IllegalAccessException {
		        //given
		        final DailyData obj = new DailyData();

		        //when
		        obj.setVolume("50");

		        //then
		        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("volume");
		        field.setAccessible(true);
		        assertEquals( field.get(obj), "50");
		    }	
		 
		 @Test
		    public void testsetCompanyName() throws NoSuchFieldException, IllegalAccessException {
		        //given
		        final DailyData obj = new DailyData();

		        //when
		        obj.setCompanyName("APPLE");

		        //then
		        final java.lang.reflect.Field field = obj.getClass().getDeclaredField("companyName");
		        field.setAccessible(true);
		        assertEquals( field.get(obj), "APPLE");
		    }

}
