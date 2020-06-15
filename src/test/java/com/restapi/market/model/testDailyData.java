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
	        assertEquals("APPLE", result);
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
	        assertEquals( "BABA",result);
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
	        assertEquals( "55",result);
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
	        assertEquals( "Technology",result);
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
	        assertEquals("60", result);
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
	        assertEquals("2020-02-02", result);
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
		        assertEquals("50", field.get(obj));
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
		        assertEquals( "2020-02-02", field.get(obj));
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
		        assertEquals("BABA",field.get(obj));
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
		        assertEquals("Technology",field.get(obj));
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
		        assertEquals( "50", field.get(obj));
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
		        assertEquals("APPLE", field.get(obj));
		    }

}
