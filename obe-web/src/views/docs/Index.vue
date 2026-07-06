<template>
  <div class="docs-page">
    <div class="docs-header">
      <h1>OBE-CDIO 教学评价管理系统 — 使用文档</h1>
      <p class="docs-subtitle">基于 OBE 成果导向教育理念与 CDIO 工程教育模式的综合教学评价平台</p>
    </div>

    <el-collapse v-model="activeSections" class="docs-collapse">
      <el-collapse-item title="一、系统概述" name="1">
        <div class="docs-content">
          <p>OBE-CDIO 教学评价管理系统是一套面向工程教育的全过程教学质量管理平台。系统以 OBE（Outcome-Based Education，成果导向教育）理念为核心，结合 CDIO（Conceive-Design-Implement-Operate，构思-设计-实施-运行）工程教育模式，实现了从课程目标设定、教学过程追踪到学习成果评价的完整闭环。</p>
          <h4>核心功能模块</h4>
          <ul>
            <li><strong>课程目标管理</strong>：支持课程目标、指标点、评价方式及 CDIO 映射的四级体系构建</li>
            <li><strong>小组管理</strong>：学生可通过邀请码加入小组，教师可管理小组及成员</li>
            <li><strong>智能问答</strong>：基于 AI 的课程答疑系统，支持文件上传与多轮对话</li>
            <li><strong>项目追踪</strong>：涵盖里程碑、任务看板、日志、Git 提交、贡献度及代码仓库的六维项目管理</li>
            <li><strong>多元评价</strong>：支持小组评价、角色评价及个人成绩计算</li>
            <li><strong>达成度分析</strong>：基于 AHP 层次分析法的课程目标达成度量化分析</li>
          </ul>
        </div>
      </el-collapse-item>

      <el-collapse-item title="二、角色与权限指南" name="2">
        <div class="docs-content">
          <h4>管理员 (ADMIN)</h4>
          <ul>
            <li>拥有系统全部功能权限</li>
            <li>可管理所有课程、小组、用户</li>
            <li>可查看所有学生的成绩与达成度数据</li>
            <li>可在个人中心创建/禁用/删除用户账号</li>
          </ul>
          <h4>教师 (TEACHER)</h4>
          <ul>
            <li>可创建和管理课程目标体系</li>
            <li>可创建小组、管理成员</li>
            <li>可使用智能问答系统回答学生问题</li>
            <li>可追踪项目进度并评价学生贡献</li>
            <li>可进行多元评价打分与达成度分析</li>
            <li>可在个人中心查看小组概览和学生达成度</li>
          </ul>
          <h4>学生 (STUDENT)</h4>
          <ul>
            <li>通过邀请码加入课程小组</li>
            <li>查看所在小组的成员信息</li>
            <li>使用智能问答系统进行学习咨询</li>
            <li>参与项目追踪（任务、日志、提交记录等）</li>
            <li>查看个人成绩与达成度分析</li>
          </ul>
        </div>
      </el-collapse-item>

      <el-collapse-item title="三、OBE 工作流程" name="3">
        <div class="docs-content">
          <p>OBE（成果导向教育）的核心流程在本系统中体现为以下步骤：</p>
          <el-steps direction="vertical" :space="30" style="margin-top:12px">
            <el-step title="步骤 1：定义课程目标" description="教师在'课程目标'模块中创建课程目标，设定目标编号、维度（知识/能力/素养）及权重。" />
            <el-step title="步骤 2：细化指标点" description="为每个课程目标添加具体的指标点，明确可观测、可衡量的能力要求。" />
            <el-step title="步骤 3：配置评价方式" description="为每个指标点设置评价方式（手动打分/MaxKB AI/Git 提交分析/测验/日志），设定权重和满分。" />
            <el-step title="步骤 4：映射 CDIO 阶段" description="将指标点映射到 CDIO 工程教育的四个阶段（构思/设计/实施/运行），确保工程实践能力全覆盖。" />
            <el-step title="步骤 5：教学实施与数据采集" description="在项目追踪模块中记录里程碑完成、任务进展、Git 提交、日志等过程数据。" />
            <el-step title="步骤 6：多元评价打分" description="教师进行小组评价和角色评价，系统自动计算个人成绩。" />
            <el-step title="步骤 7：达成度分析与改进" description="系统基于 AHP 法计算课程目标达成度，生成雷达图，为教学改进提供数据支撑。" />
          </el-steps>
        </div>
      </el-collapse-item>

      <el-collapse-item title="四、CDIO 工程教育阶段" name="4">
        <div class="docs-content">
          <p>CDIO 代表构思（Conceive）、设计（Design）、实施（Implement）和运行（Operate），是国际工程教育改革的创新模式。系统中将课程指标点映射到这四个阶段：</p>
          <el-table :data="cdioStages" border stripe style="margin-top:12px">
            <el-table-column prop="phase" label="阶段" width="80" />
            <el-table-column prop="name" label="中文名称" width="80" />
            <el-table-column prop="description" label="说明" />
            <el-table-column prop="tagType" label="标识" width="80">
              <template #default="{ row }"><el-tag :type="row.tagType" size="small">{{ row.name }}</el-tag></template>
            </el-table-column>
          </el-table>
        </div>
      </el-collapse-item>

      <el-collapse-item title="五、评价公式与算法" name="5">
        <div class="docs-content">
          <h4>5.1 课程目标权重</h4>
          <p>课程目标权重通过 AHP（层次分析法）确定。系统通过两两比较矩阵计算各目标的相对重要性，并进行一致性检验（CR &lt; 0.1 为通过）。</p>

          <h4>5.2 指标点达成度</h4>
          <p>指标点达成度 = 各评价方式得分加权和 / 各评价方式满分加权和</p>
          <p>其中评价方式分为过程性评价（FORMATIVE）和终结性评价（SUMMATIVE）。</p>

          <h4>5.3 课程目标达成度</h4>
          <p>课程目标达成度 = Min{下属所有指标点的达成度}</p>
          <p>采用最小值法，确保学生对目标所涵盖的所有指标点均达到要求。</p>

          <h4>5.4 个人最终成绩</h4>
          <p>个人最终成绩 = 小组评价得分 x 个人贡献系数 + 个人加分</p>
          <p>其中贡献系数由角色评价、Git 贡献度、日志参与度等多维因素综合计算。</p>

          <h4>5.5 整体达成度</h4>
          <p>整体达成度 = SUM(各课程目标达成度 x 目标权重)</p>
          <p>整体达成度 &ge; 60% 视为合格，低于 60% 需要重点关注和改进。</p>
        </div>
      </el-collapse-item>

      <el-collapse-item title="六、常见问题 (FAQ)" name="6">
        <div class="docs-content">
          <div class="faq-item" v-for="faq in faqs" :key="faq.q">
            <h4>{{ faq.q }}</h4>
            <p>{{ faq.a }}</p>
          </div>
        </div>
      </el-collapse-item>

      <el-collapse-item title="七、论文写作：OBE-CDIO 理论映射与创新点" name="7">
        <div class="section-body">
          <h4>7.1 系统模块与 OBE-CDIO 理论对应</h4>
          <table border="1" style="width:100%;border-collapse:collapse;margin:12px 0;font-size:13px">
            <tr style="background:#f5f7fa"><th>系统模块</th><th>OBE理论</th><th>CDIO阶段</th></tr>
            <tr><td>课程目标管理</td><td>CO→PO映射, AHP层次分析法</td><td>顶层设计</td></tr>
            <tr><td>小组岗位管理</td><td>能力本位教育</td><td>组织架构</td></tr>
            <tr><td>MaxKB智能问答</td><td>过程性评价, 形成性评价</td><td>实施支撑</td></tr>
            <tr><td>项目过程追踪</td><td>实践导向学习</td><td>C-D-I-O全阶段</td></tr>
            <tr><td>多元过程性评价</td><td>多元评估, 真实性评价</td><td>实施-运行</td></tr>
            <tr><td>达成度分析</td><td>成果导向, 持续改进</td><td>质量闭环</td></tr>
          </table>

          <h4>7.2 系统创新点</h4>
          <ul>
            <li><b>多源数据自动采集</b>：Git/日志/AI问答/自测 四通道数据自动流入评价系统，解决过程性考核数据采集难题</li>
            <li><b>贝叶斯平滑贡献度算法</b>：综合Git(35%)+日志(20%)+问答自测(25%)+贡献加分(20%)，小样本时拉向平均分配，有效反搭便车</li>
            <li><b>AI辅助全流程</b>：DeepSeek驱动智能问答、自动出题批改、学情预警、改进建议生成</li>
            <li><b>CO→PO最小值法</b>：毕业要求达成度=Min{对应课程目标达成度}，确保每个指标点都被覆盖</li>
            <li><b>岗位专项评价</b>：PM/DEV/TEST/DOC四岗位各有独立评价维度和权重</li>
            <li><b>完整数据闭环</b>：问答→行为数据→过程评价→贡献度→达成度→改进建议→教学调整</li>
          </ul>

          <h4>7.3 数据闭环链路</h4>
          <pre style="background:#f5f7fa;padding:12px;border-radius:4px;font-size:13px">
