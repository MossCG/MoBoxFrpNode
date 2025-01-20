package org.moboxlab.MoBoxFrpNode.Info;

import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxFrpNode.BasicInfo;

public class InfoLogin {
    public static void sendLoginMessage(JSONObject loginData) {
        if (loginData == null) {
            BasicInfo.logger.sendError("无法连接到主控，登录失败！");
            System.exit(1);
        }
        switch (loginData.getInteger("code")) {
            case 401:
                BasicInfo.logger.sendError("节点校验码错误，登录失败！");
                System.exit(1);
            case 404:
                BasicInfo.logger.sendError("未知的节点编号，登录失败！");
                System.exit(1);
            case 200:
                BasicInfo.logger.sendInfo("登录成功！");
                JSONObject nodeData = loginData.getJSONObject("data");
                BasicInfo.logger.sendInfo("节点状态："+nodeData.getString("enable"));
                BasicInfo.logger.sendInfo("节点域名："+nodeData.getString("domain"));
                BasicInfo.logger.sendInfo("节点名称："+nodeData.getString("name"));
                BasicInfo.logger.sendInfo("节点用户手机号绑定："+nodeData.getString("phone"));
                break;
            default:
                BasicInfo.logger.sendError("未知的响应码，登录失败！");
                System.exit(1);

        }
    }
}
