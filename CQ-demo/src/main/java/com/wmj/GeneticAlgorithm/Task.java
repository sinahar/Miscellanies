package com.wmj.GeneticAlgorithm;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author sinahar
 * @version 1.0
 * @description: 任务类
 * @date 2025/3/17 22:57
 */
@Data
@AllArgsConstructor
public class Task {
    int taskId;
    String videoPath; // 视频文件路径
    double dataSize;  // 任务数据大小
    int priority;     // 任务优先级
    int frameStart;   // 视频帧起始位置
    int frameEnd;     // 视频帧结束位置
}
