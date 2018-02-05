package com.logAnalysis.bean;

public class ClassicSlope {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}
	public ClassicSlope() {
		
	}
	
	/**
	 * （后四天平均数-前三天平均数）/3.5
	 */
	public double ClassicSclopeCal(Long [] value) {
		if (value.length == 7 ) {
			double a = (value[0] + value[1] + value [2])/3;
			double b = (value[3] + value[4] + value [5] + value [6])/4;
			double slope = (b - a)/3.5;
			System.out.println(slope);
			return slope;
		}
		return 0;
		
	}
}
