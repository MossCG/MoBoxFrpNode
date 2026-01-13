package org.moboxlab.MoBoxFrpNode.Task;

import org.moboxlab.MoBoxFrpNode.BasicInfo;
import org.mossmc.mosscg.MossLib.File.FileCheck;

import java.io.File;

public class TaskUpdateFile {
    public static void executeTask(){
        try {
            rmDir("./MoBoxFrp/frp");
            FileCheck.checkDirExist("./MoBoxFrp/frp");
            FileCheck.checkFileExist("./MoBoxFrp/frp/frps","frp/frps");
            FileCheck.checkFileExist("./MoBoxFrp/frp/frps.exe","frp/frps.exe");
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("执行任务UpdateFile时出现异常！");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void rmDir(String path) {
        File file = new File(path);
        if (!file.exists()) return;
        File[] list = file.listFiles();
        if (list != null) {
            for (File rubbish : list) {
                rubbish.delete();
            }
        }
    }
}
