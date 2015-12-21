package com.cse.duthientan.musicapplication.model;

import java.util.List;

/**
 * Created by Du Thien Tan on 10/27/2015.
 */
public class AblumHot {
    public List<Ablum> getmList() {
        return mList;
    }

    private  List<Ablum > mList;

    public AblumHot(List<Ablum> list){
        mList = list;
    }
}
