package com.fis.ecocash.ecocash.DataClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 27;

    // Database Name
    public static final String DATABASE_NAME = "propay";

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_MOBILE_MONEY = "mobile_money";
    public static final String TABLE_BACKUP = "mobile_backup";
    public static final String TABLE_CATEGORY = "mobile_category";


    //Common column names
    public static final String KEY_ID = "id";


    // USERS Table - column names
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_ID_NUM= "idNum";
    public static final String COLUMN_PH_NUM= "phNum";
    public static final String COLUMN_PSWD= "Password";
    public static final String COLUMN_APP_KEY = "appKey";
    public static final String COLUMN_MAMBU_ID ="mambu_id";
    public static final String COLUMN_STATUS= "tran_status";

    // MOBILE_MONEY Table - column names
    public static final String KEY_NAME = "tran_name";
    public static final String KEY_TYPE = "tran_type";
    public static final String KEY_TRANS_ID = "tran_id";
    public static final String KEY_BALANCE = "tran_balance";
    public static final String KEY_DATE = "tran_date";
    public static final String KEY_AMOUNT = "tran_amount";
    public static final String KEY_TEXT = "tran_text";
    public static final String KEY_MONTH = "tran_month";
    public static final String KEY_CHARGE = "tran_charge";
    public static final String KEY_CAT = "tran_cat";

    // BACKUP Table - column names
    public static final String KEY_BACK_DATE = "back_date";
    public static final String KEY_DEVICE = "back_device";
    public static final String KEY_GOOGLE = "back_google";

    // CATEGORY Table - column names
    public static final String KEY_CATEGORY = "cat_name";



    // Table Create Statements

    // dbUsers Create Statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FIRST_NAME + " TEXT, " + COLUMN_LAST_NAME + " TEXT, " +
            COLUMN_GENDER + " TEXT, " + COLUMN_ID_NUM + " TEXT, " +
            COLUMN_PH_NUM + " TEXT, " +  COLUMN_PSWD + " TEXT, " +
            COLUMN_APP_KEY + " TEXT, " + COLUMN_STATUS + " TEXT, " +  COLUMN_MAMBU_ID + " TEXT, " +KEY_DATE + " TEXT " + ")";


    // Mobile Money table create statement
    private static final String CREATE_TABLE_MOBILE = "CREATE TABLE " + TABLE_MOBILE_MONEY
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AMOUNT + " TEXT," + KEY_CHARGE + " TEXT," + KEY_CAT + " TEXT,"
            + KEY_TRANS_ID + " TEXT," + KEY_BALANCE + " TEXT," +  KEY_TEXT + " TEXT," +  KEY_MONTH + " TEXT,"
            + KEY_NAME + " TEXT, " + KEY_TYPE + " TEXT, " + KEY_DATE + " DATETIME " + ")";

    // Backup table create statement
    private static final String CREATE_TABLE_BACKUP = "CREATE TABLE " + TABLE_BACKUP
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DEVICE + " INTEGER,"
            + KEY_GOOGLE + " INTEGER," + KEY_BACK_DATE + " DATETIME " + ")";

    // Backup table create statement
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CATEGORY + " TEXT " + ")";


    public dbDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_MOBILE);
        db.execSQL(CREATE_TABLE_BACKUP);
        db.execSQL(CREATE_TABLE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOBILE_MONEY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BACKUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        // create new tables
        onCreate(db);
    }
}

