package com.example.myapplication.reader;

import com.example.myapplication.Config;
import com.example.myapplication.FolderSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhoneReader {
    public FolderSet getFolderSet(){
        String pic_dir = Config.Phone.PIC_DIR;
        FolderSet fset = new FolderSet(pic_dir);
        File dir = new File(pic_dir);
        try {
            File[] folder_names = dir.listFiles();
            for (int i = 0; i < folder_names.length; i++) {
                String folder_name = folder_names[i].getName();
                if (folder_name.startsWith(".") || !folder_names[i].isDirectory())
                    continue;
                List<String> img_names = new ArrayList<String>();
                for (File file : folder_names[i].listFiles()){
                    if (is_valid_img_name(file.getName())) {
                        img_names.add(file.getName());
                    }
                }
                fset.addFolder(folder_name, img_names);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return fset;
    }

    private boolean is_valid_img_name(String img_name){
        if (img_name.endsWith("png")
                || img_name.endsWith("jpg")
                || img_name.endsWith("jpeg")
                || img_name.endsWith("gif")) {
            return true;
        }
        return false;
    }
}
