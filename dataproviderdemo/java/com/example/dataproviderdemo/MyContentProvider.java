package com.example.dataproviderdemo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    //URI的解析
    //1.UriMatcher：在contentProvider创建时，制定好匹配规则，当调用了ContentProvider中的操作方法时
    //利用匹配类去匹配传的uri，根据不同的uri给出不同的处理
    //2.Uri自带解析方法

    UriMatcher matcher;
    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //                                             "_id=?"
        int result = 0;
        int code = matcher.match(uri);
        switch (code) {
            case 1000:
                Log.e("TAG","匹配到的路径是helloworld");
                break;
            case 1001:
                Log.e("TAG","匹配到的路径是helloworld/abc");
                break;
            case 1002:
                Log.d("TAG","匹配到路径为helloworld/任意数字的内容");
                break;
            case 1003:
                Log.i("TAG","匹配到路径为nihaoshijie/任意字符的内容");
                break;
            default:
                Log.e("TAG","执行删除数据库内容的方法");
                result = db.delete("info_tb", selection, selectionArgs);
                break;
        }
        return result;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        Log.e("TAG","调用了DataProviderDemo中的insert方法");
        long id = 0;
        if(values.size() > 0) {
            id = db.insert("info_tb", null, values);
        }else{
            String authority = uri.getAuthority();
            String path = uri.getPath();
            String query = uri.getQuery();
            String name = uri.getQueryParameter("name");
            String age = uri.getQueryParameter("age");
            String gender = uri.getQueryParameter("gender");
            Log.e("TAG","主机名：" + authority + "，路径：" + path + "，查询数据：" + query
                    + "，姓名：" + name + "，年龄：" + age + ",性别：" + gender);
            values.put("name" , name);
            values.put("age",age);
            values.put("gender",gender);
            id = db.insert("info_tb",null,values);
        }
        //将id追加到uri后面
        return  ContentUris.withAppendedId(uri,id);
    }

    //在ContentProvider创建调用
    SQLiteDatabase db;
    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        SQLiteOpenHelper helper = new SQLiteOpenHelper(getContext() , "stu.db" , null , 1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                String sql = "create table info_tb (_id integer primary key autoincrement," +
                        "name varchar(20)," +
                        "age integer," +
                        "gender varchar(2))";
                sqLiteDatabase.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        };

        db = helper.getReadableDatabase();

        //参数：代表无法匹配
        //     content://com.imooc.myprovider/helloworld
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI("com.imooc.myprovider","helloworld",1000);
        matcher.addURI("com.imooc.myprovider","helloworld/abc",1001);
        matcher.addURI("com.imooc.myprovider","helloworld/#",1002);
        matcher.addURI("com.imooc.myprovider","nihaoshijie/*",1003);

        //matcher.match()
        //返回true
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        //参数2：索要查询的列
        //参数3：查询条件
        //参数4:查询条件值
        //参数5：分组
        //参数6：分组条件
        //参数7：排序
        Cursor c = db.query("info_tb",projection,selection,selectionArgs,null,null,sortOrder);
        return  c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //  update info_tb set name = 'xx'  , age = 20 , gender = '男' where _id = 2
        int result = db.update("info_tb",values,selection,selectionArgs);
        return result;
    }
}
