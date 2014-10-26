package com.m.support.sqlite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.m.common.context.GlobalContext;
import com.m.common.settings.Setting;
import com.m.common.settings.SettingUtility;
import com.m.common.utils.Logger;
import com.m.common.utils.SystemUtility;
import com.m.support.sqlite.property.Extra;
import com.m.support.sqlite.property.Property;
import com.m.support.sqlite.property.TableInfo;
import com.m.support.sqlite.util.ClassUtils;
import com.m.support.sqlite.util.FieldUtils;
import com.m.support.sqlite.util.SqlBuilder;

/**
 * sqlite操作帮助类，暂时不支持版本升级
 * 
 * @author wangdan
 */
public class SqliteUtility {

	public static final String TAG = "SqliteUtility";

	private static HashMap<String, SqliteUtility> sqliteHelperMap;
	
	static {
		sqliteHelperMap = new HashMap<String, SqliteUtility>();
	}
	
	private SQLiteDatabase db;
	
	private TableInfo tableInfoUtils;

	private SqliteUtility(SQLiteDatabase db) {
		this.db = db;
		tableInfoUtils = new TableInfo();
	}
	
	public static void remve(String key) {
		SqliteUtility sqliteUtility = sqliteHelperMap.get(key);
		if (sqliteUtility != null) {
			sqliteUtility.db.close();
		}
		sqliteHelperMap.remove(key);
	}
	