学生AI问答 ──→ 行为数据记录 ──→ 过程性评价
    │                              │
    ├── Git提交记录 ──→ 贡献度计算 ──┤
    ├── 项目日志     ──→              │
    ├── AI自测成绩   ──→              │
    │                              ↓
    └── 知识点热力图 ←── 达成度分析 ←── 个人最终成绩
                              │
                              ↓
                    改进建议 → 教学调整 → 下一轮迭代</pre>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeSections = ref(['1'])

const cdioStages = [
  { phase: 'CONCEIVE', name: '构思', description: '明确项目需求，进行可行性分析，确定技术方案和项目目标', tagType: 'primary' },
  { phase: 'DESIGN', name: '设计', description: '进行系统架构设计、详细设计和原型开发，产出设计文档', tagType: 'success' },
  { phase: 'IMPLEMENT', name: '实施', description: '编码实现、单元测试、集成测试，将设计转化为可运行的产品', tagType: 'warning' },
  { phase: 'OPERATE', name: '运行', description: '部署上线、运行维护、收集反馈、持续改进', tagType: 'danger' }
]

const faqs = [
  {
    q: 'Q1: 如何创建课程目标？',
    a: '登录后进入"课程目标"页面，选择课程后点击"+ 课程目标"按钮，填写目标编号、标题、维度（知识/能力/素养）、权重和描述后保存。'
  },
  {
    q: 'Q2: 学生如何加入小组？',
    a: '学生登录后在"我的小组"页面，输入教师或组长提供的邀请码，点击"加入小组"即可。'
  },
  {
    q: 'Q3: AHP 权重校验不通过怎么办？',
    a: 'AHP 校验 CR 值 >= 0.1 时表示权重一致性不通过。需要重新调整课程目标的权重分配，或检查两两比较矩阵的合理性。可在"达成度分析"页面点击"AHP权重校验"查看详细结果。'
  },
  {
    q: 'Q4: 如何计算达成度？',
    a: '在"达成度分析"页面，选择目标小组后点击"计算达成度"按钮，系统将自动汇总该小组相关的所有评价数据并计算达成度。'
  },
  {
    q: 'Q5: Git 仓库同步失败怎么办？',
    a: '请检查以下几点：(1) Token 是否有效且具有仓库访问权限；(2) 仓库路径（平台/拥有者/仓库名/分支）是否正确；(3) 网络是否可以访问对应的 Git 平台。'
  },
  {
    q: 'Q6: 智能问答可以上传哪些文件？',
    a: '目前支持上传 PDF、Word (.doc/.docx)、文本文件 (.txt) 和图片 (.jpg/.png) 格式的文件。上传后 AI 将结合文件内容进行回答。'
  },
  {
    q: 'Q7: 学生成绩是如何计算的？',
    a: '学生最终成绩 = 小组得分 x 贡献系数 + 个人加分。小组得分由教师通过"多元评价"模块打分。贡献系数综合考虑角色、任务完成度、Git 贡献度等因素。个人加分来自"项目追踪 > 贡献"模块中经教师审核通过的加分项。'
  }
]
</script>

<style scoped>
.docs-page { max-width: 960px; margin: 0 auto; background: #fff; border-radius: 8px; padding: 24px; }
.docs-header { text-align: center; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 2px solid #409eff; }
.docs-header h1 { margin: 0 0 8px; font-size: 22px; color: #303133; }
.docs-subtitle { color: #909399; font-size: 14px; margin: 0; }
.docs-collapse { border: none; }
.docs-content { line-height: 1.8; color: #303133; font-size: 14px; }
.docs-content h4 { margin: 16px 0 8px; color: #409eff; }
.docs-content ul { margin: 8px 0; padding-left: 20px; }
.docs-content li { margin-bottom: 4px; }
.docs-content p { margin: 8px 0; }
.faq-item { margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px dashed #e4e7ed; }
.faq-item:last-child { border-bottom: none; }
.faq-item h4 { color: #303133; font-size: 14px; }
.faq-item p { color: #606266; }
</style>
