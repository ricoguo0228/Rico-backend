package com.rico.rbi.constant;

import java.util.HashMap;
import java.util.Map;

public class ModelID {
    private final Map<Integer,Long> idMap = new HashMap<>();
   public  long getModelID(int id) {
        return this.idMap.getOrDefault(id, -1L);
    }
    public ModelID() {
        idMap.put(1, 1739287824584585217L);
        idMap.put(2, 1234567L);
    }
}
