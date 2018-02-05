package com.logAnalysis.util;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogUtil {
	private int radix = 52;
	public static final String srcIpKey = "srcIp";
	public static final String desIpKey = "desIp";
	public static final String timeKey = "time";
	public List<Map<String, Object>> byte2MapList(String fileName,int page ,int amount) {
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		File file = new File(fileName);
		FileInputStream bis;
		try {
			bis = new FileInputStream(file);
			bis.skip((page-1)*52);
			byte[] bytes = new byte[amount*52];
			bis.read(bytes);
			int multiple = bytes.length / radix;
			for (int n = 0; n < multiple; n++) {
				byte[] unit = new byte[radix];
				System.arraycopy(bytes, radix * n, unit, 0, radix);
				/* 读取的数据不能完全填充缓冲区，会出现连续的"0" */
				if (unit[0] == 0 && unit[1] == 0 && unit[2] == 0 && unit[3] == 0) {
					break;
				}
				/** 分析数据 */
				analyseData(unit, retList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retList;
	}
	public List<Map<String, Object>> byte2MapList1(byte[] bytes) {
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		int multiple = bytes.length / radix;
		for (int n = 0; n < multiple; n++) {
			byte[] unit = new byte[radix];
			System.arraycopy(bytes, radix * n, unit, 0, radix);
			/* 读取的数据不能完全填充缓冲区，会出现连续的"0" */
			if (unit[0] == 0 && unit[1] == 0 && unit[2] == 0 && unit[3] == 0) {
				break;
			}
			/** 分析数据 */
			analyseData(unit, retList);
		}
		return retList;
	}
	public List<Map<String, Object>> byteList2MapList(List<byte[]> byteList){
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < byteList.size(); i++) {
			analyseData(byteList.get(i), retList);
		}
		return retList;
		
	}

	public static Map<String,String> getCustomDataOfBinArray(byte[] data,Map<String,String> map){
		if(null == data || data.length < 52){
			return map;
		}else{
			/* 八个字节：抓包时间（单位：秒） */
			byte[] temp = new byte[8];
			System.arraycopy(data, 0, temp, 0, 8);
			temp = arrayReverse(temp);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			map.put(timeKey, sdf.format(new Timestamp(byte2Long(temp) * 1000)));

			/* 四个字节：源地址(Source Address) */
			temp = new byte[4];
			System.arraycopy(data, 20, temp, 0, 4);
			map.put(srcIpKey, (temp[0] & 0xff) + "." + (temp[1] & 0xff) + "."
					+ (temp[2] & 0xff) + "." + (temp[3] & 0xff));

			/* 四个字节：目的地址(Destination Address) */
			temp = new byte[4];
			System.arraycopy(data, 24, temp, 0, 4);
			map.put(desIpKey, (temp[0] & 0xff) + "." + (temp[1] & 0xff) + "."
					+ (temp[2] & 0xff) + "." + (temp[3] & 0xff));
			
			return map;
		}
	}
	
	/**
	 * 分析数据
	 * 
	 * http://www.360doc.com/content/12/1113/15/3405077_247604573.shtml
	 * 4 ipv4
	 * 6 ipv6
	 * 
	 */
	public static void analyseData(byte[] data, List<Map<String, Object>> list) {
		if (data == null) {
			System.out.println("参数为空！");
			return;
		}
		if (data.length != 52) {
			System.out.println("参数长度不符合要求（52）：" + data.length);
			return;
		}
		Map<String, Object> unionMap = new HashMap<String, Object>();
		/* 八个字节：抓包时间（单位：秒） */
		byte[] temp = new byte[8];
		System.arraycopy(data, 0, temp, 0, 8);
		temp = arrayReverse(temp);
		// datagram.setTime(new Timestamp(byte2Long(temp) * 1000));
		unionMap.put("time", new Timestamp(byte2Long(temp) * 1000));
		/* ------------------ IP ------------------ */

		/* 一个字节：4位版本(Version) + 4位首部长度(IHL:Internet Header Length) */
		temp = new byte[1];
		System.arraycopy(data, 8, temp, 0, 1);
		String binary = Integer.toBinaryString(temp[0]);
		if (binary.length() < 8) {/* 补足长度 */
			int n = 8 - binary.length();
			binary = String.format("%0" + n + "d", 0) + binary;
		}
		String tmp = binary.substring(0, 4);
		// datagram.setVersion(Integer.parseInt(tmp, 2));
		unionMap.put("version", Integer.parseInt(tmp, 2));

		/* 一个正确头部的最小头部长度值是5（表示头部长度为 5*4=20 字节) */
		tmp = binary.substring(4, 8);
		// datagram.setHeaderLength(Integer.parseInt(tmp, 2));
		unionMap.put("headerLength", Integer.parseInt(tmp, 2));

		/* 一个字节：服务类型(Type of Service) */
		/* 服务器类型是按位定义的，每位代表不同的意义 */
		temp = new byte[1];
		System.arraycopy(data, 9, temp, 0, 1);
		binary = Integer.toBinaryString(temp[0]);
		if (binary.length() < 8) {/* 补足长度 */
			int n = 8 - binary.length();
			binary = String.format("%0" + n + "d", 0) + binary;
		}
		// datagram.setServiceType(binary);
		unionMap.put("serviceType", binary);

		/* 两个字节：报文总长度(Total Length) */
		temp = new byte[2];
		System.arraycopy(data, 10, temp, 0, 2);
		// datagram.setTotalLength(byte2Int(temp));
		unionMap.put("totalLength", byte2Int(temp));

		/* 两个字节：识别码(Identification) */
		temp = new byte[2];
		System.arraycopy(data, 12, temp, 0, 2);
		// datagram.setIdentification(byte2Int(temp));
		unionMap.put("identification", byte2Int(temp));

		/* 两个字节：3位控制标记(Flags) + 13位分片偏移(Fragment Offset) */
		temp = new byte[2];
		System.arraycopy(data, 14, temp, 0, 2);
		binary = Integer.toBinaryString(byte2Int(temp));
		if (binary.length() < 16) {/* 补足长度 */
			int n = 16 - binary.length();
			binary = String.format("%0" + n + "d", 0) + binary;
		}
		/* 控制标记是按位定义的，每位代表不同的意义 */
		tmp = binary.substring(0, 3);
		// datagram.setFlags(tmp);
		unionMap.put("flags", tmp);

		tmp = binary.substring(3);
		// datagram.setFragmentOffset(Integer.parseInt(tmp, 2));
		unionMap.put("fragmentOffset", Integer.parseInt(tmp, 2));

		/* 一个字节：生存时间(Time to Live) */
		temp = new byte[1];
		System.arraycopy(data, 16, temp, 0, 1);
		// datagram.setTtl(byte2Int(temp));
		unionMap.put("ttl", byte2Int(temp));

		/* 一个字节：协议(Protocol) */
		/* ”Assigned Numbers”中定义了不同协议的值 */
		temp = new byte[1];
		System.arraycopy(data, 17, temp, 0, 1);
		// datagram.setProtocol(byte2Int(temp));
		unionMap.put("protocol", byte2Int(temp));

		/* 两个字节：头部校验和(Header Checksum) */
		temp = new byte[2];
		System.arraycopy(data, 18, temp, 0, 2);
		// datagram.setHeaderChecksum(byte2Int(temp));
		unionMap.put("headerChecksum", byte2Int(temp));

		/* 四个字节：源地址(Source Address) */
		temp = new byte[4];
		System.arraycopy(data, 20, temp, 0, 4);
		// datagram.setSrcIP((temp[0] & 0xff) + "." + (temp[1] & 0xff) + "."
		// + (temp[2] & 0xff) + "." + (temp[3] & 0xff));
		unionMap.put("srcIP", (temp[0] & 0xff) + "." + (temp[1] & 0xff) + "."
				+ (temp[2] & 0xff) + "." + (temp[3] & 0xff));

		/* 四个字节：目的地址(Destination Address) */
		temp = new byte[4];
		System.arraycopy(data, 24, temp, 0, 4);
		// datagram.setDstIP((temp[0] & 0xff) + "." + (temp[1] & 0xff) + "."
		// + (temp[2] & 0xff) + "." + (temp[3] & 0xff));
		unionMap.put("dstIP", (temp[0] & 0xff) + "." + (temp[1] & 0xff) + "."
				+ (temp[2] & 0xff) + "." + (temp[3] & 0xff));
        if(Integer.valueOf(unionMap.get("protocol").toString())==1){
        	temp = new byte[1];
    		System.arraycopy(data, 28, temp, 0, 1);
    		// datagram.setSrcPort(byte2Int(temp));
    		unionMap.put("srcPort", byte2Int(temp));
    		temp = new byte[1];
    		System.arraycopy(data, 29, temp, 0, 1);
    		// datagram.setDstPort(byte2Int(temp));
    		unionMap.put("dstPort", byte2Int(temp));
    		unionMap.put("sequence", "");
    		unionMap.put("acknowledgement", "");
    		unionMap.put("dataOffset", "");
    		unionMap.put("controlBits", "");
    		unionMap.put("window", "");
    		unionMap.put("checksum", "");
    		unionMap.put("pointer", "");
    		unionMap.put("type", "");
    		list.add(unionMap);
    		return;
		}
		/* ------------------ TCP ------------------ */
		
		/* 两个字节：源端口(Source Port) */
		temp = new byte[2];
		System.arraycopy(data, 28, temp, 0, 2);
		// datagram.setSrcPort(byte2Int(temp));
		unionMap.put("srcPort", byte2Int(temp));

		/* 两个字节：目的端口(Destination Port) */
		temp = new byte[2];
		System.arraycopy(data, 30, temp, 0, 2);
		// datagram.setDstPort(byte2Int(temp));
		unionMap.put("dstPort", byte2Int(temp));

		/* 四个字节：序号(Sequence Number) */
		temp = new byte[4];
		System.arraycopy(data, 32, temp, 0, 4);
		// datagram.setSequence(byte2Int(temp));
		unionMap.put("sequence", byte2Int(temp));

		/* 四个字节：应答号(Acknowledgment Number) */
		temp = new byte[4];
		System.arraycopy(data, 36, temp, 0, 4);
		// datagram.setAcknowledgement(byte2Int(temp));
		unionMap.put("acknowledgement", byte2Int(temp));

		/* 两个字节：4位数据偏移量(Data Offset) + 6位保留位(Reserved) + 6位控制位(Control Bits) */
		temp = new byte[2];
		System.arraycopy(data, 40, temp, 0, 2);
		binary = Integer.toBinaryString(byte2Int(temp));
		if (binary.length() < 16) {/* 长度补足 */
			int n = 16 - binary.length();
			binary = String.format("%0" + n + "d", 0) + binary;/*  */
		}
		tmp = binary.substring(0, 4);
		// datagram.setDataOffset(Integer.parseInt(tmp, 2));
		unionMap.put("dataOffset", Integer.parseInt(tmp, 2));

		tmp = binary.substring(10);
		/* 控制位是按位定义的，每位代表不同的意义 */
		// datagram.setControlBits(tmp);
		unionMap.put("controlBits", tmp);

		/* 两个字节：窗口(Window) */
		temp = new byte[2];
		System.arraycopy(data, 42, temp, 0, 2);
		// datagram.setWindow(byte2Int(temp));
		unionMap.put("window", byte2Int(temp));

		/* 两个字节：校验码(Checksum) */
		temp = new byte[2];
		System.arraycopy(data, 44, temp, 0, 2);
		// datagram.setChecksum(byte2Int(temp));
		unionMap.put("checksum", byte2Int(temp));

		/* 两个字节：紧急指针(Urgent Pointer) */
		temp = new byte[2];
		System.arraycopy(data, 46, temp, 0, 2);
		// datagram.setPointer(byte2Int(temp));
		unionMap.put("pointer", byte2Int(temp));

		/* 四个字节：通信类型（第一个字节用于确定后三个字节的内容：[0:端口 'G':文件扩展名 'P':POST请求，后面3字节固定为0]） */
		temp = new byte[4];
		System.arraycopy(data, 48, temp, 0, 4);
		temp = arrayReverse(temp);
		int type = temp[0] & 0xFF;
		if (type == 0) {
			tmp = "端口：" + byte2Int(temp);
		} else if (type == 'G') {
			byte[] suffix = new byte[3];
			System.arraycopy(temp, 1, suffix, 0, 3);
			tmp = "后缀名：" + new String(suffix);
		} else if (type == 'P') {
			tmp = "POST";
		} else {
			tmp = "未知：" + Arrays.toString(temp);
		}
		// datagram.setType(tmp);
		unionMap.put("type", tmp);

		list.add(unionMap);
	}

	/**
	 * 字节数组反转
	 * 
	 * @param b
	 *            字节数组
	 * @return 反转后的字节数组
	 */
	public static byte[] arrayReverse(byte[] b) {
		int length = b.length;
		byte[] r = new byte[length];
		for (int i = 0; i < length; i++) {
			r[i] = b[length - 1 - i];
		}
		return r;
	}

	/**
	 * 字节数组 -> int
	 * 
	 * @param b
	 *            字节数组（长度1-4）
	 * @return int
	 */
	private static int byte2Int(byte[] b) {
		if (b == null || b.length < 1) {
			System.out.println("字节转整数（byte -> int）出错：参数为空！");
			return -1;
		}
		if (b.length > 4) {
			System.out.println("字节转整数（byte -> int）出错：参数长度超过4！");
			return -1;
		}
		byte[] zero = new byte[4];
		if (b.length < 4) {
			System.arraycopy(b, 0, zero, 4 - b.length, b.length);
		} else {
			zero = b;
		}
		int b1 = zero[0] & 0xff;
		int b2 = zero[1] & 0xff;
		int b3 = zero[2] & 0xff;
		int b4 = zero[3] & 0xff;
		return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
	}

	/**
	 * 字节数组 -> long
	 * 
	 * @param b
	 *            字节数组（长度1-8）
	 * @return long
	 */
	public static long byte2Long(byte[] b) {
		if (b == null || b.length < 1) {
			System.out.println("字节转长整型（byte -> long）出错：参数为空！");
			return -1;
		}
		if (b.length > 8) {
			System.out.println("字节转长整型（byte -> long）出错：参数长度超过8！");
			return -1;
		}
		byte[] zero = new byte[8];
		if (b.length < 8) {
			System.arraycopy(b, 0, zero, 4 - b.length, b.length);
		} else {
			zero = b;
		}
		long b1 = zero[0] & 0xff;
		long b2 = zero[1] & 0xff;
		long b3 = zero[2] & 0xff;
		long b4 = zero[3] & 0xff;
		long b5 = zero[4] & 0xff;
		long b6 = zero[5] & 0xff;
		long b7 = zero[6] & 0xff;
		long b8 = zero[7] & 0xff;
		return (b1 << 56) + (b2 << 48) + (b3 << 40) + (b4 << 32) + (b5 << 24)
				+ (b6 << 16) + (b7 << 8) + (b8 << 0);
	}

}
