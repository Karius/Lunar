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
	
	public final static String  TABLE_DATABASE_INFO = "ViolationDatabaseInfo"; // 网站上的违章数据库的相关信息，主要是更新日期
	public final static String	TABLE_VEHICLE_INFO = "VehicleInfo"; // 存放汽车牌号、发动机号的历史信息表
	public final static String	TABLE_VIOLATION_INDEX = "ViolationIndex"; // 违章信息索引表
	public final static String	TABLE_VIOLATION_DATA = "ViolationCache"; // 存放违章信息缓存数据表
	
	// 列名
	// 获取网站数据库更新日期时的具体日期
	public final static String COLUMN_QUERY_DATABASE_DATE = "databaseQueryDate";

	//数据库更新日期
	public final static String COLUMN_DATABASE_UPDATE_DATE = "databaseUpdateDate";
	//是否本地违章
	public final static String COLUMN_IS_NONLOCAL = "isNonlocal";

	//车辆类型
	public final static String COLUMN_VEHICLE_TYPE = "vehicleType";
	//车牌号码
	public final static String COLUMN_LICENSE_NUMBER = "licenseNumber";
	//发动机号
	public final static String COLUMN_ENGINE_NUMBER = "engineNumber";
	//罚单编号
	public final static String COLUMN_TICKET_NUMBER = "ticketNumber";
	//违章时间
	public final static String COLUMN_VIOLATION_DATE = "violationDate";
	//罚款金额
	public final static String COLUMN_FINES = "fines";
	//违法行为
	public final static String COLUMN_UNLAWFUL_ACTION = "unlawfulAction";
	//违法地点
	public final static String COLUMN_ILLEGAL_LOCATIONS = "illegalLocations";
	//处罚结果
	public final static String COLUMN_PUNISHMENT_RESULT = "punishmentResults";
	//备注
	public final static String COLUMN_COMMENT = "comment";


	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper (Context context) {
			// 在Android 中创建和打开一个数据库都可以使用openOrCreateDatabase 方法来实现，  
	        // 因为它会自动去检测是否存在这个数据库，如果存在则打开，不过不存在则创建一个数据库；  
	        // 创建成功则返回一个 SQLiteDatabase对象，否则抛出异常FileNotFoundException。  
	        // 下面是来创建一个名为"DATABASE_NAME"的数据库，并返回一个SQLiteDatabase对象   
			super (context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// 在数据库第一次生成的时候会调用这个方法，一般我们在这个方法里边生成数据库表;
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

	// 操作数据库更新日期信息
	public static class RemoteDatabaseInfo {
		public Date databaseDate = null;
		public Date queryDate = null;

		public final static String queryDateFormat = "yyyy-MM-dd HH:mm:ss";
		public final static String databaseDateFormat = "yyyy-MM-dd";
	}

	
	// 删除网站数据库信息
	private void clearRemoteDatabaseInfo () {
		dbOpen ();
		db.delete(TABLE_DATABASE_INFO, null, null);		
		dbClose ();
	}

	// 写入网站数据库信息
	public void writeRemoteDatabaseInfo (Date databaseUpdateDate) {
		
		// 首先删除旧数据
		clearRemoteDatabaseInfo ();

		dbOpen ();
		
		ContentValues cv = new ContentValues ();
		
		cv.put(COLUMN_QUERY_DATABASE_DATE, Utility.Date2Str(new Date (), RemoteDatabaseInfo.queryDateFormat));
		cv.put(COLUMN_DATABASE_UPDATE_DATE, Utility.Date2Str(databaseUpdateDate, RemoteDatabaseInfo.databaseDateFormat));
		db.insert(TABLE_DATABASE_INFO, null, cv);
		
		dbClose ();
	}

	// 读取网站数据库信息
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
	
	// 根据数据库表中的相关信息来猜测获取网站数据库更新日期
	public String getDatabaseUpdateDate () {
		// 从数据库缓存中读取违章数据库更新日期
		RemoteDatabaseInfo rdi = readRemoteDatabaseInfo ();
		
		// 数据库中无记录则返回null，表示需要从网站更新违章数据库信息
		if (null == rdi) {
			return null;
		}
		
		Date currDate = new Date ();

		long days = Utility.getDayBetween(rdi.databaseDate, currDate);
		long minutes = Utility.getMinuteBetween(rdi.queryDate, currDate);
		
		// 为同一天，或者小于两天，则不需从网站更新日期信息，直接 使用数据库缓存中的日期信息
		if ((days <= 1) 
			|| (days >= 2 && minutes < 60)) {
			return Utility.Date2Str(rdi.databaseDate, RemoteDatabaseInfo.databaseDateFormat);			
		}
		return null;
	}

	/////////////////////////////////////////////////////////////////////////////////
	// 获取所有车辆数据
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

	// 根据车牌号码查询车辆数据历史信息
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

	// 删除指定车辆数据历史信息
	// licenseNumber: 车辆牌照
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

	// 新增车辆信息
	public boolean addVehicleInfo (ViolationManager.VehicleData vd) {
		// 一般需要首先先查数据表中是否已存在该车辆的信息数据
		// 但该表在创建时，车牌号列使用了UNIQUE关键字，用以保证其唯一性
		// 所以这里不需要检查是否已有该车数据。直接插入数据即可，如有则会出错。

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
	// 违章数据处理功能


	// 删除指定车辆违章缓存
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

	// 增加指定车辆的违章信息
	public void addViolationData (Date currDatabaseDate, ViolationManager vm) {
		// 首先检查之前记录的车辆数据日期是否为当前数据库日期
		Date d = queryViolationDatabaseDate (vm.getLicenseNumber ());
		
		// 如果缓存中的记录为最新记录则返回
		if (d != null && !currDatabaseDate.after(d)) {
			return;
		}
		
		// 删除之前的车辆违章记录
		deleteViolationCache (vm.getLicenseNumber());
		
		dbOpen ();

		// 在索引表中插入车辆违章数据库日期记录
		ContentValues cv = new ContentValues ();
		cv.put(COLUMN_DATABASE_UPDATE_DATE, Utility.Date2Str(currDatabaseDate, "yyyy-MM-dd"));
		cv.put(COLUMN_VEHICLE_TYPE, vm.getVehicleType());
		cv.put(COLUMN_LICENSE_NUMBER, vm.getLicenseNumber());
		cv.put(COLUMN_ENGINE_NUMBER, vm.getEngineNumber());
		db.insert(TABLE_VIOLATION_INDEX, null, cv);
		
		// 在违章数据表中插入车辆违章数据记录
		// 本地违章记录
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
		
		// 异地违章记录
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

	// 检查指定车辆违章信息是否存在缓存中
	// 返回结果：
	// -1 : 不存在该车辆信息
	// 0  : 存在该车辆信息，但车辆除了牌照之外，发动机号或车辆类型不符
	// 1  : 存在与该车辆完全一致的信息数据
	public int checkViolationCache (ViolationManager.VehicleData vd) {
		int result = -1;
		dbOpen ();
		
		Cursor c = db.query(TABLE_VIOLATION_INDEX, null,
				COLUMN_LICENSE_NUMBER + "=?",
				new String[]{vd.licenseNumber}, 
				null, null, null);

		// 查询到车辆信息
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

	// 查询指定车辆违章数据在缓存中的更新日期
	// 如无车辆信息，则返回null
	public Date queryViolationDatabaseDate (String licenseNumber) {
		dbOpen ();

		// 查询索引记录表，是否存在指定车辆的违章信息
		Cursor c = db.query(TABLE_VIOLATION_INDEX, null,
				COLUMN_LICENSE_NUMBER + "=?",
				new String[]{licenseNumber}, 
				null, null, null);

		String updateDate = null;
		
		// 查询到车辆信息
		if (c.moveToFirst()) {
			updateDate = c.getString(c.getColumnIndexOrThrow(COLUMN_DATABASE_UPDATE_DATE));
		}
		
		dbClose ();

		return Utility.Str2Date(updateDate, "yyyy-MM-dd");
	}

	// 获取指定车辆的违章信息
	public ViolationManager queryViolationData (ViolationManager.VehicleData vd) {
		// 查询索引记录表，是否存在指定车辆的违章信息
		int isExists = checkViolationCache (vd);
		
		ViolationManager vManager = null;

		// 如果有该车辆信息
		if (1 == isExists) {
			dbOpen ();
			// 开始查询该车辆所有违章数据信息
			Cursor cursor= db.query(TABLE_VIOLATION_DATA, null,
								COLUMN_LICENSE_NUMBER + "=?", 
								new String[]{vd.licenseNumber}, 
								null, null, null);

			vManager = new ViolationManager (vd);
			
			// 将该车辆的违章信息放到 ViolatioManager中
			if (cursor.moveToFirst()) {
				do {
					// 该项违章记录是否为本地违章
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
