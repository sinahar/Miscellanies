package com.wmj.GeneticAlgorithm;

import java.util.*;

/**
 * @author sinahar
 * @version 1.0
 * @description: 遗传算法类
 * @date 2025/3/17 23:15
 */
public class GeneticAlgorithm {

    /**
     * 计算适应度函数
     * @param individual
     * @param tasks
     * @param resourceNodes
     * @return
     */
    public static double calculateFitness(List<Integer> individual, List<Task> tasks, List<ResourceNode> resourceNodes) {
        // 将个体解码为任务分配方案
        Map<Integer, Integer> taskAssignment = new HashMap<>();
        for (int i = 0; i < individual.size(); i++) {
            taskAssignment.put(tasks.get(i).taskId, individual.get(i));
        }

        // 计算各资源节点的负载
        Map<Integer, Double> nodeLoad = new HashMap<>();
        for (ResourceNode node : resourceNodes) {
            nodeLoad.put(node.nodeId, 0.0);
        }
        for (Task task : tasks) {
            int nodeId = taskAssignment.get(task.taskId);
            nodeLoad.put(nodeId, nodeLoad.get(nodeId) + task.dataSize);
        }

        // 计算负载均衡度（标准差）
        double meanLoad = nodeLoad.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double loadStd = Math.sqrt(nodeLoad.values().stream().mapToDouble(load -> Math.pow(load - meanLoad, 2)).average().orElse(0.0));

        // 计算任务完成时间（假设与数据大小和节点性能相关）
        double totalTime = 0.0;
        for (ResourceNode node : resourceNodes) {
            double nodeTotalData = nodeLoad.get(node.nodeId);
            // 简化模型：完成时间 = 数据大小 / (CPU * 内存 * 带宽)
            double nodeTime = nodeTotalData / (node.cpu * node.memory * node.bandwidth);
            totalTime += nodeTime;
        }

        // 计算资源利用率
        double totalResource = resourceNodes.stream().mapToDouble(node -> node.cpu * node.memory * node.bandwidth).sum();
        double usedResource = resourceNodes.stream().mapToDouble(node -> node.currentLoad / (node.cpu * node.memory * node.bandwidth)).sum();
        double resourceUtilization = usedResource / totalResource;

        // 综合适应度函数：负载均衡度权重较高，同时考虑任务完成时间和资源利用率
        double fitness = 0.5 * (1.0 / (loadStd + 1e-6)) + 0.3 * (1.0 / (totalTime + 1e-6)) + 0.2 * resourceUtilization;
        return fitness;
    }

    /**
     * 选择操作（轮盘赌选择）
     * @param population
     * @param fitnessScores
     * @param numParents
     * @return
     */
    public static List<List<Integer>> selection(List<List<Integer>> population, List<Double> fitnessScores, int numParents) {
        List<List<Integer>> parents = new ArrayList<>();
        double fitnessSum = fitnessScores.stream().mapToDouble(Double::doubleValue).sum();
        double[] selectionProbs = new double[fitnessScores.size()];
        for (int i = 0; i < fitnessScores.size(); i++) {
            selectionProbs[i] = fitnessScores.get(i) / fitnessSum;
        }

        Random random = new Random();
        for (int i = 0; i < numParents; i++) {
            double randProb = random.nextDouble();
            double cumulativeProb = 0.0;
            for (int j = 0; j < population.size(); j++) {
                cumulativeProb += selectionProbs[j];
                if (randProb <= cumulativeProb) {
                    parents.add(population.get(j));
                    break;
                }
            }
        }
        return parents;
    }

    /**
     * 交叉操作（单点交叉）
     * @param parents
     * @param numOffspring
     * @return
     */
    public static List<List<Integer>> crossover(List<List<Integer>> parents, int numOffspring) {
        List<List<Integer>> offspring = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numOffspring; i++) {
            if (parents.size() < 2) {
                break;
            }
            List<Integer> parent1 = parents.get(random.nextInt(parents.size()));
            List<Integer> parent2 = parents.get(random.nextInt(parents.size()));
            int crossoverPoint = random.nextInt(parent1.size());
            List<Integer> child = new ArrayList<>(parent1.subList(0, crossoverPoint));
            child.addAll(parent2.subList(crossoverPoint, parent2.size()));
            offspring.add(child);
        }
        return offspring;
    }

    /**
     * 变异操作
     * @param offspring
     * @param mutationRate
     * @param resourceNodes
     * @return
     */
    public static List<List<Integer>> mutation(List<List<Integer>> offspring, double mutationRate, List<ResourceNode> resourceNodes) {
        Random random = new Random();
        for (List<Integer> individual : offspring) {
            for (int i = 0; i < individual.size(); i++) {
                if (random.nextDouble() < mutationRate) {
                    int newNode = resourceNodes.get(random.nextInt(resourceNodes.size())).nodeId;
                    individual.set(i, newNode);
                }
            }
        }
        return offspring;
    }

    /**
     * 遗传算法主流程
     * @param tasks
     * @param resourceNodes
     * @return
     */
    public static List<Integer> run(List<Task> tasks, List<ResourceNode> resourceNodes) {
        int numGenerations = 100;
        int populationSize = 50;
        double mutationRate = 0.1;

        // 初始化种群
        List<List<Integer>> population = initializePopulation(populationSize, tasks, resourceNodes);

        for (int generation = 0; generation < numGenerations; generation++) {
            // 计算适应度
            List<Double> fitnessScores = new ArrayList<>();
            for (List<Integer> individual : population) {
                fitnessScores.add(calculateFitness(individual, tasks, resourceNodes));
            }

            // 选择父母
            List<List<Integer>> parents = selection(population, fitnessScores, 20);

            // 交叉产生后代
            List<List<Integer>> offspring = crossover(parents, 30);

            // 变异操作
            offspring = mutation(offspring, mutationRate, resourceNodes);

            // 新一代种群
            population = new ArrayList<>(parents);
            population.addAll(offspring);

            // 保持种群数量稳定
            while (population.size() > populationSize) {
                population.remove(population.size() - 1);
            }
        }

        // 最终选择适应度最高的个体作为解
        List<Double> fitnessScores = new ArrayList<>();
        for (List<Integer> individual : population) {
            fitnessScores.add(calculateFitness(individual, tasks, resourceNodes));
        }
        double maxFitness = Collections.max(fitnessScores);
        int bestIndex = fitnessScores.indexOf(maxFitness);
        return population.get(bestIndex);
    }

    /**
     * 种群初始化
     * @param numIndividuals
     * @param tasks
     * @param resourceNodes
     * @return
     */
    private static List<List<Integer>> initializePopulation(int numIndividuals, List<Task> tasks, List<ResourceNode> resourceNodes) {
        List<List<Integer>> population = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numIndividuals; i++) {
            List<Integer> individual = new ArrayList<>();
            for (Task task : tasks) {
                int selectedNode = random.nextInt(resourceNodes.size()) + 1; // 假设节点ID从1开始
                individual.add(selectedNode);
            }
            population.add(individual);
        }
        return population;
    }
    
}
