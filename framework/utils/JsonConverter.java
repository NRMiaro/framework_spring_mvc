package framework.utils;

import com.google.gson.Gson;

public class JsonConverter {

    private final Gson gson;
    
    public JsonConverter(){
        this.gson = new Gson();
    }

    public String toJson(Object object){
        return this.gson.toJson(object);
    }

}