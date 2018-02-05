package com.logAnalysis.task1;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.logAnalysis.util.DBUtil;

public class FlowReducer extends Reducer<Text, LongWritable, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<LongWritable> values,
			Reducer<Text, LongWritable, Text, Text>.Context context) throws IOException, InterruptedException {
		Long tmpLong = 0L;
		String ip = key.toString().split("--")[0];
		String visitDay = key.toString().split("--")[1];
		for (LongWritable value : values) {
			tmpLong += value.get();
		}
//		String sql = "INSERT INTO dayinfo (ip, visitDay, visitSum) VALUES ('" + ip + "', '" + visitDay + "', '" + tmpLong + "')";
//		DBUtil.InsertInto(sql);
		context.write(new Text(ip), new Text(visitDay + "--" + tmpLong));
		
	}

}
