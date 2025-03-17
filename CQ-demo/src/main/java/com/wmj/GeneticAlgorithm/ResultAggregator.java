package com.wmj.GeneticAlgorithm;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author sinahar
 * @version 1.0
 * @description:
 * @date 2025/3/17 23:19
 */
public class ResultAggregator {

    // 按帧顺序汇总结果
    public static void aggregateResults(List<ResourceNode> resourceNodes, int totalFrames) {
        // 创建按帧顺序排序的映射
        Map<Integer, Task> frameTaskMap = new TreeMap<>();

        // 收集所有节点的已处理任务
        for (ResourceNode node : resourceNodes) {
            for (Task task : node.getProcessedTasks()) {
                for (int frame = task.frameStart; frame <= task.frameEnd; frame++) {
                    frameTaskMap.put(frame, task);
                }
            }
        }

        // 按帧顺序输出结果
        System.out.println("\n汇总结果：");
        for (int frame = 0; frame < totalFrames; frame++) {
            Task task = frameTaskMap.get(frame);
            if (task != null) {
                System.out.printf("帧%d由任务%d处理，节点%d%n", frame, task.taskId, task.frameStart);
            } else {
                System.out.printf("帧%d未被处理%n", frame);
            }
        }
    }
    
}
