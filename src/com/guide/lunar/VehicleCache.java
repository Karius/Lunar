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
	
	public final static String  TABLE_DATABASE_INFO = "ViolationDatabaseInfo"; // ��վ�ϵ�Υ�����ݿ�������Ϣ����Ҫ�Ǹ�������
	public final static String	TABLE_VEHICLE_INFO = "VehicleInfo"; // ��������ƺš��������ŵ���ʷ��Ϣ��
	public final static String	TABLE_VIOLATION_INDEX = "ViolationIndex"; // Υ����Ϣ������
	public final static String	TABLE_VIOLATION_DATA = "ViolationCache"; // ���Υ����Ϣ�������ݱ�
	
	// ����
	// ��ȡ��վ���ݿ��������ʱ�ľ�������
	public final static String COLUMN_QUERY_DATABASE_DATE = "databaseQueryDate";

	//���ݿ��������
	public final static String COLUMN_DATABASE_UPDATE_DATE = "databaseUpdateDate";
	//�Ƿ񱾵�Υ��
	public final static String COLUMN_IS_NONLOCAL = "isNonlocal";

	//��������
	public final static String COLUMN_VEHICLE_TYPE = "vehicleType";
	//���ƺ���
	public final static String COLUMN_LICENSE_NUMBER = "licenseNumber";
	//��������
	public final static String COLUMN_ENGINE_NUMBER = "engineNumber";
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
			String sqlStr[] = new String []{
					String.format("CREATE TABLE \"%s\"(" +
									"[id] integer PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL" +
									",[%s] text NOT NULL COLLATE NOCASE" +
									",[%s] text NOT NULL COLLATE NOCASE" +
									");",
									TABLE_DATABASE_INFO,
									COLUMN_QUERY_DATABASE_DATE,
									COLUMN_DATABASE_UPDATE_DATE),
					
					String.format("CREATE TABLE \"%s\"(" +
									"[id] integer PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL" +
									",[vehicleType] text NOT NULL COLLATE NOCASE" +
									",[licenseNumber] text NOT NULL UNIQUE COLLATE NOCASE" +
									",[engineNumber] text NOT NULL COLLATE NOCASE" +
									");",
									TABLE_VEHICLE_INFO),

					String.format("CREATE TABLE \"%s\" (" + 
									"[id] integer PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL" +
									",[vehicleType] text NOT NULL COLLATE NOCASE" +
									",[licenseNumber] text UNIQUE NOT NULL COLLATE NOCASE" +
									",[engineNumber] text NOT NULL COLLATE NOCASE" +
									",[databaseUpdateDate] text NOT NULL COLLATE NOCASE" +
									");",
									TABLE_VIOLATION_INDEX),

					String.format("CREATE TABLE \"%s\" (" + 
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
									TABLE_VIOLATION_DATA)
			};
			
			for(int i=0;i<sqlStr.length;i++)
				db.execSQL(sqlStr[i]);
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

	// �������ݿ����������Ϣ
	public static class RemoteDatabaseInfo {
		public Date databaseDate = null;
		public Date queryDate = null;

		public final static String queryDateFormat = "yyyy-MM-dd HH:mm:ss";
		public final static String databaseDateFormat = "yyyy-MM-dd";
	}

	
	// ɾ����վ���ݿ���Ϣ
	private void clearRemoteDatabaseInfo () {
		dbOpen ();
		db.delete(TABLE_DATABASE_INFO, null, null);		
		dbClose ();
	}

	// д����վ���ݿ���Ϣ
	public void writeRemoteDatabaseInfo (Date databaseUpdateDate) {
		
		// ����ɾ��������
		clearRemoteDatabaseInfo ();

		dbOpen ();
		
		ContentValues cv = new ContentValues ();
		
		cv.put(COLUMN_QUERY_DATABASE_DATE, Utility.Date2Str(new Date (), RemoteDatabaseInfo.queryDateFormat));
		cv.put(COLUMN_DATABASE_UPDATE_DATE, Utility.Date2Str(databaseUpdateDate, RemoteDatabaseInfo.databaseDateFormat));
		db.insert(TABLE_DATABASE_INFO, null, cv);
		
		dbClose ();
	}

	// ��ȡ��վ���ݿ���Ϣ
	public RemoteDatabaseInfo readRemoteDatabaseInfo () {
		dbOpen ();
		
		RemoteDatabaseInfo rdi = null;
		
		Cursor c = db.query (TABLE_DATABASE_INFO,
							null,
							null,
							null,
							null,null,
							null);
		
		if (c.moveToFirst()) {
			rdi = new RemoteDatabaseInfo ();
			rdi.queryDate = Utility.Str2Date(
									c.getString(c.getColumnIndexOrThrow(COLUMN_QUERY_DATABASE_DATE)), 
									RemoteDatabaseInfo.queryDateFormat);
			rdi.databaseDate = Utility.Str2Date(
									c.getString(c.getColumnIndexOrThrow(COLUMN_DATABASE_UPDATE_DATE)),
									RemoteDatabaseInfo.databaseDateFormat);
			
			if (null == rdi.queryDate || null == rdi.databaseDate) {
				rdi = null;
			}
		}
		
		dbClose ();
		
		return rdi;
	}
	
	// �������ݿ���е������Ϣ���²��ȡ��վ���ݿ��������
	public String getDatabaseUpdateDate () {
		// �����ݿ⻺���ж�ȡΥ�����ݿ��������
		RemoteDatabaseInfo rdi = readRemoteDatabaseInfo ();
		
		// ���ݿ����޼�¼�򷵻�null����ʾ��Ҫ����վ����Υ�����ݿ���Ϣ
		if (null == rdi) {
			return null;
		}
		
		Date currDate = new Date ();

		long days = Utility.getDayBetween(rdi.databaseDate, currDate);
		long minutes = Utility.getMinuteBetween(rdi.queryDate, currDate);
		
		// Ϊͬһ�죬����С�����죬�������վ����������Ϣ��ֱ�� ʹ�����ݿ⻺���е�������Ϣ
		if ((days <= 1) 
			|| (days >= 2 && minutes < 60)) {
			return Utility.Date2Str(rdi.databaseDate, RemoteDatabaseInfo.databaseDateFormat);			
		}
		return null;
	}

	/////////////////////////////////////////////////////////////////////////////////
	// ��ȡ���г�������
	public List<ViolationManager.VehicleData> queryAllVehicleInfo () {
		List<ViolationManager.VehicleData> l = new ArrayList<ViolationManager.VehicleData> ();
		
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
						new ViolationManager.VehicleData (
								c.getString(c.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE)),
								c.getString(c.getColumnIndexOrThrow(COLUMN_LICENSE_NUMBER)),
								c.getString(c.getColumnIndexOrThrow(COLUMN_ENGINE_NUMBER))
								)
					);
			} while (c.moveToNext());
		}
		
		dbClose ();
		return l;
	}

	// ���ݳ��ƺ����ѯ����������ʷ��Ϣ
	public ViolationManager.VehicleData queryVehicleInfo (String licenseNumber) {
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
		ViolationManager.VehicleData vd = null;

		if (c.moveToFirst()) {
			vd = new ViolationManager.VehicleData (
					c.getString(c.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE)),
					c.getString(c.getColumnIndexOrThrow(COLUMN_LICENSE_NUMBER)),
					c.getString(c.getColumnIndexOrThrow(COLUMN_ENGINE_NUMBER))
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
	public boolean addVehicleInfo (ViolationManager.VehicleData vd) {
		// һ����Ҫ�����Ȳ����ݱ����Ƿ��Ѵ��ڸó�������Ϣ����
		// ���ñ��ڴ���ʱ�����ƺ���ʹ����UNIQUE�ؼ��֣����Ա�֤��Ψһ��
		// �������ﲻ��Ҫ����Ƿ����иó����ݡ�ֱ�Ӳ������ݼ��ɣ�����������

		ContentValues cv = new ContentValues ();

		cv.put(COLUMN_VEHICLE_TYPE, vd.vehicleType);
		cv.put(COLUMN_LICENSE_NUMBER, vd.licenseNumber);
		cv.put(COLUMN_ENGINE_NUMBER, vd.engineNumber);
		
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
		cv.put(COLUMN_VEHICLE_TYPE, vm.getVehicleType());
		cv.put(COLUMN_LICENSE_NUMBER, vm.getLicenseNumber());
		cv.put(COLUMN_ENGINE_NUMBER, vm.getEngineNumber());
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
			cv.put(COLUMN_UNLAWFUL_ACTION, vd.unlawfulAction);
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
			cv.put(COLUMN_UNLAWFUL_ACTION, vd.unlawfulAction);
			cv.put(COLUMN_ILLEGAL_LOCATIONS, vd.illegalLocations);			
			db.insert(TABLE_VIOLATION_DATA, null, cv);
		}

		dbClose ();		
	}

	// ���ָ������Υ����Ϣ�Ƿ���ڻ�����
	// ���ؽ����
	// -1 : �����ڸó�����Ϣ
	// 0  : ���ڸó�����Ϣ����������������֮�⣬�������Ż������Ͳ���
	// 1  : ������ó�����ȫһ�µ���Ϣ����
	public int checkViolationCache (ViolationManager.VehicleData vd) {
		int result = -1;
		dbOpen ();
		
		Cursor c = db.query(TABLE_VIOLATION_INDEX, null,
				COLUMN_LICENSE_NUMBER + "=?",
				new String[]{vd.licenseNumber}, 
				null, null, null);

		// ��ѯ��������Ϣ
		if (c.moveToFirst()) {
			result = vd.isSomeVehicle(new ViolationManager.VehicleData(
					c.getString(c.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE)),
					c.getString(c.getColumnIndexOrThrow(COLUMN_LICENSE_NUMBER)),
					c.getString(c.getColumnIndexOrThrow(COLUMN_ENGINE_NUMBER))
					)
			) ? 1 : 0;
		}
				
		dbClose ();
		
		return result;
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
	public ViolationManager queryViolationData (ViolationManager.VehicleData vd) {
		// ��ѯ������¼���Ƿ����ָ��������Υ����Ϣ
		int isExists = checkViolationCache (vd);
		
		ViolationManager vManager = null;

		// ����иó�����Ϣ
		if (1 == isExists) {
			dbOpen ();
			// ��ʼ��ѯ�ó�������Υ��������Ϣ
			Cursor cursor= db.query(TABLE_VIOLATION_DATA, null,
								COLUMN_LICENSE_NUMBER + "=?", 
								new String[]{vd.licenseNumber}, 
								null, null, null);

			vManager = new ViolationManager (vd);
			
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
