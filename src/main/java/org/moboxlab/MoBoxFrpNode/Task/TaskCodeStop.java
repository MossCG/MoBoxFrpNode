package org.moboxlab.MoBoxFrpNode.Task;

import org.moboxlab.MoBoxFrpNode.BasicInfo;
import org.moboxlab.MoBoxFrpNode.Cache.CacheProcess;
import org.moboxlab.MoBoxFrpNode.Object.ObjectProcess;

public class TaskCodeStop {
    public static void executeTask(String key){
        try {
            ObjectProcess process = CacheProcess.processMap.get(key);
            process.stop();
            CacheProcess.processMap.remove(key);
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("执行任务CodeStop时出现异常！");
        }
    }
}
