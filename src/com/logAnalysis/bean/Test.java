package com.logAnalysis.bean;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Long [] num1 = {21L, 34L, 2L, 0L, 40L, 0L, 10L};
		double [][] num = {{1, 21L}, {2, 34L}, {3, 2L}, {4, 0L}, {5, 40L}, {6, 0L},{7, 10L}};
		LinearRegression  a = new LinearRegression();
		a.CustomRegression(num1);
		SimpleRegression simpleRegression = new SimpleRegression(true);
		simpleRegression.addData(num);
		double slope = simpleRegression.getSlope();
		System.out.println(slope);
	}

}
