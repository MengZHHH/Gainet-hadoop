package com.logAnalysis.task1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.logAnalysis.util.LogUtil;

public class BinaryRecorderReader extends RecordReader<Text, Text> {

	private long start;
	private long end;
	private long currentPos;
	
	// 配置
	private Configuration conf = null;
	// 切片
	private FileSplit fileSplit = null;
	
	private FSDataInputStream fsis = null;
	
	
	// 定义输出的key value
	private Text key = new Text();
	private Text value = new Text();
	private byte[] byteArray = new byte[52];
	private boolean processed = false;	//进度
	
	private Map<String,String> map = new HashMap<>();
	
	public BinaryRecorderReader() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 进行初始化工作，打开文件流，根据分块信息设置起始位置和长度等
	 */
	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
		System.out.println("初始化BinaryRecorderReader");
		
		// TODO Auto-generated method stub
		// 将当前的分片转换为制定的分片
		fileSplit = (FileSplit) split;
		// 获取配置项
		conf = context.getConfiguration();
		
		// 设置读取文件的起始位置
		this.start = fileSplit.getStart();
		this.end = this.start + fileSplit.getLength();
		// 获取文件并打开输入流
		Path path = fileSplit.getPath();
		FileSystem fs = path.getFileSystem(conf);
		fsis = fs.open(path);
		
		// 设置流的开始位置和当前的字节索引位置
		fsis.seek(this.start);
		this.currentPos = this.start;
		
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if(this.currentPos < this.end){
//			System.out.print(byteArray.length + "--" + new BigDecimal(currentPos/52d) + "--");
			try {
				fsis.read(byteArray);
//				System.out.print(currentPos + "--");
				map.clear();
				LogUtil.getCustomDataOfBinArray(byteArray, map);
				if (map == null || map.size() != 3 || !map.containsKey(LogUtil.srcIpKey) || !map.containsKey(LogUtil.desIpKey) || !map.containsKey(LogUtil.timeKey)) {
					throw new Exception("本次解析失败");
				}
				
				this.key.set(map.get(LogUtil.desIpKey));
				this.value.set(map.get(LogUtil.timeKey));
				
				this.currentPos = fsis.getPos();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				processed = false;
				return false;
			}
		}else{
			processed = true;
			return false;
		}
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return this.key;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return this.value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return ((processed == true)? 1.0f : 0.0f);
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		try {  
			if(fsis != null){
				fsis.close();  
			}  
		} catch (IOException e) {  
		    // TODO Auto-generated catch block  
		    e.printStackTrace();  
		}  
	}

}
