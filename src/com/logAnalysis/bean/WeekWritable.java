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
public class WeekWritable implements Writable, DBWritable {
	String ip;
	Integer visitWeek;
	Long visitSum;
	Double trend;

	public WeekWritable() {

	}

	public WeekWritable(String ip, Integer visitWeek, Long visitSum, Double trend) {
		this.ip = ip;
		this.visitWeek = visitWeek;
		this.visitSum = visitSum;
		this.trend = trend;
	}

	@Override
	public void write(PreparedStatement statement) throws SQLException {
		statement.setString(1, this.ip);
		statement.setInt(2, this.visitWeek);
		statement.setLong(3, this.visitSum);
		statement.setDouble(4, this.trend);
	}

	@Override
	public void readFields(ResultSet resultSet) throws SQLException {
		this.ip = resultSet.getString(1);
		this.visitWeek = resultSet.getInt(2);
		this.visitSum = resultSet.getLong(2);
		this.trend = resultSet.getDouble(2);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(this.ip);
		out.writeInt(this.visitWeek);
		out.writeLong(this.visitSum);
		out.writeDouble(this.trend);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.ip = in.readUTF();
		this.visitWeek = in.readInt();
		this.visitSum = in.readLong();
		this.trend = in.readDouble();
	}

	public String toString() {
		return new String(this.ip + " " + this.visitWeek + " " + this.visitSum + " " + this.trend);
	}
}