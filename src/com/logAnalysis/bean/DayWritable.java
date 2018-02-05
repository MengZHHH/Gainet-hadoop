package com.logAnalysis.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

/**
 * 重写DBWritable
 * 
 * @author asheng TblsWritable�?要向mysql中写入数�?
 */
public class DayWritable implements Writable, DBWritable {
	String ip;
	String visitDay;
	Long visitSum;

	public DayWritable() {

	}

	public DayWritable(String ip, String visitDay, Long visitSum) {
		this.ip = ip;
		this.visitDay = visitDay;
		this.visitSum = visitSum;
	}

	@Override
	public void write(PreparedStatement statement) throws SQLException {
		statement.setString(1, this.ip);
		statement.setString(2, this.visitDay);
		statement.setLong(3, this.visitSum);
	}

	@Override
	public void readFields(ResultSet resultSet) throws SQLException {
		this.ip = resultSet.getString(1);
		this.visitDay = resultSet.getString(2);
		this.visitSum = resultSet.getLong(2);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.ip);
		out.writeUTF(this.visitDay);
		out.writeLong(this.visitSum);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.ip = in.readUTF();
		this.visitDay = in.readUTF();
		this.visitSum = in.readLong();
	}

	public String toString() {
		return new String(this.ip + " " + this.visitDay + " " + this.visitSum);
	}
}