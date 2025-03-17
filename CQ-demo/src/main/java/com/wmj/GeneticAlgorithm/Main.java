package com.wmj.GeneticAlgorithm;

import com.wmj.GeneticAlgorithm.utils.ResultAggregator;
import com.wmj.GeneticAlgorithm.utils.VideoStreamSplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sinahar
 * @version 1.0
 * @description:
 * @date 2025/3/17 23:18
 */
public class Main {
    public static void main(String[] args) {
        // 视频流参数
        String videoPath = "input_video.mp4";
        int totalFrames = 100; // 总帧数
        double dataSizePerFrame = 10.0; // 每帧数据大小
        int priority = 3; // 任务优先级

        // 分割视频流为任务
        List<Task> tasks = VideoStreamSplitter.splitVideoStream(videoPath, totalFrames, dataSizePerFrame, priority);

        // 初始化资源节点
        List<ResourceNode> resourceNodes = new ArrayList<>();
        resourceNodes.add(new ResourceNode(1, 2.5, 8, 100));
        resourceNodes.add(new ResourceNode(2, 3.0, 16, 150));
        resourceNodes.add(new ResourceNode(3, 2.0, 4, 80));

        // 启动资源节点线程
        ExecutorService executor = Executors.newFixedThreadPool(resourceNodes.size());
        for (ResourceNode node : resourceNodes) {
            executor.execute(node);
        }

        // 使用遗传算法进行任务调度
        List<Integer> bestIndividual = GeneticAlgorithm.run(tasks, resourceNodes);

        // 根据遗传算法的结果分配任务
        for (int i = 0; i < bestIndividual.size(); i++) {
            int nodeId = bestIndividual.get(i);
            ResourceNode node = resourceNodes.stream().filter(n -> n.nodeId == nodeId).findFirst().orElse(null);
            if (node != null) {
                node.addTask(tasks.get(i));
            }
        }

        // 等待任务处理完成
        try {
            Thread.sleep(5000); // 等待5秒让任务处理完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 停止资源节点线程
        for (ResourceNode node : resourceNodes) {
            node.stop();
        }
        executor.shutdown();

        // 汇总并输出结果
        ResultAggregator.aggregateResults(resourceNodes, totalFrames);

        // 打印最终负载情况
        System.out.println("\n最终各节点负载情况：");
        for (ResourceNode node : resourceNodes) {
            System.out.printf("节点%d的当前负载：%.2f%n", node.nodeId, node.currentLoad);
        }
    }
}
