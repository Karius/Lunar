package com.guide.lunar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VehicleCache {

	public final static int		DATABASE_VERSION = 1;
	public final static String	DATABASE_NAME = "DataCache.db";
	
	public final static String	TABLE_VEHICLE_INFO = "VehicleInfo"; // ��������ƺš��������ŵ���ʷ��Ϣ��
	public final static String	TABLE_VIOLATION_INDEX = "ViolationIndex"; // Υ����Ϣ������
	public final static String	TABLE_VIOLATION_DATA = "ViolationCache"; // ���Υ����Ϣ�������ݱ�
	
	// ����
	//���ݿ��������
	public final static String COLUMN_DATABASE_UPDATE_DATE = "databaseUpdateDate";
	//�Ƿ񱾵�Υ��
	public final static String COLUMN_IS_NONLOCAL = "isNonlocal";

	//��������
	public final static String COLUMN_VEHICLE_TYPE = "vehicleType";
	//���ƺ���
	public final static String COLUMN_LICENSE_NUMBER = "licenseNumber";
	//��������
	public final static String COLUMN_ENGINE_ID = "engineId";
	//�������
	public final static String COLUMN_TICKET_NUMBER = "ticketNumber";
	//Υ��ʱ��
	public final static String COLUMN_VIOLATION_DATE = "violationDate";
	//������
	public final static String COLUMN_FINES = "fines";
	//Υ����Ϊ
	public final static String COLUMN_UNLAWFUL_ACTION = "unlawfulAction";
	//Υ���ص�
	public final static String COLUMN_ILLEGAL_LOCATIONS = "illegalLocations";
	//�������
	public final static String COLUMN_PUNISHMENT_RESULT = "punishmentResults";
	//��ע
	public final static String COLUMN_COMMENT = "comment";

	// ������ع���Ϣ
	public static class VehicleData {
		// ��������
		public String type;
		// ���ƺ���
		public String licenseNumber;
		// ��������
		public String engineId;
		
		public VehicleData (String type, String licenseNumber, String engineId) {
			this.type = type;
			this.licenseNumber = licenseNumber;
			this.engineId = engineId;
		}
	}

	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper (Context context) {
			// ��Android �д����ʹ�һ�����ݿⶼ����ʹ��openOrCreateDatabase ������ʵ�֣�  
	        // ��Ϊ�����Զ�ȥ����Ƿ����������ݿ⣬���������򿪣������������򴴽�һ�����ݿ⣻  
	        // �����ɹ��򷵻�һ�� SQLiteDatabase���󣬷����׳��쳣FileNotFoundException��  
	        // ������������һ����Ϊ"DATABASE_NAME"�����ݿ⣬������һ��SQLiteDatabase����   
			super (context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// �����ݿ��һ�����ɵ�ʱ���������������һ�������������������������ݿ��;
		// http://panxq0809.iteye.com/blog/708439
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String sqlStr = String.format("CREATE TABLE \"%s\"(" +
											"[id] integer PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL" +
											",[vehicleType] text NOT NULL COLLATE NOCASE" +
											",[licenseNumber] text NOT NULL UNIQUE COLLATE NOCASE" +
											",[engineId] text NOT NULL COLLATE NOCASE" +
											");",
											TABLE_VEHICLE_INFO);
			db.execSQL(sqlStr);

			sqlStr = String.format(
											"CREATE TABLE \"%s\" (" + 
											"[id] integer PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL" +
											",[licenseNumber] text NOT NULL UNIQUE COLLATE NOCASE" +
											",[databaseUpdateDate] text COLLATE NOCASE" +
											");",
											TABLE_VIOLATION_INDEX);
			db.execSQL(sqlStr);

			sqlStr = String.format(
											"CREATE TABLE \"%s\" (" + 
											"[id] integer PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL" +
											",[isNonlocal] integer" +
											",[vehicleType] text COLLATE NOCASE" + 
											",[licenseNumber] text NOT NULL COLLATE NOCASE" + 
											",[ticketNumber] text COLLATE NOCASE" + 
											",[violationDate] text COLLATE NOCASE" + 
											",[fines] integer" + 
											",[unlawfulAction] text COLLATE NOCASE" + 
											",[illegalLocations] text COLLATE NOCASE" + 
											",[punishmentResults] text COLLATE NOCASE" + 
											",[comment] text COLLATE NOCASE" + 
											");",
											TABLE_VIOLATION_DATA);		
			db.execSQL(sqlStr);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
		}
	}

	private DatabaseHelper mOpenHelper;
	private SQLiteDatabase db;

	public VehicleCache (Context context) {
		mOpenHelper = new DatabaseHelper (context);
	}
	
	private boolean dbOpen () {
		if (dbIsOpen ()) {
			return true;
		}
		db = mOpenHelper.getWritableDatabase();
		return db != null;
	}
	
	private void dbClose () {
		if (dbIsOpen()){
			db.close();
		}
	}
	
	private boolean dbIsOpen () {
		return (db != null) ? db.isOpen() : false;
	}
	
	private boolean dbIsReadOnly () {
		return (dbIsOpen()) ? db.isReadOnly() : true;
	}


	// ��ȡ���г�������
	public List<VehicleData> queryAllVehicleInfo () {
		List<VehicleData> l = new ArrayList<VehicleData> ();
		
		dbOpen ();

		Cursor c = db.query(TABLE_VEHICLE_INFO,
							null,
							null,
							null,
							null, null,
							null);
		

		if (c.moveToFirst()){
			do {
				l.add(
						new VehicleData (
								c.getString(c.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE)),
								c.getString(c.getColumnIndexOrThrow(COLUMN_LICENSE_NUMBER)),
								c.getString(c.getColumnIndexOrThrow(COLUMN_ENGINE_ID))
								)
					);
			} while (c.moveToNext());
		}
		
		dbClose ();
		return l;
	}

	// ���ݳ��ƺ����ѯ����������ʷ��Ϣ
	public VehicleData queryVehicleInfo (String licenseNumber) {
//		String sqlStr = String.format("SELECT * FROM %s " +
//										"WHERE %s=%s",
//										TABLE_VEHICLE_INFO,
//										COLUMN_LICENSE_NUMBER,
//										licenseNumber
//										);

		dbOpen ();

		Cursor c = db.query(TABLE_VEHICLE_INFO,
							null,
							COLUMN_LICENSE_NUMBER + "=?",
							new String[]{licenseNumber},
							null, null,
							"DESC");
		VehicleData vd = null;

		if (c.moveToFirst()) {
			vd = new VehicleData (
					c.getString(c.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE)),
					c.getString(c.getColumnIndexOrThrow(COLUMN_LICENSE_NUMBER)),
					c.getString(c.getColumnIndexOrThrow(COLUMN_ENGINE_ID))
				);
		}

		dbClose ();
		return vd;
	}

	// ɾ��ָ������������ʷ��Ϣ
	// licenseNumber: ��������
	public boolean deleteVehicleInfo (String licenseNumber){
//		String sqlStr = String.format("DELETE FROM %s " +
//										"WHERE %s=\"%s\"",
//										TABLE_VEHICLE_INFO,
//										COLUMN_LICENSE_NUMBER,
//										licenseNumber);
//		db.execSQL(sqlStr);

		dbOpen ();

		db.delete(TABLE_VEHICLE_INFO,
					COLUMN_LICENSE_NUMBER + "=?",
					new String[] {licenseNumber});
		
		dbClose ();
		return true;
	}

	// ����������Ϣ
	public boolean addVehicleInfo (VehicleData vd) {
		// һ����Ҫ�����Ȳ����ݱ����Ƿ��Ѵ��ڸó�������Ϣ����
		// ���ñ��ڴ���ʱ�����ƺ���ʹ����UNIQUE�ؼ��֣����Ա�֤��Ψһ��
		// �������ﲻ��Ҫ����Ƿ����иó����ݡ�ֱ�Ӳ������ݼ��ɣ�����������

		ContentValues cv = new ContentValues ();

		cv.put(COLUMN_VEHICLE_TYPE, vd.type);
		cv.put(COLUMN_LICENSE_NUMBER, vd.licenseNumber);
		cv.put(COLUMN_ENGINE_ID, vd.engineId);
		
		dbOpen ();
		db.insert(TABLE_VEHICLE_INFO, null, cv);
		dbClose ();
		return true;
	}

	///////////////////////////////////////////////////////////////
	// Υ�����ݴ�����


	// ɾ��ָ������Υ�»���
	public void deleteViolationCache (String licenseNumber) {
		dbOpen ();

		db.delete(TABLE_VIOLATION_INDEX,
				COLUMN_LICENSE_NUMBER + "=?",
				new String[]{licenseNumber});
		db.delete(TABLE_VIOLATION_DATA,
				COLUMN_LICENSE_NUMBER + "=?",
				new String[]{licenseNumber});
		
		dbClose ();
	}

	// ����ָ��������Υ����Ϣ
	public void addViolationData (Date currDatabaseDate, ViolationManager vm) {
		// ���ȼ��֮ǰ��¼�ĳ������������Ƿ�Ϊ��ǰ���ݿ�����
		Date d = queryViolationDatabaseDate (vm.getLicenseNumber ());
		
		// ��������еļ�¼Ϊ���¼�¼�򷵻�
		if (d != null && !currDatabaseDate.after(d)) {
			return;
		}
		
		// ɾ��֮ǰ�ĳ���Υ�¼�¼
		deleteViolationCache (vm.getLicenseNumber());
		
		dbOpen ();

		// ���������в��복��Υ�����ݿ����ڼ�¼
		ContentValues cv = new ContentValues ();
		cv.put(COLUMN_DATABASE_UPDATE_DATE, Utility.Date2Str(currDatabaseDate, "yyyy-MM-dd"));
		cv.put(COLUMN_LICENSE_NUMBER, vm.getLicenseNumber());
		db.insert(TABLE_VIOLATION_INDEX, null, cv);
		
		// ��Υ�����ݱ��в��복��Υ�����ݼ�¼
		// ����Υ�¼�¼
		for (int i=0;i<vm.localList().size();i++) {
			ViolationManager.ViolationData vd = vm.localList().getList().get(i);
			cv.clear();
			cv.put(COLUMN_IS_NONLOCAL, 0);
			cv.put(COLUMN_VEHICLE_TYPE, vd.licenseType);
			cv.put(COLUMN_LICENSE_NUMBER, vd.licenseNumber);			
			cv.put(COLUMN_VIOLATION_DATE, vd.violationDateStr);
			cv.put(COLUMN_ILLEGAL_LOCATIONS, vd.illegalLocations);
			cv.put(COLUMN_UNLAWFUL_ACTION, vd.trafficViolations);
			cv.put(COLUMN_PUNISHMENT_RESULT, vd.punishmentResults);
			cv.put(COLUMN_COMMENT, vd.comment);
			db.insert(TABLE_VIOLATION_DATA, null, cv);
		}
		
		// ���Υ�¼�¼
		for (int i=0;i<vm.nonlocalList().size();i++) {
			ViolationManager.ViolationData vd = vm.nonlocalList().getList().get(i);
			cv.clear();
			cv.put(COLUMN_IS_NONLOCAL, 1);
			cv.put(COLUMN_VEHICLE_TYPE, vd.licenseType);
			cv.put(COLUMN_LICENSE_NUMBER, vd.licenseNumber);
			cv.put(COLUMN_TICKET_NUMBER, vd.ticketNumber);
			cv.put(COLUMN_VIOLATION_DATE, vd.violationDateStr);
			cv.put(COLUMN_FINES, vd.fines);
			cv.put(COLUMN_UNLAWFUL_ACTION, vd.trafficViolations);
			cv.put(COLUMN_ILLEGAL_LOCATIONS, vd.illegalLocations);			
			db.insert(TABLE_VIOLATION_DATA, null, cv);
		}

		dbClose ();		
	}
	
	// ��ѯָ������Υ�������ڻ����еĸ�������
	// ���޳�����Ϣ���򷵻�null
	public Date queryViolationDatabaseDate (String licenseNumber) {
		dbOpen ();

		// ��ѯ������¼���Ƿ����ָ��������Υ����Ϣ
		Cursor c = db.query(TABLE_VIOLATION_INDEX, null,
				COLUMN_LICENSE_NUMBER + "=?",
				new String[]{licenseNumber}, 
				null, null, null);

		String updateDate = null;
		
		// ��ѯ��������Ϣ
		if (c.moveToFirst()) {
			updateDate = c.getString(c.getColumnIndexOrThrow(COLUMN_DATABASE_UPDATE_DATE));
		}
		
		dbClose ();

		return Utility.Str2Date(updateDate, "yyyy-MM-dd");
	}

	// ��ȡָ��������Υ����Ϣ
	public ViolationManager queryViolationData (String licenseNumber, Date currDatebaseDate) {
		// ��ѯ������¼���Ƿ����ָ��������Υ����Ϣ
		Date updateDate = queryViolationDatabaseDate (licenseNumber);
		
		ViolationManager vManager = null;
		
		// ����в���Ϊ�������ݿ����ڣ���ȡ����ѯ��Υ����Ϣʱ�����ݿ�����
		if (null != updateDate && !currDatebaseDate.after(updateDate)) {
			dbOpen ();
			// ��ʼ��ѯ�ó�������Υ��������Ϣ
			Cursor cursor= db.query(TABLE_VIOLATION_DATA, null,
								COLUMN_LICENSE_NUMBER + "=?", 
								new String[]{licenseNumber}, 
								null, null, null);
			
			vManager = new ViolationManager (licenseNumber);
			
			// ���ó�����Υ����Ϣ�ŵ� ViolatioManager��
			if (cursor.moveToFirst()) {
				do {
					// ����Υ�¼�¼�Ƿ�Ϊ����Υ��
					int isNonlocal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_NONLOCAL));
					
					//vManager.nonlocalList()
					if (isNonlocal <= 0) {
						vManager.add(
							vManager.newData(ViolationManager.LOCAL).fillData(
									cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE)),
									cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LICENSE_NUMBER)),
									cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIOLATION_DATE)),
									cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ILLEGAL_LOCATIONS)),
									cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNLAWFUL_ACTION)),
									cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUNISHMENT_RESULT)),
									cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT))
						));
					} else {
						vManager.add(
								vManager.newData(ViolationManager.NONLOCAL).fillData(
										cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE)),
										cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LICENSE_NUMBER)),
										cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TICKET_NUMBER)),
										cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIOLATION_DATE)),
										Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FINES))),
										cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNLAWFUL_ACTION)),
										cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ILLEGAL_LOCATIONS))										
										));
					}
					
				}while (cursor.moveToNext());				
			}
			
			dbClose ();
		}
		
	
		return vManager;
	}
}
