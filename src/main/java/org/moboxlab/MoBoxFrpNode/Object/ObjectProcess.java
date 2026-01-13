package org.moboxlab.MoBoxFrpNode.Object;

import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxFrpNode.BasicInfo;
import org.moboxlab.MoBoxFrpNode.Task.TaskRemoveConfig;
import org.moboxlab.MoBoxFrpNode.Task.TaskWriteConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ObjectProcess {
    //进程名称
    public String name;
    //进程基本数据
    public JSONObject data;

    //守护线程
    public Thread daemon;
    //进程本体
    public Process process;

    //配置文件名称
    public String configFile;
    //可执行文件名称
    public String executeFile =  "./MoBoxFrp/frp/frps.exe";

    //启动方法
    public void start() throws Exception{
        BasicInfo.logger.sendInfo("正在启动穿透码："+name);
        //写入配置
        configFile = "./MoBoxFrp/frp/"+data.getString("token")+".toml";
        TaskWriteConfig.executeTask(configFile,data);
        //启动进程
        String command = executeFile+" -c "+configFile;
        process = Runtime.getRuntime().exec(command);
        daemon = new Thread(() -> daemonVoid(this));
        daemon.start();
    }

    //停止方法
    public void stop() throws Exception{
        BasicInfo.logger.sendInfo("正在停止穿透码："+name);
        //停止进程
        daemon.interrupt();
        process.destroy();
        //删除配置
        TaskRemoveConfig.executeTask(configFile);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void daemonVoid(ObjectProcess object) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(object.process.getInputStream()));
        while (true) {
            try {
                String readLine;
                while ((readLine = reader.readLine())!=null) {
                    BasicInfo.sendDebug(readLine);
                }
            } catch (Exception e) {
                BasicInfo.logger.sendException(e);
                BasicInfo.logger.sendWarn("守护进程出现错误！穿透码名称："+object.name);
            }
        }
    }
}
