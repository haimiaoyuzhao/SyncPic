package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.reader.CompReader;
import com.example.myapplication.reader.PhoneReader;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class MainActivity extends AppCompatActivity {
    private FolderSet p_fset = null;
    private FolderSet c_fset = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获得权限
        requestPower();
        updateFrame();
    }

    private void updateFrame(){
        // 显示手机端目录情况
        PhoneReader preader = new PhoneReader();
        this.p_fset = preader.getFolderSet();
        displayPhoneDir(this.p_fset);

        // 显示电脑端目录情况
        this.c_fset = CompReader.getSharedFileList();
        displayCompDir(this.c_fset);
    }

    public void displayPhoneDir(FolderSet pdirs){
        pdirs.initIterator();
        LinearLayout view = (LinearLayout) findViewById(R.id.linear1);
        view.removeAllViewsInLayout();
        while (pdirs.hasNext()) {
            Pair res = pdirs.next();
            String name = (String) res.first;
            HashSet<String> file_set = (HashSet<String>) res.second;
            CheckBox cbox = new CheckBox(this);
            cbox.setText(name+"("+file_set.size()+")");
            view.addView(cbox);
        }
    }

    public void displayCompDir(FolderSet pdirs){
        pdirs.initIterator();
        LinearLayout view = (LinearLayout) findViewById(R.id.linear2);
        view.removeAllViewsInLayout();
        while (pdirs.hasNext()) {
            Pair res = pdirs.next();
            String name = (String) res.first;
            HashSet<String> file_set = (HashSet<String>) res.second;
            CheckBox cbox = new CheckBox(this);
            cbox.setText(name+"("+file_set.size()+")");
            view.addView(cbox);
        }
    }

    public void upload(View view){
        LinearLayout linear = (LinearLayout) findViewById(R.id.linear1);
        List<String> img_dirs_need_to_upload = new ArrayList<String>();
        for (int i = 0; i < linear.getChildCount(); i++) {
            CheckBox cbox = (CheckBox) linear.getChildAt(i);
            if (cbox.isChecked()){
                System.out.println(cbox.getText()+" is clicked");
                String text = (String) cbox.getText();
                text = split_by_bracket(text);
                img_dirs_need_to_upload.add(text);
            }
        }
        Uploader uploader = new Uploader(this, c_fset, p_fset, img_dirs_need_to_upload);
        uploader.start();
        try {
            uploader.join();
            updateFrame();
            Toast toast = Toast.makeText(getApplicationContext(),"upload successful", Toast.LENGTH_LONG);
            toast.show();


        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void download(View view){
        LinearLayout linear = (LinearLayout) findViewById(R.id.linear2);
        List<String> img_dirs_need_to_upload = new ArrayList<String>();
        for (int i = 0; i < linear.getChildCount(); i++) {
            CheckBox cbox = (CheckBox) linear.getChildAt(i);
            if (cbox.isChecked()){
                System.out.println(cbox.getText()+" is clicked");
                String text = (String) cbox.getText();
                text = split_by_bracket(text);
                img_dirs_need_to_upload.add(text);
            }
        }
        Downloader downloader = new Downloader(this, c_fset, p_fset, img_dirs_need_to_upload);
        downloader.start();
        try {
            downloader.join();
            updateFrame();
            Toast toast = Toast.makeText(getApplicationContext(),"download successful", Toast.LENGTH_LONG);
            toast.show();

        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private String split_by_bracket(String str){
        int n = str.length() - 1;
        int pt = 0;
        String res = "";
        for (int i = n-1; i >= 0; i--) {
            if (str.charAt(i) == '(') {
                pt = i;
                break;
            }
        }
        for (int i = 0; i < pt; i ++) {
            res += str.charAt(i);
        }
        return res;
    }

    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            System.out.println("###### request power");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            System.out.println("###### request power");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            System.out.println("###### request power");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,}, 1);

        }
        System.out.println("###### request power success");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}