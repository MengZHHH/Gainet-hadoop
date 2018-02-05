package com.logAnalysis.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FlowMapper extends Mapper<Text, Text, Text, LongWritable> {
	// DO THIS IS USEFUL TO PRELOAD THE IPMAPPER UTIL 
	private Set<String> ipSet = new HashSet<String>();
	
	@Override
	protected void setup(Mapper<Text, Text, Text, LongWritable>.Context context)
			throws IOException, InterruptedException {
		super.setup(context);
		String basePath = "hdfs://master.unixmen.cn:9000/zhangyubing/ip";
		Configuration con = context.getConfiguration();
		con.set("fs.defaultFS", "master.unixmen.cn:9000");
		con.addResource(new Path("/home/hadoop/conf/core-site.xml"));
		FileSystem fs = FileSystem.get(con);
		FSDataInputStream open = fs.open(new Path(basePath + "/" + "vps+server.txt" ));
		initIpSet(open);
		
	}

	private void initIpSet(FSDataInputStream open) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(open));
		String s = null;
		try {
			while ((s = reader.readLine() )!=null) {
				ipSet.add(s);
			}
		} catch (IOException e) {
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	@Override
	protected void map(Text key, Text value, Mapper<Text, Text, Text, LongWritable>.Context context)
			throws IOException, InterruptedException {
		try {
//			if(key.toString().startsWith("122.114.") || key.toString().startsWith("116.255.")) {
//				context.write(new Text(key.toString()), new LongWritable(1));
//			}
			if(ipSet.contains(key.toString())) {
				context.write(new Text(key.toString() + "--" + value.toString()), new LongWritable(1));
			}
			
//			String timeDestIp[] = key.toString().split("--");
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
//			String saveMsg = sdf.format(new Timestamp(Long.valueOf(timeDestIp[0])));
//			saveMsg += "--" + timeDestIp[1];
			
//			String saveMsg = key.toString();
			
//			if (!IpUtils.checkIpEffective(key.toString())) {
//				return;
//			}
//			
//			Map<String, String> countryAndCityByIp = ipMapper.getCountryAndCityByIp(key.toString());
//			if (countryAndCityByIp!= null && countryAndCityByIp.containsKey(IpMapper.RESULT_KEY_COUNTRY) && countryAndCityByIp.containsKey(IpMapper.RESULT_KEY_CITY)) {
//				if (StringUtils.isBlank(countryAndCityByIp.get(IpMapper.RESULT_KEY_COUNTRY))) {
//					context.write(new Text(UNKNOW_COUNTRY), new LongWritable(1L));
//					return;
//				}
//				
//				if (countryAndCityByIp.get(IpMapper.RESULT_KEY_COUNTRY).equals("中国")) {
//					if (StringUtils.isBlank(countryAndCityByIp.get(IpMapper.RESULT_KEY_CITY))) {
//						context.write(new Text(CHINA_UNKNOW_CITY), new LongWritable(1));
//					}else{
//						context.write(new Text(key.toString()), new LongWritable(1));
//					}
//				}else{
//					context.write(new Text(FROEIGIN_COUNTRY), new LongWritable(1L));
//				}
//				
//			}else{
//				context.write(new Text(UNKNOW_LOACTION), new LongWritable(1L));
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
