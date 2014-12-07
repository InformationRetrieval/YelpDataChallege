package com.yelpdata.ir.project;

public class MyStringCmp {
	
	    private String myString;
	    
	    public MyStringCmp(String s){
	    	this.myString = s;
	    }
	    
	    @Override
	    public boolean equals(Object o){
	    	String str = (String) o;
	    	return this.myString.equalsIgnoreCase(str);
	    }

}
