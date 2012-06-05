package com.guide.lunar;

import com.guide.lunar.ViolationData;
import java.util.List;
import java.util.ArrayList;

public class ViolationManager {

    public List<ViolationData> vdList = new ArrayList<ViolationData> ();

    private int vType;

    public ViolationManager (int type) {
        vType = type;
    }

    public ViolationManager add (ViolationData data) {
        vdList.add (data);
        return this;
    }

    public ViolationData newData () {
        return new ViolationData (vType);
    }

    public List<ViolationData> getList () {
        return vdList;
    }

    public int size () {
        return vdList.size ();
    }

}
