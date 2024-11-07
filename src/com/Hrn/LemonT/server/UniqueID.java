package com.Hrn.LemonT.server;

import java.util.ArrayList;
import java.util.List;

public class UniqueID {
    
    private static List<Integer> ids= new ArrayList<Integer>(); 
    
    private UniqueID(){

    }

    private static int index=0;
    private static final int RANGE=10000;

    static{
        for (int i=0;i<RANGE;i++){
            ids.add(i);
        }
    }

    public static int getIdentifier(){
        if(index > ids.size() -1) index=0;
        return ids.get(index++);
    }

}
