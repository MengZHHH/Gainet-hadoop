package com.logAnalysis.task2;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FlowMapper extends Mapper<Text, Text, Text, Text> {
	// DO THIS IS USEFUL TO PRELOAD THE IPMAPPER UTIL 
	
	@Override
	protected void setup(Mapper<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		super.setup(context);
		Configuration con = context.getConfiguration();
		con.set("fs.defaultFS", "master.unixmen.cn:9000");
		con.addResource(new Path("/home/hadoop/conf/core-site.xml"));
		
	}
	@Override
	protected void map(Text key, Text value, Mapper<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		try {
			context.write(key, value);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
