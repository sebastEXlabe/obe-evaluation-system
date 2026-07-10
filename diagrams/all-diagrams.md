# 论文图表 Mermaid 代码
# 渲染命令：npx @mermaid-js/mermaid-cli --input xxx.mmd --output xxx.png --scale 4

# ============================================================
# 图1 系统逻辑架构图（第4章 4.1 总体架构）
# ============================================================
```mermaid
graph TB
    Browser(["用户浏览器"])
    Browser --> Nginx["Nginx :80<br/>静态资源 + API代理 + MaxKB代理"]

    subgraph Frontend["前端 obe-web"]
        direction LR
        Vue["Vue 3 + Element Plus"]
        Pinia["Pinia 状态管理"]
        G2["AntV G2 可视化"]
    end

    Nginx --> Frontend
    Nginx -->|"/api/*"| Backend

    subgraph Backend["后端 obe-server :8989"]
        Security["Spring Security + JWT 认证"]
        direction LR
        M1["认证授权"]
        M2["课程管理"]
        M3["小组管理"]
        M4["项目追踪"]
        M5["多元评价"]
        M6["达成度分析"]
        M7["AI模块"]
    end

    Backend --> Data

    subgraph Data["数据层"]
        PG[("PostgreSQL 16<br/>pgvector")]
        Redis[("Redis 7")]
        MinIO[("MinIO")]
    end

    Nginx -->|"/admin/*"| MaxKB
    M7 --> DeepSeek
    M7 --> MaxKB

    subgraph External["外部服务"]
        DeepSeek["DeepSeek API"]
        MaxKB["MaxKB v2.10.3"]
    end

    style Browser fill:#E8F5E9,stroke:#00A087
    style Nginx fill:#FFF3E0,stroke:#F4A261
    style DeepSeek fill:#E3F2FD,stroke:#4DBBD5
```

# ============================================================
# 图2 系统部署架构图（第4章 4.1 总体架构）
# ============================================================
```mermaid
graph TB
    User["用户浏览器"] -->|":80"| Web

    subgraph Docker["Docker 宿主机"]
        Web["obe-web<br/>nginx :80"]
        Server["obe-server<br/>Spring Boot :8989"]
        PG["obe-postgres<br/>PostgreSQL 16 :5432"]
        Redis["obe-redis<br/>Redis 7 :6379"]
        MinIO["obe-minio<br/>MinIO :9000"]
        MaxKB["obe-maxkb<br/>MaxKB :8080"]
    end

    Web -->|"/api/"| Server
    Web -->|"/admin/"| MaxKB
    Server --> PG
    Server --> Redis
    Server --> MinIO
    Server --> MaxKB
    Server -->|HTTPS| DeepSeek

    DeepSeek["DeepSeek API"]

    style User fill:#E8F5E9,stroke:#00A087
    style Web fill:#FFF3E0,stroke:#F4A261
    style Server fill:#FFF3E0,stroke:#F4A261
    style DeepSeek fill:#E3F2FD,stroke:#4DBBD5
```

# ============================================================
# 图3 用户与认证模块ER图（第4章 4.3 数据库设计）
# ============================================================
```mermaid
erDiagram
    SYS_USER ||--o{ AUDIT_LOG : 操作
    SYS_USER ||--o{ NOTIFICATION : 接收
    SYS_USER {
        bigint id PK
        varchar username
        varchar password
        varchar real_name
        varchar role_code
    }
    AUDIT_LOG {
        bigint id PK
        bigint user_id FK
        varchar action
        varchar target
        timestamp created_at
    }
    NOTIFICATION {
        bigint id PK
        bigint user_id FK
        varchar title
        text content
        boolean is_read
    }
```

# ============================================================
# 图4 OBE评价体系ER图（第4章 4.3 数据库设计）
# ============================================================
```mermaid
erDiagram
    COURSE ||--o{ COURSE_OBJECTIVE : 包含
    COURSE_OBJECTIVE ||--o{ GRADUATION_INDICATOR : 分解为
    COURSE_OBJECTIVE ||--o{ OBJECTIVE_REQUIREMENT_MAPPING : 映射
    GRADUATION_REQUIREMENT ||--o{ OBJECTIVE_REQUIREMENT_MAPPING : 映射
    GRADUATION_INDICATOR ||--o{ EVALUATION_METHOD : 评价方式

    COURSE {
        bigint id PK
        varchar course_name
        varchar semester
        bigint teacher_id FK
    }
    COURSE_OBJECTIVE {
        bigint id PK
        varchar objective_no
        varchar title
        varchar dimension
        numeric weight
        bigint course_id FK
    }
    GRADUATION_REQUIREMENT {
        bigint id PK
        varchar req_no
        varchar title
        text description
    }
    GRADUATION_INDICATOR {
        bigint id PK
        varchar indicator_no
        varchar title
        bigint objective_id FK
    }
    OBJECTIVE_REQUIREMENT_MAPPING {
        bigint id PK
        bigint objective_id FK
        bigint requirement_id FK
        varchar support_level
    }
    EVALUATION_METHOD {
        bigint id PK
        varchar method_name
        numeric weight
        varchar data_source
        numeric full_score
        bigint indicator_id FK
    }
```

