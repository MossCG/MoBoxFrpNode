package org.moboxlab.MoBoxFrpNode.Task;

import org.moboxlab.MoBoxFrpNode.BasicInfo;

import java.io.File;

public class TaskRemoveConfig {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void executeTask(String path){
        try {
            File file = new File(path);
            if (file.exists()) file.delete();
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("执行任务RemoveConfig时出现异常！");
        }
    }
}
