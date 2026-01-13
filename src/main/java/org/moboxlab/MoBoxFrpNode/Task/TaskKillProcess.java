package org.moboxlab.MoBoxFrpNode.Task;

import org.moboxlab.MoBoxFrpNode.BasicInfo;

public class TaskKillProcess {
    public static void executeTask(String fileName) {
        try {
            String systemType = BasicInfo.config.getString("systemType");
            switch (systemType) {
                case "Windows":
                    killWindowsProcess(fileName);
                    break;
                case "Linux":
                    killLinuxProcess(fileName);
                    break;
                default:
                    BasicInfo.logger.sendWarn("不支持的平台类型: " + systemType);
                    break;
            }
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("执行任务KillProcess时出现异常！");
        }
    }

    private static void killWindowsProcess(String fileName) {
        try {
            // 检查文件扩展名，如果没有.exe则添加
            String processName = fileName;
            if (!fileName.toLowerCase().endsWith(".exe")) {
                processName = fileName + ".exe";
                BasicInfo.logger.sendInfo("为Windows进程添加.exe扩展名: " + processName);
            }

            // 查找并杀死所有指定进程
            String command = "taskkill /F /IM " + processName;
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                BasicInfo.logger.sendInfo("成功结束进程: " + processName);
            } else if (exitCode == 128) {
                BasicInfo.logger.sendInfo("进程未运行: " + processName);
            } else {
                BasicInfo.logger.sendWarn("结束进程时返回非预期代码: " + exitCode + ", 进程名: " + processName);
            }
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("结束Windows进程时出现异常: " + fileName);
        }
    }

    private static void killLinuxProcess(String fileName) {
        try {
            // 移除可能的.exe扩展名（如果是Windows格式的文件名）
            String processName = fileName;
            if (fileName.toLowerCase().endsWith(".exe")) {
                processName = fileName.substring(0, fileName.length() - 4);
                BasicInfo.logger.sendInfo("移除Linux进程的.exe扩展名: " + processName);
            }

            // 方法1: 使用killall命令
            String[] killallCommand = {"killall", "-9", processName};
            Process killallProcess = Runtime.getRuntime().exec(killallCommand);
            int killallExitCode = killallProcess.waitFor();

            if (killallExitCode == 0) {
                BasicInfo.logger.sendInfo("成功结束进程: " + processName);
            } else {
                // 如果killall失败（可能killall命令不存在），尝试使用pkill
                try {
                    String[] pkillCommand = {"pkill", "-9", processName};
                    Process pkillProcess = Runtime.getRuntime().exec(pkillCommand);
                    int pkillExitCode = pkillProcess.waitFor();

                    if (pkillExitCode == 0) {
                        BasicInfo.logger.sendInfo("成功结束进程: " + processName);
                    } else if (pkillExitCode == 1) {
                        BasicInfo.logger.sendInfo("进程未运行: " + processName);
                    } else {
                        // 如果pkill也失败，尝试使用传统的ps + kill方法
                        killLinuxProcessTraditional(processName);
                    }
                } catch (Exception e) {
                    // 如果pkill失败，使用传统方法
                    killLinuxProcessTraditional(processName);
                }
            }
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("结束Linux进程时出现异常: " + fileName);
        }
    }

    private static void killLinuxProcessTraditional(String processName) {
        try {
            // 方法2: 传统方法 - 使用ps查找PID然后kill
            String[] psCommand = {"ps", "-ef"};
            Process psProcess = Runtime.getRuntime().exec(psCommand);

            StringBuilder output = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(psProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            psProcess.waitFor();

            // 解析输出，查找包含进程名的行
            java.util.List<Integer> pids = new java.util.ArrayList<>();
            String[] lines = output.toString().split("\n");
            for (String line : lines) {
                if (line.contains(processName) && !line.contains("grep") && !line.contains("ps -ef")) {
                    // 提取PID（通常是第二列）
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length > 1) {
                        try {
                            int pid = Integer.parseInt(parts[1]);
                            pids.add(pid);
                        } catch (NumberFormatException e) {
                            // 忽略无法解析PID的行
                        }
                    }
                }
            }

            // 杀死找到的进程
            if (pids.isEmpty()) {
                BasicInfo.logger.sendInfo("进程未运行: " + processName);
                return;
            }

            for (int pid : pids) {
                try {
                    String[] killCommand = {"kill", "-9", String.valueOf(pid)};
                    Process killProcess = Runtime.getRuntime().exec(killCommand);
                    killProcess.waitFor();
                    BasicInfo.logger.sendInfo("成功结束进程: " + processName + " (PID: " + pid + ")");
                } catch (Exception e) {
                    BasicInfo.logger.sendWarn("无法结束进程PID: " + pid);
                }
            }

        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("使用传统方法结束Linux进程时出现异常: " + processName);
        }
    }
}
