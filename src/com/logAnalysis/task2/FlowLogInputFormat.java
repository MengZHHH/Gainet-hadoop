package com.logAnalysis.task2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import com.google.common.base.Stopwatch;

public class FlowLogInputFormat extends FileInputFormat<Text, Text> {
	
	@Override
	public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		System.out.println("创建RecoredReader");
		BinaryRecorderReader reader = new BinaryRecorderReader();
		reader.initialize(split, context);
		return reader;
	}

	// 重写判断可分割的方法 设置为不可分割
	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		System.out.println("判断文件是否可以分割");
		// return false;
		return true;
	}

	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {
		   Stopwatch sw = new Stopwatch().start();
//	    long minSize = Math.max(getFormatMinSplitSize(), getMinSplitSize(job));
//	    long maxSize = getMaxSplitSize(job);

	    // generate splits
	    // 创建分片的list
	    List<InputSplit> splits = new ArrayList<InputSplit>();
	    // 获取文件信息
	    List<FileStatus> files = listStatus(job);
	    
	    // 遍历文件
	    for (FileStatus file: files) {
	    	// 获取文件路径
	      Path path = file.getPath();
	      // 获取文件长度
	      long length = file.getLen();
	      if (length != 0) {
	    	  // 块文件分区
	        BlockLocation[] blkLocations;
	        if (file instanceof LocatedFileStatus) {
	          blkLocations = ((LocatedFileStatus) file).getBlockLocations();
	        } else {
	          FileSystem fs = path.getFileSystem(job.getConfiguration());
	          blkLocations = fs.getFileBlockLocations(file, 0, length);
	        }
	        if (isSplitable(job, path)) {
	          //long blockSize = file.getBlockSize();
	          //long splitSize = computeSplitSize(blockSize, minSize, maxSize);
	        	
	        	// 一共有多少个
	          long totoalMetaCount = (long)length;
	          // 获取最后一个分片需要的额外的大小
	          long eachMetaCount = (long)totoalMetaCount/1;
	          
	          // 每个分片的大小
	          long tmpSize = eachMetaCount;
	          
	          long splitSize = tmpSize;
	          long bytesRemaining = length;
	          //while (((double) bytesRemaining)/splitSize > 1.1) {
	          while(bytesRemaining > tmpSize*1.1){
	            int blkIndex = getBlockIndex(blkLocations, length-bytesRemaining);
	            splits.add(makeSplit(path, length-bytesRemaining, splitSize,
	                        blkLocations[blkIndex].getHosts(),
	                        blkLocations[blkIndex].getCachedHosts()));
	            bytesRemaining -= splitSize;
	          }

	          if (bytesRemaining != 0) {
	            int blkIndex = getBlockIndex(blkLocations, length-bytesRemaining);
	            splits.add(makeSplit(path, length-bytesRemaining, bytesRemaining,
	                       blkLocations[blkIndex].getHosts(),
	                       blkLocations[blkIndex].getCachedHosts()));
	          }
	        } else { // not splitable
	          splits.add(makeSplit(path, 0, length, blkLocations[0].getHosts(),
	                      blkLocations[0].getCachedHosts()));
	        }
	      } else { 
	        //Create empty hosts array for zero length files
	        splits.add(makeSplit(path, 0, length, new String[0]));
	      }
	    }
	    // Save the number of input files for metrics/loadgen
    job.getConfiguration().setLong(NUM_INPUT_FILES, files.size());
   sw.stop();
	    
	    System.out.println("分片数量" + splits.size());
	    
	    return splits;
	}
	
	

}
