package com.example.getdatafromsystemdemo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sms_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.获取内容处理者
                ContentResolver resolver = getContentResolver();
                //2.查询方法
                //sms: short message service
                //    content://sms     短信箱
                //    content://sms/inbox     收件箱
                //     content://sms/sent       发件箱
                //      content://sms/draft     草稿箱
                Uri uri = Uri.parse("content://sms/draft");
                Cursor c = resolver.query(uri,null,null,null,null);
                //3.解析Cursor
                //遍历Cursor
                while(c.moveToNext()){
                    //对象，内容
                    //参数：列索引
                    //c.getString(2);
                    //遍历该行的列
                    String msg = "";

                    String address = c.getString(c.getColumnIndex("address"));
                    String body = c.getString(c.getColumnIndex("body"));

                    msg = address + ":" + body;
                    /*
                    for(int i = 0 ; i < c.getColumnCount() ; i++){
                        msg += c.getString(i) + "  ";
                    }
                    */
                    Log.e("TAG",msg);
                }
            }
        });

        findViewById(R.id.read_contact_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = getContentResolver();
                //对于联系人而言，他们的存储方式是将姓名和其他内容（电话号码）由不同点contentProvider操作的
                //首先想象姓名和其他内容属于不同的表
                //而姓名所在的表是主表，其他内容位于从表
                //而主表中的主键会在从表中作为外键使用
                Cursor c1 = resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
                while(c1.moveToNext()){

//                    ContactsContract.Contacts.DISPLAY_NAME    姓名
//                    ContactsContract.Contacts._ID     主键
                    String name = c1.getString(c1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String _id = c1.getString(c1.getColumnIndex(ContactsContract.Contacts._ID ));
                    Log.e("TAG","姓名是：" + name +" , id是" + _id);

                    String selections = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
                    Cursor c2 = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            selections,
                            new String[]{_id},
                            null);
                    while(c2.moveToNext()){
                        String number = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        name += "   " + number;
                    }

                    Log.e("TAG" , name);
                    /*
                    String msg = "";
                    for(int i = 0 ; i < c1.getColumnCount() ; i++){
                        msg += c1.getString(i) + "  ";
                    }
                    Log.e("TAG",msg);
                    */

                }
            }
        });

        findViewById(R.id.add_contact_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = getContentResolver();

                //1.往一个ContentProvider中插入一条空数据，获取新生成的id
                //2.利用刚刚生成的id分别组合姓名和电话号码往另一个ContentProvider中插入数据U
                ContentValues values = new ContentValues();
                Uri uri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI,values);
                long id = ContentUris.parseId(uri);

                //插入姓名
                //指定姓名列的内容
                values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,"Tommy");
                //指定和姓名关联的编号列的内容
                values.put(ContactsContract.Data.RAW_CONTACT_ID,id);
                //指定该行数据的类型
                values.put(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                resolver.insert(ContactsContract.Data.CONTENT_URI,values);

                //插入电话号码
                //清空ContentValues对象
                values.clear();
                //指定电话号码列的内容
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "15789065588");
                //指定和电话号码关联的编号列的内容
                values.put(ContactsContract.Data.RAW_CONTACT_ID,id);
                //指定该行数据的类型
                values.put(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                //指定联系方式的类型
                values.put(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

                resolver.insert(ContactsContract.Data.CONTENT_URI,values);
            }
        });
    }
}
