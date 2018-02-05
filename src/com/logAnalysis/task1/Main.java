package com.logAnalysis.task1;

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

import com.logAnalysis.task2.Task2;


public class Main {
	
	public static void main(String[] args) throws Exception {
		
		// hadoop jar ./test_programme/flowstats.jar cn.leonard.custom.MainDriver
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()));

		Configuration configuration = new Configuration();
//		DBConfiguration.configureDB(configuration, "com.mysql.jdbc.Driver","jdbc:mysql://127.0.0.1:3306/loganalysis","root", "root");
//		configuration.addResource("hadoop-remote.xml");
		Job job = Job.getInstance(configuration);
		job.setJarByClass(Main.class);
		
		job.setMapperClass(FlowMapper.class);
		job.setReducerClass(FlowReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
//		job.setOutputFormatClass(Text.class);
		
		job.setInputFormatClass(FlowLogInputFormat.class);
		FileInputFormat.setInputPaths(job, new Path("C:\\Users\\Windows10\\Desktop\\1516934560.dat"));
//		DBInputFormat.setInput(job, inputClass, tableName, conditions, orderBy, fieldNames);
		FileOutputFormat.setOutputPath(job, new Path("hdfs://master.unixmen.cn:9000/zhangmeng/output_test7/"));
//		DBOutputFormat.setOutput(job, "dayinfo", "ip", "visitDay", "visitSum");
		
//		job.submit();
		boolean waitForCompletion = job.waitForCompletion(true);
		System.out.println(waitForCompletion);
		System.out.println(sdf.format(new Date()));
		//调用任务2
		Task2.run();
	}
}
