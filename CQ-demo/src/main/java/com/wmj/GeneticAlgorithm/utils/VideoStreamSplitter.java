package com.wmj.GeneticAlgorithm.utils;

import com.wmj.GeneticAlgorithm.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sinahar
 * @version 1.0
 * @description: 视频流处理工具
 * @date 2025/3/17 23:17
 */
public class VideoStreamSplitter {

    /**
     * 分割视频流为每10帧一个任务
     * @param videoPath
     * @param totalFrames
     * @param dataSizePerFrame
     * @param priority
     * @return
     */
    public static List<Task> splitVideoStream(String videoPath, int totalFrames, double dataSizePerFrame, int priority) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < totalFrames; i += 10) {
            int frameStart = i;
            int frameEnd = Math.min(i + 9, totalFrames - 1);
            double dataSize = (frameEnd - frameStart + 1) * dataSizePerFrame;
            tasks.add(new Task(tasks.size() + 1, videoPath, dataSize, priority, frameStart, frameEnd));
        }
        return tasks;
    }
    
}
