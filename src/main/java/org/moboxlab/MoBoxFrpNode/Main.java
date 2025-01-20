package org.moboxlab.MoBoxFrpNode;

import org.moboxlab.MoBoxFrpNode.API.APILogin;
import org.moboxlab.MoBoxFrpNode.Command.CommandDebug;
import org.moboxlab.MoBoxFrpNode.Command.CommandExit;
import org.moboxlab.MoBoxFrpNode.Info.InfoLogin;
import org.moboxlab.MoBoxFrpNode.Info.InfoStart;
import org.mossmc.mosscg.MossLib.Command.CommandManager;
import org.mossmc.mosscg.MossLib.Config.ConfigManager;
import org.mossmc.mosscg.MossLib.File.FileCheck;
//import org.mossmc.mosscg.MossLib.File.FileDependency;
import org.mossmc.mosscg.MossLib.Object.ObjectLogger;

public class Main {
    public static void main(String[] args) {
        //计时
        long startTime = System.currentTimeMillis();

        //日志模块初始化
        FileCheck.checkDirExist("./MoBoxFrp");
        BasicInfo.logger = new ObjectLogger("./MoBoxFrp/logs");
        //外部依赖初始化（不包含MossLib）
        //FileDependency.loadDependencyDir("./MoBoxFrp/dependency", "dependency");
        //启动消息组
        InfoStart.sendStartMessage();
        //配置文件初始化
        BasicInfo.logger.sendInfo("正在读取配置文件......");
        BasicInfo.config = ConfigManager.getConfigObject("./MoBoxFrp", "config.yml", "config.yml");

        //初始化API
        BasicInfo.logger.sendInfo("正在登录至主控......");
        InfoLogin.sendLoginMessage(APILogin.getResult());

        //命令行初始化
        CommandManager.initCommand(BasicInfo.logger,true);
        CommandManager.registerCommand(new CommandExit());
        CommandManager.registerCommand(new CommandDebug());


        //计时
        long completeTime = System.currentTimeMillis();
        BasicInfo.logger.sendInfo("启动完成！耗时："+(completeTime-startTime)+"毫秒！");
    }
}
