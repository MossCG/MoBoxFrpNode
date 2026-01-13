package org.moboxlab.MoBoxFrpNode.Object;

import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxFrpNode.BasicInfo;

public class ObjectProcess {
    public String name;

    public JSONObject data;

    public void start() {
        BasicInfo.logger.sendInfo("正在启动穿透码："+name);
    }

    public void stop() {
        BasicInfo.logger.sendInfo("正在停止穿透码："+name);
    }
}
