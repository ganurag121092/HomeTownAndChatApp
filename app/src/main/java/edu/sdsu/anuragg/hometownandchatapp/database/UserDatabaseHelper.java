package edu.sdsu.anuragg.hometownandchatapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import edu.sdsu.anuragg.hometownandchatapp.DataAccessLayer;
import edu.sdsu.anuragg.hometownandchatapp.UserDataModel;

/**
 * Created by AnuragG on 11-Apr-17.
 */

public class UserDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UserDATABASE.db";
    public static final String USERS_TABLE_NAME= "user";
    public static final int DATABASE_VERSION = 1;
    public static final String USERS_COLUMN_ID = "id";
    public static final String USERS_COLUMN_NICKNAME = "nickname";
    public static final String USERS_COLUMN_COUNTRY = "country";
    public static final String USERS_COLUMN_STATE = "state";
    public static final String USERS_COLUMN_CITY = "city";
    public static final String USERS_COLUMN_YEAR = "year";
    public static final String USERS_COLUMN_LATITUDE = "latitude";
    public static final String USERS_COLUMN_LONGITUDE = "longitude";
    public static final String USERS_COLUMN_TIMESTAMP = "timestamp";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase userDb) {
        userDb.execSQL("CREATE TABLE " + USERS_TABLE_NAME + "("
                + USERS_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + USERS_COLUMN_NICKNAME + " TEXT,"
                + USERS_COLUMN_COUNTRY + " TEXT,"
                + USERS_COLUMN_STATE + " TEXT,"
                + USERS_COLUMN_CITY + " TEXT,"
                + USERS_COLUMN_YEAR + " INTEGER,"
                + USERS_COLUMN_LATITUDE + " REAL,"
                + USERS_COLUMN_LONGITUDE + " REAL,"
                + USERS_COLUMN_TIMESTAMP + " TEXT"
                + ");");


    }

    @Override
    public void onUpgrade(SQLiteDatabase userDb, int oldVersion, int newVersion) {
        userDb.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);

        // Create tables again
        onCreate(userDb);
    }

    public boolean insertUser (UserDataModel userDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERS_COLUMN_ID, userDataModel.id);
        values.put(USERS_COLUMN_NICKNAME, userDataModel.nickname);
        values.put(USERS_COLUMN_COUNTRY, userDataModel.country);
        values.put(USERS_COLUMN_STATE, userDataModel.state);
        values.put(USERS_COLUMN_CITY, userDataModel.city);
        values.put(USERS_COLUMN_YEAR, userDataModel.year);
        values.put(USERS_COLUMN_LATITUDE, userDataModel.latitude);
        values.put(USERS_COLUMN_LONGITUDE, userDataModel.longitude);
        values.put(USERS_COLUMN_TIMESTAMP, userDataModel.timestamp);
        db.insertWithOnConflict(USERS_TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public Cursor getUserRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from USERS where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, USERS_TABLE_NAME);
        return numRows;
    }

    public boolean isUserExists(String username){
        int count = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + USERS_TABLE_NAME + " WHERE " + USERS_COLUMN_NICKNAME + " = ?";
            res = db.rawQuery(query, new String[] {username});
            if (res.moveToFirst()) {
                count = res.getInt(0);
            }
            return count > 0;
        }
        finally {
            if (res != null) {
                res.close();
            }
        }
    }

    public int getMaxId(){
        int maxId = 0;

        return maxId;
    }

    public boolean isDbEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("Check DB","Data inside TABLE:");

        String query = "SELECT count(*) FROM "+ UserDatabaseHelper.USERS_TABLE_NAME+";";
        Cursor result = db.rawQuery(query,null);
        result.moveToFirst();

        int DB_RECORD_COUNT= result.getInt(0);
        result.close();
        Log.i("Row Count","Record count: "+DB_RECORD_COUNT);

        return (DB_RECORD_COUNT==0);
    }

    public ArrayList<UserDataModel> getLatestUsers(String selectedCountry, String selectedState, String selectedYear, Context context) {
        ArrayList<UserDataModel> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        String url;
        if(selectedCountry!=null) {
            if(selectedState!=null) {
                if (selectedYear != null) {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_STATE + " = "+"\""+selectedState+"\"" + " AND "+
                            USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&state=" + selectedState + "&year=" + String.valueOf(selectedYear) + "&page=0&reverse=true";
                } else {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_STATE + " = "+"\""+selectedState+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&state=" + selectedState + "&page=0&reverse=true";
                }
            }
            else{
                if(selectedYear!=null) {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&year=" + String.valueOf(selectedYear) + "&page=0&reverse=true";
                }
                else {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+ "\""+selectedCountry+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&page=0&reverse=true";
                }
            }
        }
        else{
            if(selectedYear!=null){
                res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                        USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" +
                        " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                url = "http://bismarck.sdsu.edu/hometown/users?year="+selectedYear + "&page=0&reverse=true";
            }
            else{
                res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                url = "http://bismarck.sdsu.edu/hometown/users" + "?page=0&reverse=true";
            }
        }
        res.moveToFirst();
        while(!res.isAfterLast()){
            UserDataModel user = new UserDataModel();
            user.nickname = res.getString(res.getColumnIndex(USERS_COLUMN_NICKNAME));
            user.id = res.getInt(res.getColumnIndex(USERS_COLUMN_ID));
            user.country = res.getString(res.getColumnIndex(USERS_COLUMN_COUNTRY));
            user.state = res.getString(res.getColumnIndex(USERS_COLUMN_STATE));
            user.city = res.getString(res.getColumnIndex(USERS_COLUMN_CITY));
            user.year = res.getInt(res.getColumnIndex(USERS_COLUMN_YEAR));
            user.timestamp = res.getString(res.getColumnIndex(USERS_COLUMN_TIMESTAMP));
            user.latitude = res.getDouble(res.getColumnIndex(USERS_COLUMN_LATITUDE));
            user.longitude = res.getDouble(res.getColumnIndex(USERS_COLUMN_LONGITUDE));
            array_list.add(user);
            res.moveToNext();
        }


        res.close();
        Log.d("Filterd Users Size", Integer.toString(array_list.size()));
        if(array_list.size()<25) {
            DataAccessLayer dataAccessLayer = new DataAccessLayer();
            int max_id = array_list.size() != 0 ? array_list.get(array_list.size() - 1).id : 0;
            int min_id = array_list.size() != 0 ? array_list.get(0).id : 0;

            dataAccessLayer.refreshRecords(url, context, min_id, max_id);
        }
            //getLatestUsers(selectedCountry,selectedState,selectedYear,context);

        /*if(array_list.size()<25){
            getLatestUsers(selectedCountry,selectedState,selectedYear,context);
        }*/
        return array_list;
    }

    public ArrayList<UserDataModel> getOldUsers(int minimumId, String selectedCountry, String selectedState, String selectedYear, Context context) {
        ArrayList<UserDataModel> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        String url;
        if(selectedCountry!=null) {
            if(selectedState!=null) {
                if (selectedYear != null) {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_STATE + " = "+"\""+selectedState+"\"" + " AND "+
                            USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" + " AND "+
                            USERS_COLUMN_ID + " < "+"\""+minimumId+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&state=" + selectedState + "&year=" + String.valueOf(selectedYear) + "&page=0&reverse=true";
                } else {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_STATE + " = "+"\""+selectedState+"\"" + " AND "+
                            USERS_COLUMN_ID + " < "+"\""+minimumId+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&state=" + selectedState + "&page=0&reverse=true";
                }
            }
            else{
                if(selectedYear!=null) {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" + " AND "+
                            USERS_COLUMN_ID + " < "+"\""+minimumId+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&year=" + String.valueOf(selectedYear) + "&page=0&reverse=true";
                }
                else {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+ "\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_ID + " < "+"\""+minimumId+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&page=0&reverse=true";
                }
            }
        }
        else{
            if(selectedYear!=null){
                res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                        USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" + " AND "+
                        USERS_COLUMN_ID + " < "+"\""+minimumId+"\"" +
                        " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                url = "http://bismarck.sdsu.edu/hometown/users?year="+selectedYear + "&page=0&reverse=true";
            }
            else{
                res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+ " WHERE "+
                        USERS_COLUMN_ID + " < "+"\""+minimumId+"\"" +
                        " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                url = "http://bismarck.sdsu.edu/hometown/users" + "?page=0&reverse=true";
            }
        }
        res.moveToFirst();
        while(!res.isAfterLast()){
            UserDataModel user = new UserDataModel();
            user.nickname = res.getString(res.getColumnIndex(USERS_COLUMN_NICKNAME));
            user.id = res.getInt(res.getColumnIndex(USERS_COLUMN_ID));
            user.country = res.getString(res.getColumnIndex(USERS_COLUMN_COUNTRY));
            user.state = res.getString(res.getColumnIndex(USERS_COLUMN_STATE));
            user.city = res.getString(res.getColumnIndex(USERS_COLUMN_CITY));
            user.year = res.getInt(res.getColumnIndex(USERS_COLUMN_YEAR));
            user.timestamp = res.getString(res.getColumnIndex(USERS_COLUMN_TIMESTAMP));
            user.latitude = res.getDouble(res.getColumnIndex(USERS_COLUMN_LATITUDE));
            user.longitude = res.getDouble(res.getColumnIndex(USERS_COLUMN_LONGITUDE));
            array_list.add(user);
            res.moveToNext();
        }


        res.close();
        Log.d("Filterd Users Size", Integer.toString(array_list.size()));
        DataAccessLayer dataAccessLayer = new DataAccessLayer();
         if(array_list.size()<25) {

             dataAccessLayer.refreshBeforeRecords(url + "&beforeid=" + minimumId, context);
         }

        return array_list;
    }

    public ArrayList<UserDataModel> getMoreLatestUsers(int maximumId, String selectedCountry, String selectedState, String selectedYear, Context context) {
        ArrayList<UserDataModel> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        String url;
        if(selectedCountry!=null) {
            if(selectedState!=null) {
                if (selectedYear != null) {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_STATE + " = "+"\""+selectedState+"\"" + " AND "+
                            USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" + " AND "+
                            USERS_COLUMN_ID + " > "+"\""+maximumId+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&state=" + selectedState + "&year=" + String.valueOf(selectedYear) + "&page=0&reverse=true";
                } else {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_STATE + " = "+"\""+selectedState+"\"" + " AND "+
                            USERS_COLUMN_ID + " > "+"\""+maximumId+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&state=" + selectedState + "&page=0&reverse=true";
                }
            }
            else{
                if(selectedYear!=null) {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+"\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" + " AND "+
                            USERS_COLUMN_ID + " > "+"\""+maximumId+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&year=" + String.valueOf(selectedYear) + "&page=0&reverse=true";
                }
                else {
                    res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                            USERS_COLUMN_COUNTRY + " = "+ "\""+selectedCountry+"\"" + " AND "+
                            USERS_COLUMN_ID + " > "+"\""+maximumId+"\"" +
                            " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + selectedCountry + "&page=0&reverse=true";
                }
            }
        }
        else{
            if(selectedYear!=null){
                res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+" WHERE " +
                        USERS_COLUMN_YEAR + " = "+"\""+selectedYear+"\"" + " AND "+
                        USERS_COLUMN_ID + " > "+"\""+maximumId+"\"" +
                        " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                url = "http://bismarck.sdsu.edu/hometown/users?year="+selectedYear + "&page=0&reverse=true";
            }
            else{
                res =  db.rawQuery( "select * from "+USERS_TABLE_NAME+ " WHERE "+
                        USERS_COLUMN_ID + " > "+"\""+maximumId+"\"" +
                        " ORDER BY "+USERS_COLUMN_ID+" DESC LIMIT 25;", null);
                url = "http://bismarck.sdsu.edu/hometown/users" + "?page=0&reverse=true";
            }
        }
        res.moveToFirst();
        while(!res.isAfterLast()){
            UserDataModel user = new UserDataModel();
            user.nickname = res.getString(res.getColumnIndex(USERS_COLUMN_NICKNAME));
            user.id = res.getInt(res.getColumnIndex(USERS_COLUMN_ID));
            user.country = res.getString(res.getColumnIndex(USERS_COLUMN_COUNTRY));
            user.state = res.getString(res.getColumnIndex(USERS_COLUMN_STATE));
            user.city = res.getString(res.getColumnIndex(USERS_COLUMN_CITY));
            user.year = res.getInt(res.getColumnIndex(USERS_COLUMN_YEAR));
            user.timestamp = res.getString(res.getColumnIndex(USERS_COLUMN_TIMESTAMP));
            user.latitude = res.getDouble(res.getColumnIndex(USERS_COLUMN_LATITUDE));
            user.longitude = res.getDouble(res.getColumnIndex(USERS_COLUMN_LONGITUDE));
            array_list.add(user);
            res.moveToNext();
        }


        res.close();
        Log.d("Filterd Users Size", Integer.toString(array_list.size()));
        DataAccessLayer dataAccessLayer = new DataAccessLayer();
        if(array_list.size()<25) {
            dataAccessLayer.refreshAfterRecords(url + "&afterid=" + maximumId, context);
        }
        return array_list;
    }


}
