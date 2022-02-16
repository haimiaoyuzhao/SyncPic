package com.example.myapplication;

import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class Uploader extends Thread{
    private MainActivity activity;
    private FolderSet c_fset;
    private FolderSet p_fset;
    private List<String> img_dirs;

    public Uploader(MainActivity activity,
                    FolderSet c_fset,
                    FolderSet p_fset,
                    List<String> img_dirs){
        this.activity = activity;
        this.p_fset = p_fset;
        this.c_fset = c_fset;
        this.img_dirs = img_dirs;
    }

    public Uploader(MainActivity activity,
                    FolderSet c_fset,
                    FolderSet p_fset){
        this.activity = activity;
        this.p_fset = p_fset;
        this.c_fset = c_fset;
    }

    public void setImg_dirs(List<String> img_dirs) {
        this.img_dirs = img_dirs;
    }

    @Override
    public void run() {
        // 首先computer文件夹中不存在的文件夹先创建出来
        System.out.println("1. mkdir");
        for (String img_dir: img_dirs){
            if (!this.c_fset.hasFolder(img_dir)){
                smbMkDir(c_fset.getAbsolutePath(img_dir));
            }
        }

        // 将文件上传上去
        System.out.println("2. upload file");
        for (String folder_name: img_dirs) {
            HashSet<String> p_folder = p_fset.getFolder(folder_name);
            HashSet<String> c_folder = c_fset.getFolder(folder_name);
            for (String img_name: p_folder) {
                if (c_folder != null && c_folder.contains(img_name)) {
                    continue;
                }
                String p_path = p_fset.getAbsolutePath(folder_name, img_name);
                String c_path = c_fset.getAbsolutePath(folder_name, img_name);
                uploadFileToSharedFolder(p_path, c_path);
                System.out.println(p_path + " ###################### " + c_path);
            }
        }
    }

    public void smbMkDir(String remoteUrl){
        System.out.println(remoteUrl);
        SmbFile smbFile;
        try {
            // smb://userName:passWord@host/path/folderName
            smbFile = new SmbFile(remoteUrl);
            if (!smbFile.exists()) {
                smbFile.mkdir();
            }
        } catch (MalformedURLException | SmbException e) {
            e.printStackTrace();
        }
    }

    private static void uploadFileToSharedFolder(String p_path, String c_path) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File localFile = new File(p_path);
            inputStream = new FileInputStream(localFile);
            // smb://userName:passWord@host/path/shareFolderPath/fileName
            SmbFile smbFile = new SmbFile(c_path);
            smbFile.connect();
            outputStream = new SmbFileOutputStream(smbFile);
            byte[] buffer = new byte[4096];
            int len = 0; // 读取长度
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            // 刷新缓冲的输出流
            outputStream.flush();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (MalformedURLException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