# ============================================================
# 图5 小组与成员模块ER图（第4章 4.3 数据库设计）
# ============================================================
```mermaid
erDiagram
    PROJECT_GROUP ||--o{ GROUP_MEMBER : 成员
    GROUP_MEMBER ||--o{ POSITION_HISTORY : 变更记录
    SYS_USER ||--o{ GROUP_MEMBER : 参与

    PROJECT_GROUP {
        bigint id PK
        varchar group_name
        bigint teacher_id FK
        int max_members
        varchar invite_code
        bigint course_id FK
    }
    GROUP_MEMBER {
        bigint id PK
        bigint group_id FK
        bigint user_id FK
        varchar role_code
        timestamp join_at
    }
    POSITION_HISTORY {
        bigint id PK
        bigint group_id FK
        bigint user_id FK
        varchar old_role
        varchar new_role
        bigint changed_by FK
        varchar reason
    }
```

# ============================================================
# 图6 项目追踪模块ER图（第4章 4.3 数据库设计）
# ============================================================
```mermaid
erDiagram
    PROJECT_GROUP ||--o{ PROJECT_MILESTONE : 里程碑
    PROJECT_MILESTONE ||--o{ PROJECT_TASK : 任务
    PROJECT_GROUP ||--o{ PROJECT_JOURNAL : 日志
    PROJECT_GROUP ||--o{ GIT_COMMIT_LOG : 提交
    PROJECT_GROUP ||--o{ REPO_CONFIG : 仓库
    PROJECT_GROUP ||--o{ REQUIREMENT_CHANGE : 变更
    PROJECT_GROUP ||--o{ CONTRIBUTION_LOG : 贡献

    PROJECT_MILESTONE {
        bigint id PK
        bigint group_id FK
        varchar title
        varchar cdio_phase
        date due_date
        boolean is_done
    }
    PROJECT_TASK {
        bigint id PK
        bigint group_id FK
        bigint milestone_id FK
        bigint assignee_id FK
        varchar title
        varchar status
    }
    PROJECT_JOURNAL {
        bigint id PK
        bigint user_id FK
        bigint group_id FK
        text content
        date journal_date
    }
    GIT_COMMIT_LOG {
        bigint id PK
        bigint user_id FK
        bigint group_id FK
        varchar commit_hash
        text message
        int additions
        int deletions
        timestamp committed_at
    }
    REPO_CONFIG {
        bigint id PK
        bigint group_id FK
        varchar platform
        varchar owner
        varchar repo
        varchar branch
        boolean enabled
    }
    REQUIREMENT_CHANGE {
        bigint id PK
        bigint group_id FK
        varchar title
        text description
        varchar change_type
        bigint created_by FK
    }
    CONTRIBUTION_LOG {
        bigint id PK
        bigint user_id FK
        bigint group_id FK
        varchar description
        numeric bonus_score
        boolean approved
        bigint approved_by FK
    }
```

# ============================================================
# 图7 评价与成绩模块ER图（第4章 4.3 数据库设计）
# ============================================================
```mermaid
erDiagram
    PROJECT_GROUP ||--o{ GROUP_EVALUATION : 小组评分
    PROJECT_GROUP ||--o{ PERSONAL_SCORE : 个人成绩
    PROJECT_GROUP ||--o{ ROLE_EVALUATION : 角色评价
    SYS_USER ||--o{ PERSONAL_SCORE : 得分
    SYS_USER ||--o{ ROLE_EVALUATION : 被评
    PERSONAL_SCORE ||--o{ SCORE_DISPUTE : 申诉

    GROUP_EVALUATION {
        bigint id PK
        bigint group_id FK
        bigint evaluator_id FK
        varchar dimension
        numeric score
        text comment
    }
    PERSONAL_SCORE {
        bigint id PK
        bigint user_id FK
        bigint group_id FK
        numeric group_total_score
        numeric contribution_ratio
        numeric final_score
        numeric bonus_total
    }
    ROLE_EVALUATION {
        bigint id PK
        bigint user_id FK
        bigint group_id FK
        bigint evaluator_id FK
        varchar role_code
        varchar dimension
        numeric score
    }
    SCORE_DISPUTE {
        bigint id PK
        bigint user_id FK
        bigint score_id
        varchar score_type
        text reason
        varchar status
    }
```

