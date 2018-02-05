package com.logAnalysis.bean;


import org.apache.commons.math3.stat.regression.SimpleRegression;
public class LinearRegression {

	public LinearRegression(){
		
	}
	
	public double CustomRegression(Long [] value) {
		// TODO Auto-generated method stub
		SimpleRegression simpleRegression = new SimpleRegression(true);
		
		if (value.length == 7 ) {
			simpleRegression.addData(new double [][] { {1, value[0]}, {2, value[1]}, {3, value[2]},
				{4, value[3]}, {5, value[4]}, {6, value[5]}, {7, value[6]} });
			double slope = simpleRegression.getSlope();
			System.out.println(slope);
			return slope;
			
		}
		return 0;				
		}

	}
	
	


