package com.logAnalysis.task2;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class Task2 {
	
	public static void run() throws Exception {
		
		// hadoop jar ./test_programme/flowstats.jar cn.leonard.custom.MainDriver
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()));

		Configuration configuration = new Configuration();
//		DBConfiguration.configureDB(configuration, "com.mysql.jdbc.Driver","jdbc:mysql://127.0.0.1:3306/loganalysis","root", "root");
//		configuration.addResource("hadoop-remote.xml");
		Job job = Job.getInstance(configuration);
		job.setJarByClass(Task2.class);
		
		job.setMapperClass(FlowMapper.class);
		job.setReducerClass(FlowReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
//		job.setOutputFormatClass(Text.class);
		
		job.setInputFormatClass(FlowLogInputFormat.class);
		FileInputFormat.setInputPaths(job, new Path("hdfs://master.unixmen.cn:9000/zhangmeng/output_test7/part-r-00000"));
//		DBInputFormat.setInput(job, inputClass, tableName, conditions, orderBy, fieldNames);
		FileOutputFormat.setOutputPath(job, new Path("hdfs://master.unixmen.cn:9000/zhangmeng/output_test8/"));
//		DBOutputFormat.setOutput(job, "dayinfo", "ip", "visitDay", "visitSum");
		
//		job.submit();
		boolean waitForCompletion = job.waitForCompletion(true);
		System.out.println(waitForCompletion);
		System.out.println(sdf.format(new Date()));
	}
	
	public static void main(String[] args) throws Exception {
		Task2.run();
	}
	
}
