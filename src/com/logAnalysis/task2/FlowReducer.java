package com.logAnalysis.task2;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.logAnalysis.util.DBUtil;

public class FlowReducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values,
			Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
		Long tmpLong = 0L;
		for (Text value : values) {
			tmpLong += Long.valueOf(value.toString().split("--")[1]);
		}
		String sql = "INSERT INTO weekinfo (ip, visitWeek, trend, visitSum) VALUES ('" + key.toString() + "', 4, " + tmpLong/564.0 + ", '" + tmpLong + "')";
		DBUtil.InsertInto(sql);
		context.write(new Text(key), new Text("4" + "--" + tmpLong/564.0 + tmpLong));
		
	}

}
