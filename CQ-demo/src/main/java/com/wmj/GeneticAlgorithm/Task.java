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
    double dataSize; // 任务数据大小
    int priority;    // 任务优先级
}
