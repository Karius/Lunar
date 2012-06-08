package com.guide.lunar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class ViolationManager {

    public static final int LOCAL    = 1;
    public static final int NONLOCAL = 2;

    public class ViolationData {

        // 罚单类型(本地，异地/现场)
        private int vType;

        // 本地罚单信息
        // :号牌类型；违法号牌；违法时间；违法地点；违法行为；处罚结果；备注；
        // 异地/现场罚单信息
        // :号牌类型；车牌号码；罚单编号；违法时间；罚款金额；违法行为；违法地点；


        // 牌照类型
        public String licenseType;

        // 车牌号码
        public String licenseNumber;

        // 罚单编号
        public String ticketNumber;

        // 违章时间
        public Date violationDate;
        public String violationDateStr;

        // 罚款金额
        public int fines;

        // 违法行为
        public String trafficViolations;

        // 违法地点
        public String illegalLocations;

        // 处罚结果
        public String punishmentResults;

        // 备注
        public String comment;


        // 构造函数
        private ViolationData (int type) {
            vType = type;
        }

        // 按照解析出的网页上的列顺序字符串来填写违章信息
        public ViolationData fillData (String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
            SimpleDateFormat sdt = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

            if (LOCAL == vType) {
                licenseType = s1;
                licenseNumber = s2;
                violationDateStr = s3;
                illegalLocations = s4;
                trafficViolations = s5;
                punishmentResults = s6;
                comment = s7;
            } else if (NONLOCAL == vType) {
                licenseType = s1;
                licenseNumber = s2;
                ticketNumber = s3;
                violationDateStr = s4;
                try {
                    fines = Integer.parseInt (s5);
                } catch (java.lang.NumberFormatException e) {
                    fines = 0;
                }
                trafficViolations = s6;
                illegalLocations = s7;
            }
            return this;
        }    
        
    }
    
    public class ViolationDataList {
    	int vType;
    	List<ViolationData> vioDataList = new ArrayList<ViolationData> ();
    	
    	private ViolationDataList (int vType) {
    		this.vType = vType;
    	}

    	public ViolationDataList add (ViolationData vData) {
    		if (vType != vData.vType) {
    			return null;
    		}

    		vioDataList.add(vData);
    		return this;
    	}

    	public int size () {
    		return vioDataList.size();
    	}
    	
    	public final List<ViolationData> getList () {
    		return vioDataList;
    	}
    }
    
    public ViolationDataList vLocalList = new ViolationDataList (LOCAL);
    public ViolationDataList vNonlocalList = new ViolationDataList (NONLOCAL);


    public ViolationManager () {
    }


    public ViolationData newData (int vType) {
        return new ViolationData (vType);
    }

    public ViolationManager add (ViolationData data) {
    	if (LOCAL == data.vType) {
    		vLocalList.add (data);
    	} else if (NONLOCAL == data.vType) {
    		vNonlocalList.add (data);
    	}
        return this;
    }

    public ViolationDataList localList () {
        return vLocalList;
    }

    public ViolationDataList nonlocalList () {
        return vNonlocalList;
    }

}
