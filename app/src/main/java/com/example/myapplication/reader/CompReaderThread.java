package com.example.myapplication.reader;

import com.example.myapplication.Config;
import com.example.myapplication.FolderSet;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class CompReaderThread extends Thread{
    private FolderSet c_folder_set;

    public FolderSet getC_folder_set() {
        return c_folder_set;
    }

    @Override
    public void run() {
        String host = Config.Computer.HOST;//远程服务器的地址
        String username = Config.Computer.USERNAME;//远程服务器的用户名
        String password = Config.Computer.PASSWORD;//远程服务器的密码
        String path = Config.Computer.PIC_DIR;//远程服务器共享文件夹名称
        String remoteUrl = "smb://" + username + ":" + password + "@" + host + path + (path.endsWith("/") ? "" : "/");//这是需要输入密码的url
        this.c_folder_set = new FolderSet(remoteUrl);
        System.out.println(remoteUrl);
        SmbFile smbFile;
        try {
            // smb://userName:passWord@host/path/
            smbFile = new SmbFile(remoteUrl);
            if (!smbFile.exists()) {
                System.out.println("no such folder");
            } else {
                SmbFile[] folder_names = smbFile.listFiles();
                for (SmbFile f : folder_names) {
                    if (!f.isDirectory()){
                        continue;
                    }
                    List<String> img_names = new ArrayList<String>();
                    for (SmbFile sub_f: f.listFiles()){
                        img_names.add(sub_f.getName());
                    }
                    // 去除最后一个/
                    this.c_folder_set.addFolder(f.getName().substring(0, f.getName().length()-1), img_names);
                }
            }
        } catch (MalformedURLException | SmbException e) {
            e.printStackTrace();
        }
    }
}
