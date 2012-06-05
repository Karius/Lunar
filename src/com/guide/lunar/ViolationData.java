package com.guide.lunar;
//好了
import java.util.Date;
import java.text.SimpleDateFormat;

public class ViolationData {

    public static final int LOCAL    = 1;
    public static final int NONLOCAL = 2;

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
    public ViolationData (int type) {
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
