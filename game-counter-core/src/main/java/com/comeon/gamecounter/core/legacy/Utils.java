package com.comeon.gamecounter.core.legacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;

public class Utils {
    public static final String REGEXOFNUM = "^0|([1-9]\\d*)";
    public static final String CHARSET = "ASCII";

    public static String timestampToDatetime(long timeMillis){
        Timestamp timestamp = new Timestamp(timeMillis);
        Date date = new Date(timestamp.getTime());
        return date.toString();
    }

    public static String stringFromStream(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
            return stringBuilder.toString().trim();
        } else return "";
    }

    public Map<String, Object> parseQuery(String query){
        Map<String, Object> res = new HashMap<String, Object>();
        if(query!=null){
            String pairs[] = query.split("&");
            for(String pair : pairs){
                String param[] = pair.split("=");
                String key = null;
                String value = null;
                if(param.length > 0)
                    key = param[0];
                if(param.length > 1)
                    value = param[1];
                if(res.containsKey(key)){
                    Object obj = res.get(key);
                    if(obj instanceof List<?>){
                        List<String> values = (List<String>)obj;
                        values.add(value);
                    }else
                        if(obj instanceof String){
                            List<String>values = new ArrayList<String>();
                            values.add((String)obj);
                            values.add(value);
                            res.put(key,values);
                        }
                }else
                    res.put(key, value);
            }
        }
        return res;
    }
}
