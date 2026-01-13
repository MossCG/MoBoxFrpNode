package org.moboxlab.MoBoxFrpNode.Task;

import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxFrpNode.BasicInfo;

public class TaskSetLimit {
    public static void executeTask(JSONObject data){
        try {
            int band = data.getInteger("band");
            int port = data.getInteger("portServer");
            String token = data.getString("token");
            String systemType = BasicInfo.config.getString("systemType");
            switch (systemType) {
                case "Windows":
                    setWindowsLimit(token,band);
                    break;
                case "Linux":
                    setLinuxLimit(band,port);
                    break;
                default:
                    BasicInfo.logger.sendWarn("不支持的平台类型: " + systemType);
                    break;
            }
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("执行任务SetLimit时出现异常！");
        }
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private static void setWindowsLimit(String token, int band){
        StringBuilder command = new StringBuilder();
        command.append("powershell.exe New-NetQosPolicy ");
        command.append("-Name \"frps_").append(token).append("\" ");
        command.append("-ThrottleRate ").append(band*1280000).append(" ");
        command.append("-AppPathName \"frps_").append(token).append(".exe\" ");
        command.append("-IPProtocol \"Both\"");
        TaskExecuteCommand.executeTask(command.toString());
    }

    private static void setLinuxLimit(int band, int port){
        String network = BasicInfo.config.getString("network");
        //创建限速子类
        StringBuilder command = new StringBuilder();
        command.append("tc class add dev ").append(network).append(" ");
        command.append("parent 1:1 ");
        command.append("classid 1:").append(port).append(" ");
        command.append("htb rate ").append(band).append("mbit ");
        command.append("ceil ").append(band).append("mbit");
        TaskExecuteCommand.executeTask(command.toString());
        //添加入站过滤器
        command = new StringBuilder();
        command.append("tc filter add dev ").append(network).append(" ");
        command.append("protocol ip ");
        command.append("parent 1:0 ");
        command.append("prio 1 u32 match ip ");
        command.append("dport ").append(port).append(" 0xffff ");
        command.append("flowid 1:").append(port);
        TaskExecuteCommand.executeTask(command.toString());
        //添加出站过滤器
        command = new StringBuilder();
        command.append("tc filter add dev ").append(network).append(" ");
        command.append("protocol ip ");
        command.append("parent 1:0 ");
        command.append("prio 1 u32 match ip ");
        command.append("sport ").append(port).append(" 0xffff ");
        command.append("flowid 1:").append(port);
        TaskExecuteCommand.executeTask(command.toString());
    }
}
