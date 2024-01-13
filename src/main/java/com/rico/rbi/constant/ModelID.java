package com.rico.rbi.constant;

import java.util.HashMap;
import java.util.Map;

public class ModelID {
    private final Map<String,Long> idMap = new HashMap<>();
   public  long getModelID(String id) {
        return this.idMap.getOrDefault(id, -1L);
    }
    public ModelID() {
        idMap.put("redBook", 1746145668336439297L);
        idMap.put("known", 1746147283512578050L);
        idMap.put("weiBo", 1746147676132986882L);
        idMap.put("product", 1746147676132986882L);
        idMap.put("good", 1746148286664265730L);
        idMap.put("goodBack", 1746148658887774210L);
        idMap.put("introduce", 1746148890404966402L);
        idMap.put("sister", 1746149512827097089L);
        idMap.put("video", 1746149979590217729L);
    }
}
