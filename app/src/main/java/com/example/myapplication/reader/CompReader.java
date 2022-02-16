package com.example.myapplication.reader;

import com.example.myapplication.FolderSet;


public class CompReader{
    public static FolderSet getSharedFileList() {
        CompReaderThread mythread = new CompReaderThread();
        mythread.start();
        try {
            mythread.join();
        }
        catch (Exception e){
            System.out.println(e);
        }
        return mythread.getC_folder_set();
    }
}
