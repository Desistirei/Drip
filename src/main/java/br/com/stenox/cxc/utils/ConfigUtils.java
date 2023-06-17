package br.com.stenox.cxc.utils;

import br.com.stenox.cxc.Main;

import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {

    private final Map<String, Object> OBJECTS;
    private final Main plugin;

    public ConfigUtils(Main plugin){
        this.plugin = plugin;
        this.OBJECTS = new HashMap<>();
    }

    public String getString(String string){
        if (OBJECTS.containsKey(string)){
            return (String) OBJECTS.get(string);
        } else {
            String s = plugin.getConfig().getString(string);
            if (s != null)
                OBJECTS.put(string, s);

            return s;
        }
    }

    public int getInteger(String string){
        if (OBJECTS.containsKey(string)){
            return (int) OBJECTS.get(string);
        } else {
            int s = plugin.getConfig().getInt(string);
            OBJECTS.put(string, s);

            return s;
        }
    }

    public Object get(String string){
        if (OBJECTS.containsKey(string)){
            return OBJECTS.get(string);
        } else {
            Object s = plugin.getConfig().get(string);
            OBJECTS.put(string, s);

            return s;
        }
    }
}
