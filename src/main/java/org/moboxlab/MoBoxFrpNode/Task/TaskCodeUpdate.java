package org.moboxlab.MoBoxFrpNode.Task;

import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxFrpNode.API.APICode;
import org.moboxlab.MoBoxFrpNode.BasicInfo;
import org.moboxlab.MoBoxFrpNode.Cache.CacheProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskCodeUpdate {
    public static void executeTask(){
        try {
            JSONObject result = APICode.getResult();
            if (result == null) {
                BasicInfo.logger.sendWarn("更新穿透码列表失败：无法连接到主控！");
                return;
            }
            if (!result.getBoolean("success")) {
                BasicInfo.logger.sendWarn("更新穿透码列表失败："+result.getString("message"));
                return;
            }
            BasicInfo.sendDebug("成功从主控获取穿透码列表，数量："+result.getJSONArray("codes").size());
            //整理穿透码
            Map<String,JSONObject> startMap = new HashMap<>();
            List<String> keyList = new ArrayList<>();
            List<String> stopList = new ArrayList<>();
            //整理缺失值
            for (Object o : result.getJSONArray("codes")) {
                JSONObject codeData = JSONObject.parseObject(o.toString());
                String key = getKey(codeData);
                keyList.add(key);
                if (!CacheProcess.processMap.containsKey(key)) {
                    startMap.put(key,codeData);
                }
            }
            //整理多余值
            for (String key : CacheProcess.processMap.keySet()) {
                if (!keyList.contains(key)) {
                    stopList.add(key);
                }
            }
            //先执行删除操作
            for (String key : stopList) {
                TaskCodeStop.executeTask(key);
            }
            //再执行创建操作
            startMap.forEach(TaskCodeStart::executeTask);
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("执行任务CodeUpdate时出现异常！");
        }
    }

    public static String getKey(JSONObject codeData) {
        return "#" +
        codeData.getString("codeID") + "-" +
        codeData.getString("number") + "-" +
        codeData.getString("band") + "Mbps-SP" +
        codeData.getString("portServer") + "-OP" +
        codeData.getString("portOpen") + "|" +
        codeData.getString("token");
    }
}
