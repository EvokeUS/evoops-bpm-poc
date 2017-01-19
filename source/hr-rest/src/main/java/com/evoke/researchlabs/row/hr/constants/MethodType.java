package com.evoke.researchlabs.row.hr.constants;

/**
 * 
 * @author Zama
 *
 */
public enum MethodType {
	POST("POST"), PUT("PUT"), GET("GET");
	
	private String methodType;
	
	private MethodType(String methodType){
		this.methodType = methodType;
	}
	
	public String getMethodType() {
		return methodType;
	}
}