# ============================================================
# 图8 AI与知识库模块ER图（第4章 4.3 数据库设计）
# ============================================================
```mermaid
erDiagram
    KNOWLEDGE_POINT ||--o{ QA_RECORD : 关联
    SYS_USER ||--o{ QA_RECORD : 提问
    SYS_USER ||--o{ SELF_TEST_RECORD : 自测
    PROJECT_GROUP ||--o{ QUESTION_ANSWER : 问答

    KNOWLEDGE_POINT {
        bigint id PK
        varchar title
        varchar chapter
        text content
    }
    QA_RECORD {
        bigint id PK
        bigint user_id FK
        bigint knowledge_id FK
        text question
        text answer
        boolean is_resolved
        timestamp asked_at
    }
    SELF_TEST_RECORD {
        bigint id PK
        bigint user_id FK
        numeric score
        numeric total
        timestamp taken_at
    }
    QUESTION_ANSWER {
        bigint id PK
        bigint group_id FK
        bigint ask_user_id FK
        bigint answer_user_id FK
        varchar title
        text question
        text answer
        varchar status
    }
```

# ============================================================
# 图9 数据采集阶段流程图（第4章 4.2.4 多元过程性评价）
# ============================================================
```mermaid
graph TB
    Start([课程开始]) --> SetObj[设定课程目标<br/>KNOWLEDGE/ABILITY/QUALITY]
    SetObj --> AHP{AHP一致性校验<br/>CR < 0.1?}
    AHP -->|不通过| Adjust[调整判断矩阵]
    Adjust --> AHP
    AHP -->|通过| Map[建立CO-PO映射矩阵]
    Map --> Form[组建项目小组<br/>分配PM/DEV/TEST/DOC]
    Form --> StartCDIO[启动CDIO项目]

    StartCDIO --> GitChan[Git提交采集<br/>Commit次数+增删行数]
    StartCDIO --> JourChan[项目日志采集<br/>每日/每周进展]
    StartCDIO --> QAChan[AI问答采集<br/>提问次数+知识点匹配]
    StartCDIO --> TestChan[自测成绩采集<br/>正答率+完成时间]

    style AHP fill:#FFF8E1,stroke:#E69F00
    style GitChan fill:#E8F5E9,stroke:#00A087
    style JourChan fill:#E8F5E9,stroke:#00A087
    style QAChan fill:#E8F5E9,stroke:#00A087
    style TestChan fill:#E8F5E9,stroke:#00A087
```

# ============================================================
# 图10 贡献度计算流程图（第4章 4.2.4 多元过程性评价）
# ============================================================
```mermaid
graph TB
    GitData["Git提交数据"] --> RawScore
    JourData["项目日志数据"] --> RawScore
    QAData["AI问答数据"] --> RawScore
    TestData["自测成绩数据"] --> RawScore
    
    RawScore["计算原始贡献分 S_i<br/>0.35×Git + 0.20×日志<br/>+ 0.25×问答+自测 + 0.20×加分"]
    
    RawScore --> FindMax["找到小组最高分 S_max"]
    FindMax --> Bayes["贝叶斯平滑<br/>r_i = S_i + C×prior / S_max + C<br/>prior = 1/n, C = 50"]
    Bayes --> Check{"r_i 是否异常?"}
    Check -->|否| Final["最终成绩<br/>Score = Score_group × r_i + Bonus"]
    Check -->|是| Flag["标记人工审核"]
    Final --> Persist["持久化存储<br/>personal_score 表"]
    
    style Bayes fill:#DAE8FC,stroke:#6C8EBF
    style Final fill:#D5E8D4,stroke:#82B366
```

# ============================================================
# 图11 达成度分析流程图（第4章 4.2.4 多元过程性评价）
# ============================================================
```mermaid
graph TB
    EvalData["评价数据汇总"] --> COCalc["课程目标达成度<br/>加权平均法<br/>A = Σ a_i·w_i / Σ w_i"]
    COCalc --> DimCalc["维度分数汇总<br/>KNOWLEDGE/ABILITY/QUALITY"]
    COCalc --> POCalc["CO→PO 映射<br/>PO_j = min{CO_k}"]
    
    DimCalc --> Radar["达成度雷达图"]
    POCalc --> BarChart["毕业要求柱状图"]
    COCalc --> Heatmap["达成度热力图"]
    
    Radar --> CQI{"达成度 ≥ 0.6?"}
    BarChart --> CQI
    Heatmap --> CQI
    
    CQI -->|否| Improve["生成改进建议<br/>薄弱环节预警<br/>教学策略调整"]
    Improve --> Back["反馈至下轮课程"]
    CQI -->|是| End([课程结束])
    
    style CQI fill:#E1D5E7,stroke:#9673A6
    style COCalc fill:#DAE8FC,stroke:#6C8EBF
    style POCalc fill:#DAE8FC,stroke:#6C8EBF
```
