package com.guide.lunar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class ViolationManager {

    public static final int LOCAL    = 1;
    public static final int NONLOCAL = 2;

	// Υ�³�����ع���Ϣ
	public static class VehicleData {
		// ��������
		public String vehicleType;
		// ���ƺ���
		public String licenseNumber;
		// ��������
		public String engineNumber;
		
		public VehicleData (String type, String licenseNumber, String engineNumber) {
			this.vehicleType = type;
			this.licenseNumber = licenseNumber;
			this.engineNumber = engineNumber;
		}

		public boolean isSomeVehicle (VehicleData vd) {
	    	return isSomeLicenseNumber (vd.licenseNumber)
	    			&& isSomeEngineNumber (vd.engineNumber)
	    			&& isSomeVehicleType (vd.vehicleType);
	    }

	    public boolean isSomeLicenseNumber (String licenseNumber) {
	    	return this.licenseNumber.equalsIgnoreCase(licenseNumber);
	    }
	    
	    public boolean isSomeEngineNumber (String engineNumber) {
	    	return this.engineNumber.equalsIgnoreCase(engineNumber);
	    }
	    
	    public boolean isSomeVehicleType (String type) {
	    	return this.vehicleType.equalsIgnoreCase(type);
	    }
	}

	// Υ������
    public class ViolationData {

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
        //public Date violationDate;
        public String violationDateStr;

        // ������
        public int fines;

        // Υ����Ϊ
        public String unlawfulAction;

        // Υ���ص�
        public String illegalLocations;

        // �������
        public String punishmentResults;

        // ��ע
        public String comment;


        // ���캯��
        private ViolationData (int type) {
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
                unlawfulAction = s5;
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
                unlawfulAction = s6;
                illegalLocations = s7;
            }
            return this;
        }    
        
    }
    
    // Υ�����ݼ�����
    public class ViolationDataList {
    	// Υ�����ͣ�����/���
    	int vType;
    	// Υ������List
    	List<ViolationData> vioDataList = new ArrayList<ViolationData> ();
    	
    	// ���캯��
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

    private VehicleData vehicleData = null;
    private ViolationDataList vLocalList = new ViolationDataList (LOCAL);
    private ViolationDataList vNonlocalList = new ViolationDataList (NONLOCAL);


    public ViolationManager (VehicleData vd) {
    	this.vehicleData = vd;
    }
    
    public final String getLicenseNumber () {
    	return vehicleData.licenseNumber;
    }
    
    public final String getVehicleType () {
    	return vehicleData.vehicleType;
    }
    
    public final String getEngineNumber () {
    	return vehicleData.engineNumber;
    }

    public boolean isSomeLicenseNumber (String licenseNumber) {
    	return vehicleData.isSomeLicenseNumber(licenseNumber);
    }
    
    public boolean isSomeEngineNumber (String engineNumber) {
    	return vehicleData.isSomeEngineNumber(engineNumber);
    }
    
    public boolean isSomeVehicleType (String type) {
    	return vehicleData.isSomeVehicleType(type);
    }

    public boolean isSomeVehicle (VehicleData vd) {
    	return vehicleData.isSomeVehicle(vd);
    }

    public ViolationData newData (int vType) {
        return new ViolationData (vType);
    }

    public ViolationManager add (ViolationData data) {
    	if (!isSomeLicenseNumber (data.licenseNumber)) {
    		return null;
    	}
    	if (LOCAL == data.vType) {
    		vLocalList.add (data);
    	} else if (NONLOCAL == data.vType) {
    		vNonlocalList.add (data);
    	}
        return this;
    }

    public final ViolationDataList localList () {
        return vLocalList;
    }

    public final ViolationDataList nonlocalList () {
        return vNonlocalList;
    }

}
