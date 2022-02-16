package com.example.myapplication;



import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FolderSet implements Iterator<Pair> {
    private final HashMap<String, HashSet<String>> folders;
    private int pointer = 0;
    private List<String> keys;
    private String absolute_dir = "";


    public FolderSet(String absolute_dir){
        this.absolute_dir = absolute_dir;
        this.folders = new HashMap<String, HashSet<String>>();
    }

    public void addFolder(String folder_name, List<String> files){
        HashSet<String> one_folder = new HashSet<String>();
        for (String file : files){
            one_folder.add(file);
        }
        this.folders.put(folder_name, one_folder);
    }

    public String getAbsolutePath(String folder_name, String file_name){
        return this.absolute_dir+(this.absolute_dir.endsWith("/") ? "" : "/")+folder_name+"/"+file_name;
    }

    public String getAbsolutePath(String folder_name){
        return this.absolute_dir+(this.absolute_dir.endsWith("/") ? "" : "/")+folder_name;
    }

    public HashSet<String> getFolder(String folder_name){
        return this.folders.get(folder_name);
    }

    public boolean hasFolder(String folder_name){
        return this.folders.containsKey(folder_name);
    }

    public Set<String> getFolderNameSet(){
        return this.folders.keySet();
    }

    public void initIterator(){
        this.pointer = 0;
        keys = new ArrayList<String>();
        for (String k: folders.keySet()){
            this.keys.add(k);
        }
        this.keys.sort(Comparator.naturalOrder());
    }

    @Override
    public boolean hasNext() {
        if (this.pointer >= this.keys.size()) {
            return false;
        }
        return true;
    }

    @Override
    public Pair next() {
        String name = this.keys.get(this.pointer);
        this.pointer += 1;
        return new Pair(name, this.folders.get(name));
    }
}