	public static void deleteDB(String dbName) {
		try {
			boolean bol = SQLiteDatabase.deleteDatabase(GlobalContext.getInstance().getDatabasePath(dbName));
			
			Logger.w(TAG, String.format("删除DB[dbName = %s]，结果: %s", dbName, String.valueOf(bol)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteSdcardDB(String dbName) {
		try {
			boolean bol = SQLiteDatabase.deleteDatabase(new File(getDBPath(dbName)));
			
			Logger.w(TAG, String.format("删除Sdcard DB[dbName = %s]，结果: %s", dbName, String.valueOf(bol)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 2014-06-19:新增对sdcard和程序内部目录的选择
	 * 
	 * @param key
	 * @param dbName
	 * @return
	 */
	public static SqliteUtility getInstanceInSdcard(String key, String dbName) {
		SqliteUtility sqliteUtility = sqliteHelperMap.get(key);
		
		if (sqliteUtility == null) {
			sqliteUtility = new SqliteUtility(openSdcardDb(dbName));
			
			sqliteHelperMap.put(key, sqliteUtility);
		}
		
		return sqliteUtility;
	}
	
	/**
	 * 2014-06-09:新增多个库的支持
	 * 
	 * @param key
	 * @param dbName
	 * @return
	 */
	public static SqliteUtility getInstanceInApp(String key, String dbName) {
		SqliteUtility sqliteUtility = sqliteHelperMap.get(key);
		
		if (sqliteUtility == null) {
			sqliteUtility = new SqliteUtility(new SqliteDbHelper(dbName).getWritableDatabase());
			
			sqliteHelperMap.put(key, sqliteUtility);
		}
		
		return sqliteUtility;
	}
	
	public static SqliteUtility getInstance() {
		SqliteUtility sqliteUtility = sqliteHelperMap.get("default");
		
		if (sqliteUtility == null) {
			String dbType = SettingUtility.getStringSetting("db_type");
			if ("sdcard".equals(dbType))
				try {
					sqliteUtility = new SqliteUtility(openSdcardDb());
				} catch (Exception e) {
					sqliteUtility = new SqliteUtility(new SqliteDbHelper(SettingUtility.getStringSetting("db_name")).getWritableDatabase());
				}
			else if ("app".equals(dbType))
				sqliteUtility = new SqliteUtility(new SqliteDbHelper(SettingUtility.getStringSetting("db_name")).getWritableDatabase());
			else
				throw new RuntimeException("db_type not setting");

			sqliteHelperMap.put("default", sqliteUtility);
		}
		return sqliteUtility;
	}

	private Object[] getSelectionAndArgs(String idColumnName, Extra extra) {
		String[] argArr = new String[10];
		String[] valueArr = new String[10];

		if (extra != null) {
			int index = 0;
			int indexForValue = 0;
			if (!TextUtils.isEmpty(idColumnName) && !TextUtils.isEmpty(extra.getId())) {
				argArr[index++] = idColumnName;
				valueArr[indexForValue++] = extra.getId();
			}
			if (!TextUtils.isEmpty(extra.getOwner())) {
				argArr[index++] = FieldUtils.OWNER;
				valueArr[indexForValue++] = extra.getOwner();
			}
			if (!TextUtils.isEmpty(extra.getKey())) {
				argArr[index++] = FieldUtils.KEY;
				valueArr[indexForValue++] = extra.getKey();
			}
		}

		StringBuffer sbForArgs = new StringBuffer();
		List<String> valueList = new ArrayList<String>();

		for (int i = 0; i < argArr.length; i++) {
			String arg = argArr[i];
			String value = valueArr[i];
			if (!TextUtils.isEmpty(arg) && !TextUtils.isEmpty(value)) {
				sbForArgs.append(" ").append(arg).append(" = ? ");
				sbForArgs.append("and");

				valueList.add(value);
			}
		}

		String selection = sbForArgs.toString();
		if (!TextUtils.isEmpty(selection)) {
			selection = selection.substring(0, selection.length() - 3);

			String[] selectionArgs = new String[valueList.size()];
			for (int i = 0; i < selectionArgs.length; i++)
				selectionArgs[i] = valueList.get(i);

			return new Object[] { selection, selectionArgs };
		}

		return null;
	}

	public Cursor rawQuery(Class<?> clazz, String sql, String[] selectionArgs) {
		checkTableExist(clazz);

		return getRd().rawQuery(sql, selectionArgs);
	}

	public Cursor query(Class<?> clazz, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy,
			String limit) {
		checkTableExist(clazz);

		return getRd().query(ClassUtils.getTableName(clazz), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}
	
	/**
	 * 插入一个实体，如果已经存在，则更新
	 * 
	 * @param extra
	 * @param entity
	 */
	public <T> void insert(Extra extra, T entity) {
		insert(extra, entity, true);
	}

	public <T> void insert(Extra extra, T entity, boolean autoUpdate) {
		checkTableExist(entity.getClass());

		try {
			Field idField = ClassUtils.getPrimaryKeyField(entity.getClass());
			idField.setAccessible(true);
			String idColName = FieldUtils.getColumnNameByField(idField);

			String selection = null;
			String[] selectionArgs = null;

			if (extra == null)
				extra = new Extra();

			extra.setId(idField.get(entity).toString());
			Object[] o = getSelectionAndArgs(idColName, extra);
			if (o != null) {
				selection = o[0].toString();
				selectionArgs = (String[]) o[1];
			}

			int flag = 0;
			if (autoUpdate) {
				flag = getWd()
						.update(ClassUtils.getTableName(entity.getClass()), ClassUtils.getUpdateContentValues(tableInfoUtils, entity), selection, selectionArgs);
				if (flag > 0)
					Logger.d(TAG, String.format(" 更新 , id = %s, key = %s", extra.getId(), extra.getKey() + ""));
			} else {
				Cursor cursor = getRd().query(ClassUtils.getTableName(entity.getClass()), null, selection, selectionArgs, null, null, null);
				if (cursor.moveToFirst()) {
					flag = 1;
					Logger.d(TAG, String.format(" 已存在，不更新  , id = %s, key = %s", extra.getId(), extra.getKey()));					
				}
			}
			
			if (flag > 0) {
			} else {
				Logger.d(TAG, String.format(" 插入, id = %s, key = %s", extra.getId(), extra.getKey() + ""));
				getWd().insert(ClassUtils.getTableName(entity.getClass()), idColName, ClassUtils.getContentValues(tableInfoUtils, extra, entity));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 2014-08-21，新增批量插入数据
	 * 
	 * @param extra
	 * @param entities
	 * @param autoUpdate
	 */
	public <T> void insertBatchList(List<SqliteBatchBean<T>> batch, boolean autoUpdate) {
		if (batch != null && batch.size() > 0) {
			getRd().beginTransaction();

			long time = System.currentTimeMillis();
			Logger.i(TAG, "*******************************************");
			Logger.i(TAG, "begin insert List");

			int count = 0;
			try {
				for (SqliteBatchBean<T> bean : batch) {
					Extra extra = bean.extra; 
					List<T> entities = bean.list;
					
					if (entities == null || entities.size() == 0)
						continue;
					
					count += entities.size();
					
					Class<?> clazz = entities.get(0).getClass();

					checkTableExist(clazz);

					for (T entity : entities) {
						if (extra == null)
							extra = new Extra();

						// id属性
						Field idField = ClassUtils.getPrimaryKeyField(entity.getClass());
						idField.setAccessible(true);
						String idColName = FieldUtils.getColumnNameByField(idField);

						String selection = null;
						String[] selectionArgs = null;

						extra.setId(idField.get(entity).toString());

						Object[] o = getSelectionAndArgs(idColName, extra);
						if (o != null) {
							selection = o[0].toString();
							selectionArgs = (String[]) o[1];
						}

						int flag = 0;
						if (selection != null && selectionArgs != null) {
							if (autoUpdate) {
								flag = getWd().update(ClassUtils.getTableName(entity.getClass()), ClassUtils.getUpdateContentValues(tableInfoUtils, entity), selection,
										selectionArgs);
								
								if (flag > 0)
									Logger.v(TAG, String.format(" 更新已存在, id = %s, key = %s", extra.getId(), extra.getKey() + ""));
							} else {
								Cursor cursor = getRd().query(ClassUtils.getTableName(entity.getClass()), null, selection, selectionArgs, null, null, null);
								if (cursor.moveToFirst()) {
									flag = 1;
									Logger.v(TAG, String.format(" 已存在，不更新, id = %s, key = %s", extra.getId(), extra.getKey() + ""));								
								}
							}
						}
						if (flag == 0) {
							Logger.v(TAG, String.format(" 插入, id = %s, key = %s", extra.getId(), extra.getKey() + ""));
							getRd().insert(ClassUtils.getTableName(entity.getClass()), idColName, ClassUtils.getContentValues(tableInfoUtils, extra, entity));
						}
					}
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			getRd().setTransactionSuccessful();
			getRd().endTransaction();
			Logger.i(TAG, "end insert List");
			Logger.i(TAG, "*******************************************");
			Logger.w(TAG, String.format("插入%d条数据，共耗时%s秒", count, String.valueOf((System.currentTimeMillis() - time) / 1000)));
		}
	}
	
	/**
	 * 2014-08-21，新增批量插入数据
	 * 
	 * @param extra
	 * @param entities
	 * @param autoUpdate
	 */
	public <T> void insertList(Extra extra, List<T> entities, boolean autoUpdate) {
		SqliteBatchBean<T> batch = new SqliteBatchBean<T>();
		batch.extra = extra;
		batch.list = entities;
		
		List<SqliteBatchBean<T>> batchList = new ArrayList<SqliteUtility.SqliteBatchBean<T>>();
		batchList.add(batch);
		
		insertBatchList(batchList, autoUpdate);
 	}

	public <T> void insertList(Extra extra, List<T> entities) {
		insertList(extra, entities, true);
	}

	/**
	 * @param clazz
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public <T> List<T> selectAll(Class<T> clazz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		checkTableExist(clazz);

		List<T> result = new ArrayList<T>();
		SQLiteDatabase db = getRd();

		TableInfo tableInfo = tableInfoUtils.get(clazz);
		Field idField = tableInfo.getId().getField();

		Logger.d(
				TAG,
				String.format("begin read List, table = %s", ClassUtils.getTableName(clazz)));
		
		if (selection != null)
			Logger.i(TAG, String.format("selection = %s", selection));
		if (selectionArgs != null)
			for (String arg : selectionArgs)
				Logger.i(TAG, arg);

		Cursor cursor = db.query(ClassUtils.getTableName(clazz), null, selection, selectionArgs, groupBy, having, orderBy, limit);

		if (cursor.moveToFirst()) {
			do {
				try {
					T t = clazz.newInstance();
					// 设置主键
					idField.setAccessible(true);
					setFieldValue(t, idField, cursor.getString(cursor.getColumnIndex(FieldUtils.getColumnNameByField(idField))));
					// idField.set(t, FieldUtils.json2Value(cursor.getString(cursor.getColumnIndex(FieldUtils.getColumnNameByField(idField))), idField));
					// 设置各属性
					if (tableInfo.propertyMap.size() > 0) {
						Set<String> keySet = tableInfo.propertyMap.keySet();
						for (String key : keySet) {
							Property property = tableInfo.propertyMap.get(key);
							Field field = property.getField();
							field.setAccessible(true);
							int colIndex = cursor.getColumnIndex(FieldUtils.getColumnNameByField(field));
							if (colIndex == -1)
								continue;
							String value = cursor.getString(colIndex);
							setFieldValue(t, field, value);
						}
					}

//					Logger.v(TAG, String.format("load data --->%s", Logger.toJson(t)));
					result.add(t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
			Logger.d(TAG, "read List size = " + result.size());
			Logger.d(TAG, "end read List");
		} else {
			Logger.d(TAG, "read empty List");
		}
		cursor.close();

		return result;
	}
	
	private void setFieldValue(Object t, Field field, String value) throws Exception {
		if (field.getType().equals(String.class) || field.getType().equals(Enum.class)) {
			field.set(t, value);
		} else if (field.getType().isPrimitive()) {
			field.set(t, FieldUtils.json2Value("\"" + value + "\"", field));
		} else {
			field.set(t, FieldUtils.json2Value(value, field));
		}
	}

	public <T> T selectById(Extra extra, Class<T> clazz) {
		TableInfo tableInfo = tableInfoUtils.get(clazz);
		Field idField = tableInfo.getId().getField();
		String idColName = FieldUtils.getColumnNameByField(idField);

		String selection = null;
		String[] selectionArgs = null;
		if (extra != null) {
			Object[] o = getSelectionAndArgs(idColName, extra);
			if (o != null) {
				selection = o[0].toString();
				selectionArgs = (String[]) o[1];
			}
		}

		List<T> resultList = selectAll(clazz, selection, selectionArgs);
		if (resultList != null && resultList.size() > 0)
			return resultList.get(0);

		return null;
	}

	public <T> List<T> selectAll(Class<T> clazz, String selection, String[] selectionArgs, String orderBy, String limit) {
		return selectAll(clazz, selection, selectionArgs, null, null, orderBy, limit);
	}

	public <T> List<T> selectAll(Class<T> clazz, String selection, String[] selectionArgs) {
		return selectAll(clazz, selection, selectionArgs, null, null, null, null);
	}

	public <T> List<T> selectAll(Class<T> clazz) {
		return selectAll(clazz, null, null, null, null, null, null);
	}

	public void deleteAll(Extra extra, Class<?> clazz) {
		checkTableExist(clazz);

		TableInfo tableInfo = tableInfoUtils.get(clazz);
		Field idField = tableInfo.getId().getField();
		String idColName = FieldUtils.getColumnNameByField(idField);

		String whereClause = null;
		String[] whereArgs = null;
		Object[] o = getSelectionAndArgs(idColName, extra);
		if (o != null) {
			whereClause = o[0].toString();
			whereArgs = (String[]) o[1];
		}

		int flag = getWd().delete(ClassUtils.getTableName(clazz), whereClause, whereArgs);
		Logger.d(TAG, String.format("delete %d rows", flag));
	}

	public void delete(Extra extra, Object entity) {
		try {
			checkTableExist(entity.getClass());

			SQLiteDatabase db = getWd();

			Field idField = tableInfoUtils.get(entity.getClass()).getId().getField();
			idField.setAccessible(true);
			idField.get(entity);

			String selection = null;
			String[] selectionArgs = null;

			if (extra == null)
				extra = new Extra();

			extra.setId(idField.get(entity).toString());
			Object[] o = getSelectionAndArgs(FieldUtils.getColumnNameByField(idField), extra);
			if (o != null) {
				selection = o[0].toString();
				selectionArgs = (String[]) o[1];
			}

			int flag = db.delete(ClassUtils.getTableName(entity.getClass()), selection, selectionArgs);
			if (flag > 0)
				Logger.w(TAG, String.format("删除, flag = %d , id = %s", flag, extra.getId()));
			else
				Logger.w(TAG, String.format("删除失败 , id = %s ", extra.getId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(Extra extra, Class<?> clazz, Object id) {
		try {
			Object i = clazz.newInstance();
			Field idField = tableInfoUtils.get(clazz).getId().getField();
			idField.setAccessible(true);
			idField.set(i, id);
			delete(extra, i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(Class<?> clazz, String whereClause, String[] whereArgs) {
		checkTableExist(clazz);

		SQLiteDatabase db = getWd();
		db.delete(tableInfoUtils.get(clazz).getTableName(), whereClause, whereArgs);
	}

	public void update(Extra extra, Object entity) {
		insert(extra, entity);
	}
	
	public int update(Class<?> clazz, ContentValues values, String whereClause, String[] whereArgs) {
		checkTableExist(clazz);
		
		SQLiteDatabase db = getWd();
		return db.update(ClassUtils.getTableName(clazz), values, whereClause, whereArgs);
	}

	private void checkTableExist(Class<?> clazz) {
		if (!tableIsExist(tableInfoUtils.get(clazz))) {
			String sql = SqlBuilder.getCreatTableSQL(tableInfoUtils, clazz);

			Logger.d(TAG, String.format("create table, name = %s, sql = %s", ClassUtils.getTableName(clazz), sql));

			db.execSQL(sql);
		}
	}

	private boolean tableIsExist(TableInfo table) {
		if (table.isCheckDatabese())
			return true;

		Cursor cursor = null;
		try {
			String sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + table.getTableName() + "' ";
			Logger.d(TAG, sql);

			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					table.setCheckDatabese(true);

					// 检查表字段是否发生更改
					cursor.close();

					cursor = db.rawQuery("PRAGMA table_info" + "(" + table.getTableName() + ")", null);
					// table的所有字段名称
					List<String> nameList = new ArrayList<String>();
					if (cursor != null && cursor.moveToNext()) {
						do {
							nameList.add(cursor.getString(cursor.getColumnIndex("name")));
						} while (cursor.moveToNext());
					}
					cursor.close();

					// 检查新对象的是否更新
					List<Property> properties = ClassUtils.getPropertyList(table.getClazz());
					Field idField = ClassUtils.getPrimaryKeyField(table.getClazz());

					List<String> properList = new ArrayList<String>();
					for (Property property : properties) {
						properList.add(property.getField().getName());
					}
					
					// 1、新增对新字段自动添加的功能，暂时不能删除字段 2014-06-27
					//---------------------------------------------------------------------------------------------------
					
					List<String> newFieldList = new ArrayList<String>();
					for (String field : properList) {
						boolean isNew = true;
						
						for (String tableField : nameList) {
							if (tableField.equals(field)) {
								isNew = false;
								break;
							}
						}
						
						if (isNew)
							newFieldList.add(field);
					}
					
					for (String newField : newFieldList) {
						db.execSQL(String.format("ALTER TABLE %s ADD %s TEXT", table.getTableName(), newField));
					}
					
					// sqlite 暂时不支持drop column
					if (false) {
						List<String> deleteFieldList = new ArrayList<String>();
						for (String tableField : nameList) {
							if (!"_id".equals(tableField) && !FieldUtils.CREATEAT.equals(tableField) && !FieldUtils.KEY.equals(tableField)
									&& !FieldUtils.OWNER.equals(tableField) && !idField.getName().equals(tableField)) {
								boolean deleted = true;
								for (String field : properList) {
									
									if (field.equals(tableField)) {
										deleted = false;
										
										break;
									}
									
								}
								
								if (deleted)
									deleteFieldList.add(tableField);
							}
						}
						
						for (String deletedField : deleteFieldList) {
							db.execSQL(String.format("ALTER TABLE %s DROP %s", table.getTableName(), deletedField));
						}
					}

					//---------------------------------------------------------------------------------------------------
					

//					if (!nameList.contains(FieldUtils.getColumnNameByField(idField))) {
//						Logger.w(String.format("数据表%s更新了，删除该表", table.getTableName()));
//						db.execSQL("DROP TABLE " + table.getTableName());
//						table.setCheckDatabese(false);
//						return false;
//					}
//					nameList.remove(FieldUtils.getColumnNameByField(idField));
//					properList.remove(FieldUtils.getColumnNameByField(idField));
//
//					for (String property : properList) {
//						boolean notExist = true;
//						for (int i = 0; i < nameList.size(); i++) {
//							if (nameList.get(i).equals(property)) {
//								notExist = false;
//								break;
//							}
//						}
//						if (notExist) {
//							Logger.w(String.format("数据表%s更新了，删除该表", table.getTableName()));
//							db.execSQL("DROP TABLE " + table.getTableName());
//							table.setCheckDatabese(false);
//							return false;
//						}
//					}
					
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}

		return false;
	}
	
	public SQLiteDatabase getRd() {
		return db;
	}

	public SQLiteDatabase getWd() {
		return db;
	}

	static void dropDb(SQLiteDatabase db) {
		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name != 'sqlite_sequence'", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				db.execSQL("DROP TABLE " + cursor.getString(0));
			}
		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}

	static class SqliteDbHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 1;
		
		SqliteDbHelper(String dbName) {
			super(GlobalContext.getInstance(), dbName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			dropDb(db);
			onCreate(db);
		}

	}

	static SQLiteDatabase openSdcardDb() {
		return openSdcardDb(SettingUtility.getStringSetting("db_name"));
	}
	
	static SQLiteDatabase openSdcardDb(String dbName) {
		File dbf = new File(getDBPath(dbName));
		if (!dbf.exists()) {
			dbf.getParentFile().mkdirs();

			try {
				if (dbf.createNewFile()) {
					Logger.d(TAG,
							String.format("create db on disk, path = %s", dbf.getAbsolutePath()));
					Logger.i(TAG,
							String.format("open db on disk, path = %s", dbf.getAbsolutePath()));
					return SQLiteDatabase.openOrCreateDatabase(dbf, null);
				}
			} catch (IOException ioex) {
				ioex.printStackTrace();
				
				throw new RuntimeException("create db on disk failed", ioex);
			}
		} else {
			Logger.i(TAG, "open db on app dire");
			return SQLiteDatabase.openOrCreateDatabase(dbf, null);
		}
		
		return null;
	}
	
	/**
	 * Sdcard的DB path
	 * 
	 * @param dbName
	 * @return
	 */
	public static String getDBPath(String dbName) {
		Setting setting = SettingUtility.getSetting("path");
		String sdcardPath = SystemUtility.getSdcardPath() + File.separator + SettingUtility.getStringSetting("root_path") + File.separator + "."
				+ setting.getValue();
		
		return sdcardPath + File.separator + dbName + ".db";
	}
	
	public static class SqliteBatchBean<T> {
		
		public Extra extra;
		
		public List<T> list;
		
	}

}
