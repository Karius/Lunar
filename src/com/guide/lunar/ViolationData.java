package com.guide.lunar;
//����
import java.util.Date;
import java.text.SimpleDateFormat;

public class ViolationData {

    public static final int LOCAL    = 1;
    public static final int NONLOCAL = 2;

    // ��������(���أ����/�ֳ�)
    private int vType;

    // ���ط�����Ϣ
    // :�������ͣ�Υ�����ƣ�Υ��ʱ�䣻Υ���ص㣻Υ����Ϊ�������������ע��
    // ���/�ֳ�������Ϣ
    // :�������ͣ����ƺ��룻������ţ�Υ��ʱ�䣻�����Υ����Ϊ��Υ���ص㣻


    // ��������
    public String licenseType;

    // ���ƺ���
    public String licenseNumber;

    // �������
    public String ticketNumber;

    // Υ��ʱ��
    public Date violationDate;
    public String violationDateStr;

    // ������
    public int fines;

    // Υ����Ϊ
    public String trafficViolations;

    // Υ���ص�
    public String illegalLocations;

    // �������
    public String punishmentResults;

    // ��ע
    public String comment;


    // ���캯��
    public ViolationData (int type) {
        vType = type;
    }

    // ���ս���������ҳ�ϵ���˳���ַ�������дΥ����Ϣ
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
