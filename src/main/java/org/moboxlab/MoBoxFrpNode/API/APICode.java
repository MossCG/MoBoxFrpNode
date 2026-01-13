package org.moboxlab.MoBoxFrpNode.API;

import com.alibaba.fastjson.JSONObject;

public class APICode {
    public static JSONObject getResult() {
        String route = "/NodeAPI/Code";
        JSONObject request = new JSONObject();
        return APIBasic.postAPI(route,request);
    }
}
