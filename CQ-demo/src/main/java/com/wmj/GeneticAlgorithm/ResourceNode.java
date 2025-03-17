package com.wmj.GeneticAlgorithm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author sinahar
 * @version 1.0
 * @description: 资源节点类
 * @date 2025/3/17 23:13
 */
@Data
@AllArgsConstructor
public class ResourceNode implements Runnable{
    int nodeId;
    double cpu;          // CPU性能
    double memory;       // 内存大小
    double bandwidth;    // 网络带宽
    double currentLoad;  // 当前负载
    BlockingQueue<Task> taskQueue; // 任务队列
    boolean isRunning;   // 线程运行状态
    List<Task> processedTasks; // 已处理任务列表

    public ResourceNode(int nodeId, double cpu, double memory, double bandwidth) {
        this.nodeId = nodeId;
        this.cpu = cpu;
        this.memory = memory;
        this.bandwidth = bandwidth;
        this.currentLoad = 0;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.isRunning = true;
        this.processedTasks = new ArrayList<>();
    }
    
    @Override
    public void run() {
        while (isRunning) {
            try {
                Task task = taskQueue.take(); // 从队列中获取任务
                processTask(task); // 处理任务
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 处理任务
     * @param task
     */
    public void processTask(Task task) {
        try {
            // 模拟任务处理时间（简化模型：处理时间 = 数据大小 / (CPU * 内存 * 带宽)）
            double processingTime = task.dataSize / (cpu * memory * bandwidth);
            Thread.sleep((long) (processingTime * 1000)); // 模拟处理延迟
            System.out.printf("节点%d处理了任务%d，视频路径：%s，帧范围：%d-%d%n",
                    nodeId, task.taskId, task.videoPath, task.frameStart, task.frameEnd);
            currentLoad -= task.dataSize; // 更新负载
            processedTasks.add(task); // 添加到已处理任务列表
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 添加任务到队列
     * @param task
     */
    public void addTask(Task task) {
        try {
            taskQueue.put(task);
            currentLoad += task.dataSize; // 添加任务时更新负载
            System.out.printf("任务%d添加到节点%d的队列，当前负载：%.2f%n", task.taskId, nodeId, currentLoad);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 停止线程
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * 获取已处理任务列表
     * @return
     */
    public List<Task> getProcessedTasks() {
        return new ArrayList<>(processedTasks);
    }
}
