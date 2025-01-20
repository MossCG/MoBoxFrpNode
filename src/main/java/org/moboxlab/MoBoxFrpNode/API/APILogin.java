package org.moboxlab.MoBoxFrpNode.API;

import com.alibaba.fastjson.JSONObject;

public class APILogin {
    public static JSONObject getResult() {
        JSONObject request = new JSONObject();
        request.put("type","nodeLogin");
        JSONObject result = APIBasic.postAPI(request);
        return result;
    }
}
