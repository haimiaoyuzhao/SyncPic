package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class Downloader extends Thread{
    private MainActivity activity;
    private FolderSet c_fset;
    private FolderSet p_fset;
    private List<String> img_dirs;

    public Downloader(MainActivity activity,
                    FolderSet c_fset,
                    FolderSet p_fset,
                    List<String> img_dirs){
        this.activity = activity;
        this.p_fset = p_fset;
        this.c_fset = c_fset;
        this.img_dirs = img_dirs;
    }

    @Override
    public void run() {
        // 首先computer文件夹中不存在的文件夹先创建出来
        System.out.println("1. mkdir");
        for (String img_dir: img_dirs){
            if (!this.p_fset.hasFolder(img_dir)){
                img_dir = p_fset.getAbsolutePath(img_dir);
                File file = new File(img_dir);
                if (!file.exists()){
                    System.out.println(file.getPath()+" is not exists");
                    file.mkdirs();
                }
                else{
                    System.out.println(file.getPath()+"  exists");
                }
            }
        }

        // 将文件下载下来
        System.out.println("2. download file");
        for (String folder_name: img_dirs) {
            HashSet<String> p_folder = p_fset.getFolder(folder_name);
            HashSet<String> c_folder = c_fset.getFolder(folder_name);
            for (String img_name: c_folder) {
                if (p_folder != null && p_folder.contains(img_name)) {
                    continue;
                }
                String p_path = p_fset.getAbsolutePath(folder_name, img_name);
                String c_path = c_fset.getAbsolutePath(folder_name, img_name);
                downloadFileToFolder(c_path, p_path);
                System.out.println(p_path + " ###################### " + c_path);
            }
        }
    }

    public void downloadFileToFolder(String c_path, String p_path) {
        InputStream in = null;
        OutputStream out = null;
        try {
            SmbFile remoteFile = new SmbFile(c_path);
            File localFile = new File(p_path);
            in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                out.write(buffer);
                buffer = new byte[1024];
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(activity.getApplicationContext().getContentResolver(),
//                    p_path, new File(p_path).getName(), null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        // 最后通知图库更新
        activity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+p_path)));
    }
}
