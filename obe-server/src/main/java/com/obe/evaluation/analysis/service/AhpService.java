package com.obe.evaluation.analysis.service;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * AHP层次分析法服务 — 权重计算与一致性检验
 */
@Service
public class AhpService {

    /** Saaty 1-9标度对应的随机一致性指标 RI 值（1~15阶） */
    private static final double[] RI = {0,0,0.58,0.90,1.12,1.24,1.32,1.41,1.45,1.49,1.51,1.48,1.56,1.57,1.58};

    /**
     * 输入：n×n 两两比较矩阵（对角线为1，a_ij = 1/a_ji）
     * 输出：权重向量 + λmax + CI + CR + 是否通过检验
     */
    public Map<String, Object> calculate(double[][] matrix) {
        int n = matrix.length;
        if (n < 2) return Map.of("error", "至少需要2个比较对象");

        // 列归一化
        double[] colSums = new double[n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) colSums[j] += matrix[i][j];
        }
        double[][] norm = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                norm[i][j] = matrix[i][j] / colSums[j];

        // 行求和取平均 → 权重向量
        double[] weights = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) weights[i] += norm[i][j];
            weights[i] /= n;
        }

        // 计算最大特征值
        double lambdaMax = 0;
        for (int i = 0; i < n; i++) {
            double weightedSum = 0;
            for (int j = 0; j < n; j++) weightedSum += matrix[i][j] * weights[j];
            lambdaMax += weightedSum / weights[i];
        }
        lambdaMax /= n;

        // 一致性检验
        double CI = (lambdaMax - n) / (n - 1);
        double ri = n <= RI.length ? RI[Math.min(n - 1, RI.length - 1)] : 1.58;
        double CR = ri > 0 ? CI / ri : 0;

        List<Map<String, Object>> weightList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            weightList.add(Map.of("index", i, "weight", Math.round(weights[i] * 10000.0) / 10000.0));
        }

        return Map.of("weights", weightList, "lambdaMax", Math.round(lambdaMax * 10000.0) / 10000.0,
            "CI", Math.round(CI * 10000.0) / 10000.0, "CR", Math.round(CR * 10000.0) / 10000.0,
            "ri", ri, "consistent", CR < 0.1, "n", n);
    }
}
