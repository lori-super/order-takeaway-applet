package com.sky.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.properties.BaiduProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class BaiduUtil {
    @Autowired
    private  BaiduProperties baiduProperties;
    private static final String url_Road = "https://api.map.baidu.com/directionlite/v1/driving";

    private static  final String url = "https://api.map.baidu.com/geocoding/v3";
    public  Double distance(String user_address){

        Map<String, String> map = new HashMap<>();
        map.put("address", baiduProperties.getAddress());
        map.put("ak", baiduProperties.getAk());
        map.put("output", "json");
        String result = HttpClientUtil.doGet(url, map);
        JSONObject jsonObject = JSON.parseObject(result);
        Double lng_resource = jsonObject.getJSONObject("result").getJSONObject("location").getDouble("lng");
        Double lat_resource = jsonObject.getJSONObject("result").getJSONObject("location").getDouble("lat");

        map.put("address", user_address);
        String result1 = HttpClientUtil.doGet(url, map);
        JSONObject jsonObject1 = JSON.parseObject(result1);
        Double lng_target = jsonObject1.getJSONObject("result").getJSONObject("location").getDouble("lng");
        Double lat_target = jsonObject1.getJSONObject("result").getJSONObject("location").getDouble("lat");


        Map<String, String> map1 = new HashMap<>();
        String origin = lat_resource + "," + lng_resource ;
        String destination = lat_target + "," + lng_target ;
        System.out.println("origin" + origin + ", destination " + destination);
        map1.put("ak", baiduProperties.getAk());
        map1.put("origin", origin);
        map1.put("destination", destination);
        String drive = HttpClientUtil.doGet(url_Road, map1);
        JSONObject driveJson = JSON.parseObject(drive);
        return driveJson.getJSONObject("result").getJSONArray("routes").getJSONObject(0).getDouble("distance");
    }
}
