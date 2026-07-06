# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: rendering-validation.spec.js >> 前端渲染验证（DOM内容检查） >> RENDER-01: Git提交表所有列均非空
- Location: e2e\rendering-validation.spec.js:52:3

# Error details

```
Error: Git提交-行数不是数字: "未开始"

expect(received).toBe(expected) // Object.is equality

Expected: false
Received: true
```

# Page snapshot

```yaml
- generic [ref=e3]:
  - complementary [ref=e4]:
    - generic [ref=e5]:
      - generic [ref=e6]: 管理端
      - generic [ref=e7]: TestAdmin
      - button "❮❮" [ref=e8] [cursor=pointer]:
        - generic [ref=e10]: ❮❮
    - menubar [ref=e11]:
      - menuitem "课程目标" [ref=e12] [cursor=pointer]:
        - img [ref=e13]
        - generic [ref=e17]: 课程目标
      - menuitem "小组管理" [ref=e18] [cursor=pointer]:
        - img [ref=e19]
        - generic [ref=e21]: 小组管理
      - menuitem "智能问答" [ref=e22] [cursor=pointer]:
        - img [ref=e23]
        - generic [ref=e27]: 智能问答
      - menuitem "项目追踪" [ref=e28] [cursor=pointer]:
        - img [ref=e29]
        - generic [ref=e33]: 项目追踪
      - menuitem "多元评价" [ref=e34] [cursor=pointer]:
        - img [ref=e35]
        - generic [ref=e38]: 多元评价
      - menuitem "达成度分析" [ref=e39] [cursor=pointer]:
        - img [ref=e40]
        - generic [ref=e46]: 达成度分析
      - menuitem "个人中心" [ref=e47] [cursor=pointer]:
        - img [ref=e48]
        - generic [ref=e51]: 个人中心
      - menuitem "使用文档" [ref=e52] [cursor=pointer]:
        - img [ref=e53]
        - generic [ref=e56]: 使用文档
  - generic [ref=e57]:
    - generic [ref=e58]:
      - navigation "el.breadcrumb.label" [ref=e59]:
        - generic [ref=e60]:
          - link "首页" [ref=e61]
          - text: /
        - link "项目追踪" [ref=e63]
      - button "退出登录" [ref=e64] [cursor=pointer]:
        - generic [ref=e65]: 退出登录
    - main [ref=e66]:
      - generic [ref=e67]:
        - generic [ref=e68]:
          - generic [ref=e70] [cursor=pointer]:
            - generic:
              - combobox [ref=e72]
              - generic [ref=e73]: 软件工程第1组
            - img [ref=e76]
          - button "刷新" [ref=e78] [cursor=pointer]:
            - generic [ref=e79]: 刷新
        - generic [ref=e80]:
          - tablist [ref=e84]:
            - tab "里程碑" [ref=e86]
            - tab "任务" [ref=e87]
            - tab "日志" [ref=e88]
            - tab "Git 提交" [active] [selected] [ref=e89] [cursor=pointer]
            - tab "贡献" [ref=e90]
            - tab "Git 仓库" [ref=e91]
          - tabpanel "Git 提交" [ref=e93]:
            - button "+ 添加提交记录" [ref=e95] [cursor=pointer]:
              - generic [ref=e96]: + 添加提交记录
            - generic [ref=e98]:
              - table [ref=e100]:
                - rowgroup [ref=e108]:
                  - row "用户 提交信息 +行数 -行数 时间 操作" [ref=e109]:
                    - columnheader "用户" [ref=e110]:
                      - generic [ref=e111]: 用户
                    - columnheader "提交信息" [ref=e112]:
                      - generic [ref=e113]: 提交信息
                    - columnheader "+行数" [ref=e114]:
                      - generic [ref=e115]: +行数
                    - columnheader "-行数" [ref=e116]:
                      - generic [ref=e117]: "-行数"
                    - columnheader "时间" [ref=e118]:
                      - generic [ref=e119]: 时间
                    - columnheader "操作" [ref=e120]:
                      - generic [ref=e121]: 操作
              - table [ref=e126]:
                - rowgroup [ref=e134]:
                  - 'row "系统管理员 fix: QA会话水平权限校验+达成度崩溃修复+SQL关闭+CourseController修复+29项批量修复完成 4 0 2026/7/3 23:26 编辑 删除" [ref=e135]':
                    - cell "系统管理员" [ref=e136]:
                      - generic [ref=e137]: 系统管理员
                    - 'cell "fix: QA会话水平权限校验+达成度崩溃修复+SQL关闭+CourseController修复+29项批量修复完成" [ref=e138]':
                      - generic [ref=e139]: "fix: QA会话水平权限校验+达成度崩溃修复+SQL关闭+CourseController修复+29项批量修复完成"
                    - cell "4" [ref=e140]:
                      - generic [ref=e141]: "4"
                    - cell "0" [ref=e142]:
                      - generic [ref=e143]: "0"
                    - cell "2026/7/3 23:26" [ref=e144]:
                      - generic [ref=e145]: 2026/7/3 23:26
                    - cell "编辑 删除" [ref=e146]:
                      - generic [ref=e147]:
                        - button "编辑" [ref=e148] [cursor=pointer]:
                          - generic [ref=e149]: 编辑
                        - button "删除" [ref=e150] [cursor=pointer]:
                          - generic [ref=e151]: 删除
                  - 'row "系统管理员 fix: P0致命修复-ClassCast崩溃+package缺失+SQL日志关闭+JWT增强+重复import 4 7 2026/7/3 23:24 编辑 删除" [ref=e152]':
                    - cell "系统管理员" [ref=e153]:
                      - generic [ref=e154]: 系统管理员
                    - 'cell "fix: P0致命修复-ClassCast崩溃+package缺失+SQL日志关闭+JWT增强+重复import" [ref=e155]':
                      - generic [ref=e156]: "fix: P0致命修复-ClassCast崩溃+package缺失+SQL日志关闭+JWT增强+重复import"
                    - cell "4" [ref=e157]:
                      - generic [ref=e158]: "4"
                    - cell "7" [ref=e159]:
                      - generic [ref=e160]: "7"
                    - cell "2026/7/3 23:24" [ref=e161]:
                      - generic [ref=e162]: 2026/7/3 23:24
                    - cell "编辑 删除" [ref=e163]:
                      - generic [ref=e164]:
                        - button "编辑" [ref=e165] [cursor=pointer]:
                          - generic [ref=e166]: 编辑
                        - button "删除" [ref=e167] [cursor=pointer]:
                          - generic [ref=e168]: 删除
                  - 'row "系统管理员 fix: admin创建用户+CourseController去重+MaxKB兜底提示+Security课程保护+evaluatorId修复 20 9 2026/7/3 23:19 编辑 删除" [ref=e169]':
                    - cell "系统管理员" [ref=e170]:
                      - generic [ref=e171]: 系统管理员
                    - 'cell "fix: admin创建用户+CourseController去重+MaxKB兜底提示+Security课程保护+evaluatorId修复" [ref=e172]':
                      - generic [ref=e173]: "fix: admin创建用户+CourseController去重+MaxKB兜底提示+Security课程保护+evaluatorId修复"
                    - cell "20" [ref=e174]:
                      - generic [ref=e175]: "20"
                    - cell "9" [ref=e176]:
                      - generic [ref=e177]: "9"
                    - cell "2026/7/3 23:19" [ref=e178]:
                      - generic [ref=e179]: 2026/7/3 23:19
                    - cell "编辑 删除" [ref=e180]:
                      - generic [ref=e181]:
                        - button "编辑" [ref=e182] [cursor=pointer]:
                          - generic [ref=e183]: 编辑
                        - button "删除" [ref=e184] [cursor=pointer]:
                          - generic [ref=e185]: 删除
                  - 'row "系统管理员 fix: evaluatorId从SecurityContext读取+ACHIEVEment courseId解析+模板展开 7 1 2026/7/3 23:16 编辑 删除" [ref=e186]':
                    - cell "系统管理员" [ref=e187]:
                      - generic [ref=e188]: 系统管理员
                    - 'cell "fix: evaluatorId从SecurityContext读取+ACHIEVEment courseId解析+模板展开" [ref=e189]':
                      - generic [ref=e190]: "fix: evaluatorId从SecurityContext读取+ACHIEVEment courseId解析+模板展开"
                    - cell "7" [ref=e191]:
                      - generic [ref=e192]: "7"
                    - cell "1" [ref=e193]:
                      - generic [ref=e194]: "1"
                    - cell "2026/7/3 23:16" [ref=e195]:
                      - generic [ref=e196]: 2026/7/3 23:16
                    - cell "编辑 删除" [ref=e197]:
                      - generic [ref=e198]:
                        - button "编辑" [ref=e199] [cursor=pointer]:
                          - generic [ref=e200]: 编辑
                        - button "删除" [ref=e201] [cursor=pointer]:
                          - generic [ref=e202]: 删除
                  - 'row "系统管理员 feat: 全部模板展开+scoped CSS-4页面独立样式表+比赛级完美交付 200 15 2026/7/3 23:11 编辑 删除" [ref=e203]':
                    - cell "系统管理员" [ref=e204]:
                      - generic [ref=e205]: 系统管理员
                    - 'cell "feat: 全部模板展开+scoped CSS-4页面独立样式表+比赛级完美交付" [ref=e206]':
                      - generic [ref=e207]: "feat: 全部模板展开+scoped CSS-4页面独立样式表+比赛级完美交付"
                    - cell "200" [ref=e208]:
                      - generic [ref=e209]: "200"
                    - cell "15" [ref=e210]:
                      - generic [ref=e211]: "15"
                    - cell "2026/7/3 23:11" [ref=e212]:
                      - generic [ref=e213]: 2026/7/3 23:11
                    - cell "编辑 删除" [ref=e214]:
                      - generic [ref=e215]:
                        - button "编辑" [ref=e216] [cursor=pointer]:
                          - generic [ref=e217]: 编辑
                        - button "删除" [ref=e218] [cursor=pointer]:
                          - generic [ref=e219]: 删除
                  - 'row "系统管理员 feat: 模板展开-group和course页面+scoped CSS+全量修复完成 247 9 2026/7/3 23:09 编辑 删除" [ref=e220]':
                    - cell "系统管理员" [ref=e221]:
                      - generic [ref=e222]: 系统管理员
                    - 'cell "feat: 模板展开-group和course页面+scoped CSS+全量修复完成" [ref=e223]':
                      - generic [ref=e224]: "feat: 模板展开-group和course页面+scoped CSS+全量修复完成"
                    - cell "247" [ref=e225]:
                      - generic [ref=e226]: "247"
                    - cell "9" [ref=e227]:
                      - generic [ref=e228]: "9"
                    - cell "2026/7/3 23:09" [ref=e229]:
                      - generic [ref=e230]: 2026/7/3 23:09
                    - cell "编辑 删除" [ref=e231]:
                      - generic [ref=e232]:
                        - button "编辑" [ref=e233] [cursor=pointer]:
                          - generic [ref=e234]: 编辑
                        - button "删除" [ref=e235] [cursor=pointer]:
                          - generic [ref=e236]: 删除
                  - 'row "系统管理员 feat: 比赛完善-Pinia auth store+CSS tokens+文件校验+限流+改进建议指示器+课程树过滤 39 0 2026/7/3 23:07 编辑 删除" [ref=e237]':
                    - cell "系统管理员" [ref=e238]:
                      - generic [ref=e239]: 系统管理员
                    - 'cell "feat: 比赛完善-Pinia auth store+CSS tokens+文件校验+限流+改进建议指示器+课程树过滤" [ref=e240]':
                      - generic [ref=e241]: "feat: 比赛完善-Pinia auth store+CSS tokens+文件校验+限流+改进建议指示器+课程树过滤"
                    - cell "39" [ref=e242]:
                      - generic [ref=e243]: "39"
                    - cell "0" [ref=e244]:
                      - generic [ref=e245]: "0"
                    - cell "2026/7/3 23:07" [ref=e246]:
                      - generic [ref=e247]: 2026/7/3 23:07
                    - cell "编辑 删除" [ref=e248]:
                      - generic [ref=e249]:
                        - button "编辑" [ref=e250] [cursor=pointer]:
                          - generic [ref=e251]: 编辑
                        - button "删除" [ref=e252] [cursor=pointer]:
                          - generic [ref=e253]: 删除
                  - 'row "系统管理员 fix: 课程按courseId过滤树+改进建议设indicatorId+RoleEvaluation接入达成度 7 5 2026/7/3 22:57 编辑 删除" [ref=e254]':
                    - cell "系统管理员" [ref=e255]:
                      - generic [ref=e256]: 系统管理员
                    - 'cell "fix: 课程按courseId过滤树+改进建议设indicatorId+RoleEvaluation接入达成度" [ref=e257]':
                      - generic [ref=e258]: "fix: 课程按courseId过滤树+改进建议设indicatorId+RoleEvaluation接入达成度"
                    - cell "7" [ref=e259]:
                      - generic [ref=e260]: "7"
                    - cell "5" [ref=e261]:
                      - generic [ref=e262]: "5"
                    - cell "2026/7/3 22:57" [ref=e263]:
                      - generic [ref=e264]: 2026/7/3 22:57
                    - cell "编辑 删除" [ref=e265]:
                      - generic [ref=e266]:
                        - button "编辑" [ref=e267] [cursor=pointer]:
                          - generic [ref=e268]: 编辑
                        - button "删除" [ref=e269] [cursor=pointer]:
                          - generic [ref=e270]: 删除
                  - 'row "系统管理员 fix: RoleEvaluation接入达成度计算+课程选择器+Git跳过未匹配+学生项目访问 9 0 2026/7/3 22:52 编辑 删除" [ref=e271]':
                    - cell "系统管理员" [ref=e272]:
                      - generic [ref=e273]: 系统管理员
                    - 'cell "fix: RoleEvaluation接入达成度计算+课程选择器+Git跳过未匹配+学生项目访问" [ref=e274]':
                      - generic [ref=e275]: "fix: RoleEvaluation接入达成度计算+课程选择器+Git跳过未匹配+学生项目访问"
                    - cell "9" [ref=e276]:
                      - generic [ref=e277]: "9"
                    - cell "0" [ref=e278]:
                      - generic [ref=e279]: "0"
                    - cell "2026/7/3 22:52" [ref=e280]:
                      - generic [ref=e281]: 2026/7/3 22:52
                    - cell "编辑 删除" [ref=e282]:
                      - generic [ref=e283]:
                        - button "编辑" [ref=e284] [cursor=pointer]:
                          - generic [ref=e285]: 编辑
                        - button "删除" [ref=e286] [cursor=pointer]:
                          - generic [ref=e287]: 删除
                  - 'row "系统管理员 fix: 学生可访问项目追踪+Layout菜单添加项目追踪+Git未匹配跳过 2 1 2026/7/3 22:43 编辑 删除" [ref=e288]':
                    - cell "系统管理员" [ref=e289]:
                      - generic [ref=e290]: 系统管理员
                    - 'cell "fix: 学生可访问项目追踪+Layout菜单添加项目追踪+Git未匹配跳过" [ref=e291]':
                      - generic [ref=e292]: "fix: 学生可访问项目追踪+Layout菜单添加项目追踪+Git未匹配跳过"
                    - cell "2" [ref=e293]:
                      - generic [ref=e294]: "2"
                    - cell "1" [ref=e295]:
                      - generic [ref=e296]: "1"
                    - cell "2026/7/3 22:43" [ref=e297]:
                      - generic [ref=e298]: 2026/7/3 22:43
                    - cell "编辑 删除" [ref=e299]:
                      - generic [ref=e300]:
                        - button "编辑" [ref=e301] [cursor=pointer]:
                          - generic [ref=e302]: 编辑
                        - button "删除" [ref=e303] [cursor=pointer]:
                          - generic [ref=e304]: 删除
                  - 'row "系统管理员 fix: Git未匹配提交跳过而非归到admin+GitHub详情行数+改进建议API+里程碑更新 11 81 2026/7/3 22:41 编辑 删除" [ref=e305]':
                    - cell "系统管理员" [ref=e306]:
                      - generic [ref=e307]: 系统管理员
                    - 'cell "fix: Git未匹配提交跳过而非归到admin+GitHub详情行数+改进建议API+里程碑更新" [ref=e308]':
                      - generic [ref=e309]: "fix: Git未匹配提交跳过而非归到admin+GitHub详情行数+改进建议API+里程碑更新"
                    - cell "11" [ref=e310]:
                      - generic [ref=e311]: "11"
                    - cell "81" [ref=e312]:
                      - generic [ref=e313]: "81"
                    - cell "2026/7/3 22:41" [ref=e314]:
                      - generic [ref=e315]: 2026/7/3 22:41
                    - cell "编辑 删除" [ref=e316]:
                      - generic [ref=e317]:
                        - button "编辑" [ref=e318] [cursor=pointer]:
                          - generic [ref=e319]: 编辑
                        - button "删除" [ref=e320] [cursor=pointer]:
                          - generic [ref=e321]: 删除
                  - 'row "系统管理员 fix: CdioPhaseMapping+改进建议API+里程碑更新+贡献审批+AI回复FK+删除端点 11 1 2026/7/3 22:35 编辑 删除" [ref=e322]':
                    - cell "系统管理员" [ref=e323]:
                      - generic [ref=e324]: 系统管理员
                    - 'cell "fix: CdioPhaseMapping+改进建议API+里程碑更新+贡献审批+AI回复FK+删除端点" [ref=e325]':
                      - generic [ref=e326]: "fix: CdioPhaseMapping+改进建议API+里程碑更新+贡献审批+AI回复FK+删除端点"
                    - cell "11" [ref=e327]:
                      - generic [ref=e328]: "11"
                    - cell "1" [ref=e329]:
                      - generic [ref=e330]: "1"
                    - cell "2026/7/3 22:35" [ref=e331]:
                      - generic [ref=e332]: 2026/7/3 22:35
                    - cell "编辑 删除" [ref=e333]:
                      - generic [ref=e334]:
                        - button "编辑" [ref=e335] [cursor=pointer]:
                          - generic [ref=e336]: 编辑
                        - button "删除" [ref=e337] [cursor=pointer]:
                          - generic [ref=e338]: 删除
                  - 'row "系统管理员 fix: AI回复FK修复+贡献审批+删除端点+课程隔离+知识库实体 13 2 2026/7/3 22:33 编辑 删除" [ref=e339]':
                    - cell "系统管理员" [ref=e340]:
                      - generic [ref=e341]: 系统管理员
                    - 'cell "fix: AI回复FK修复+贡献审批+删除端点+课程隔离+知识库实体" [ref=e342]':
                      - generic [ref=e343]: "fix: AI回复FK修复+贡献审批+删除端点+课程隔离+知识库实体"
                    - cell "13" [ref=e344]:
                      - generic [ref=e345]: "13"
                    - cell "2" [ref=e346]:
                      - generic [ref=e347]: "2"
                    - cell "2026/7/3 22:33" [ref=e348]:
                      - generic [ref=e349]: 2026/7/3 22:33
                    - cell "编辑 删除" [ref=e350]:
                      - generic [ref=e351]:
                        - button "编辑" [ref=e352] [cursor=pointer]:
                          - generic [ref=e353]: 编辑
                        - button "删除" [ref=e354] [cursor=pointer]:
                          - generic [ref=e355]: 删除
                  - 'row "系统管理员 fix: TokenRedis+Audit登录+calculatedAt+deadcode清理+GitHub详情+IDOR 26 8 2026/7/3 22:28 编辑 删除" [ref=e356]':
                    - cell "系统管理员" [ref=e357]:
                      - generic [ref=e358]: 系统管理员
                    - 'cell "fix: TokenRedis+Audit登录+calculatedAt+deadcode清理+GitHub详情+IDOR" [ref=e359]':
                      - generic [ref=e360]: "fix: TokenRedis+Audit登录+calculatedAt+deadcode清理+GitHub详情+IDOR"
                    - cell "26" [ref=e361]:
                      - generic [ref=e362]: "26"
                    - cell "8" [ref=e363]:
                      - generic [ref=e364]: "8"
                    - cell "2026/7/3 22:28" [ref=e365]:
                      - generic [ref=e366]: 2026/7/3 22:28
                    - cell "编辑 删除" [ref=e367]:
                      - generic [ref=e368]:
                        - button "编辑" [ref=e369] [cursor=pointer]:
                          - generic [ref=e370]: 编辑
                        - button "删除" [ref=e371] [cursor=pointer]:
                          - generic [ref=e372]: 删除
                  - 'row "系统管理员 fix: GitHub同步获取每提交详细行数+IDOR完整修复+calcRound版本追踪 15 1 2026/7/3 22:17 编辑 删除" [ref=e373]':
                    - cell "系统管理员" [ref=e374]:
                      - generic [ref=e375]: 系统管理员
                    - 'cell "fix: GitHub同步获取每提交详细行数+IDOR完整修复+calcRound版本追踪" [ref=e376]':
                      - generic [ref=e377]: "fix: GitHub同步获取每提交详细行数+IDOR完整修复+calcRound版本追踪"
                    - cell "15" [ref=e378]:
                      - generic [ref=e379]: "15"
                    - cell "1" [ref=e380]:
                      - generic [ref=e381]: "1"
                    - cell "2026/7/3 22:17" [ref=e382]:
                      - generic [ref=e383]: 2026/7/3 22:17
                    - cell "编辑 删除" [ref=e384]:
                      - generic [ref=e385]:
                        - button "编辑" [ref=e386] [cursor=pointer]:
                          - generic [ref=e387]: 编辑
                        - button "删除" [ref=e388] [cursor=pointer]:
                          - generic [ref=e389]: 删除
                  - 'row "系统管理员 fix: P0 IDOR修复(join+QA从SecurityContext读userId)+calcRound保存+GitHub默认ID修复 5 3 2026/7/3 22:15 编辑 删除" [ref=e390]':
                    - cell "系统管理员" [ref=e391]:
                      - generic [ref=e392]: 系统管理员
                    - 'cell "fix: P0 IDOR修复(join+QA从SecurityContext读userId)+calcRound保存+GitHub默认ID修复" [ref=e393]':
                      - generic [ref=e394]: "fix: P0 IDOR修复(join+QA从SecurityContext读userId)+calcRound保存+GitHub默认ID修复"
                    - cell "5" [ref=e395]:
                      - generic [ref=e396]: "5"
                    - cell "3" [ref=e397]:
                      - generic [ref=e398]: "3"
                    - cell "2026/7/3 22:15" [ref=e399]:
                      - generic [ref=e400]: 2026/7/3 22:15
                    - cell "编辑 删除" [ref=e401]:
                      - generic [ref=e402]:
                        - button "编辑" [ref=e403] [cursor=pointer]:
                          - generic [ref=e404]: 编辑
                        - button "删除" [ref=e405] [cursor=pointer]:
                          - generic [ref=e406]: 删除
                  - 'row "系统管理员 fix: Settings密码确认校验+401hash路由+API拦截器优化 3 2 2026/7/3 22:10 编辑 删除" [ref=e407]':
                    - cell "系统管理员" [ref=e408]:
                      - generic [ref=e409]: 系统管理员
                    - 'cell "fix: Settings密码确认校验+401hash路由+API拦截器优化" [ref=e410]':
                      - generic [ref=e411]: "fix: Settings密码确认校验+401hash路由+API拦截器优化"
                    - cell "3" [ref=e412]:
                      - generic [ref=e413]: "3"
                    - cell "2" [ref=e414]:
                      - generic [ref=e415]: "2"
                    - cell "2026/7/3 22:10" [ref=e416]:
                      - generic [ref=e417]: 2026/7/3 22:10
                    - cell "编辑 删除" [ref=e418]:
                      - generic [ref=e419]:
                        - button "编辑" [ref=e420] [cursor=pointer]:
                          - generic [ref=e421]: 编辑
                        - button "删除" [ref=e422] [cursor=pointer]:
                          - generic [ref=e423]: 删除
                  - 'row "系统管理员 fix: Settings确认密码输入框+贡献度审批+课程删除确认弹窗 1 0 2026/7/3 22:09 编辑 删除" [ref=e424]':
                    - cell "系统管理员" [ref=e425]:
                      - generic [ref=e426]: 系统管理员
                    - 'cell "fix: Settings确认密码输入框+贡献度审批+课程删除确认弹窗" [ref=e427]':
                      - generic [ref=e428]: "fix: Settings确认密码输入框+贡献度审批+课程删除确认弹窗"
                    - cell "1" [ref=e429]:
                      - generic [ref=e430]: "1"
                    - cell "0" [ref=e431]:
                      - generic [ref=e432]: "0"
                    - cell "2026/7/3 22:09" [ref=e433]:
                      - generic [ref=e434]: 2026/7/3 22:09
                    - cell "编辑 删除" [ref=e435]:
                      - generic [ref=e436]:
                        - button "编辑" [ref=e437] [cursor=pointer]:
                          - generic [ref=e438]: 编辑
                        - button "删除" [ref=e439] [cursor=pointer]:
                          - generic [ref=e440]: 删除
                  - 'row "系统管理员 fix: 课程删除确认弹窗+贡献度审批字段+评分只计已审批 5 3 2026/7/3 22:08 编辑 删除" [ref=e441]':
                    - cell "系统管理员" [ref=e442]:
                      - generic [ref=e443]: 系统管理员
                    - 'cell "fix: 课程删除确认弹窗+贡献度审批字段+评分只计已审批" [ref=e444]':
                      - generic [ref=e445]: "fix: 课程删除确认弹窗+贡献度审批字段+评分只计已审批"
                    - cell "5" [ref=e446]:
                      - generic [ref=e447]: "5"
                    - cell "3" [ref=e448]:
                      - generic [ref=e449]: "3"
                    - cell "2026/7/3 22:08" [ref=e450]:
                      - generic [ref=e451]: 2026/7/3 22:08
                    - cell "编辑 删除" [ref=e452]:
                      - generic [ref=e453]:
                        - button "编辑" [ref=e454] [cursor=pointer]:
                          - generic [ref=e455]: 编辑
                        - button "删除" [ref=e456] [cursor=pointer]:
                          - generic [ref=e457]: 删除
                  - 'row "系统管理员 fix: 课程树删除加确认弹窗ElMessageBox+Settings密码确认+QA Enter键Ctrl+Enter 1 1 2026/7/3 22:03 编辑 删除" [ref=e458]':
                    - cell "系统管理员" [ref=e459]:
                      - generic [ref=e460]: 系统管理员
                    - 'cell "fix: 课程树删除加确认弹窗ElMessageBox+Settings密码确认+QA Enter键Ctrl+Enter" [ref=e461]':
                      - generic [ref=e462]: "fix: 课程树删除加确认弹窗ElMessageBox+Settings密码确认+QA Enter键Ctrl+Enter"
                    - cell "1" [ref=e463]:
                      - generic [ref=e464]: "1"
                    - cell "1" [ref=e465]:
                      - generic [ref=e466]: "1"
                    - cell "2026/7/3 22:03" [ref=e467]:
                      - generic [ref=e468]: 2026/7/3 22:03
                    - cell "编辑 删除" [ref=e469]:
                      - generic [ref=e470]:
                        - button "编辑" [ref=e471] [cursor=pointer]:
                          - generic [ref=e472]: 编辑
                        - button "删除" [ref=e473] [cursor=pointer]:
                          - generic [ref=e474]: 删除
                  - 'row "系统管理员 fix: QA Enter键改Ctrl+Enter发送+Settings密码确认字段+GroupService真实课程名 2 2 2026/7/3 22:01 编辑 删除" [ref=e475]':
                    - cell "系统管理员" [ref=e476]:
                      - generic [ref=e477]: 系统管理员
                    - 'cell "fix: QA Enter键改Ctrl+Enter发送+Settings密码确认字段+GroupService真实课程名" [ref=e478]':
                      - generic [ref=e479]: "fix: QA Enter键改Ctrl+Enter发送+Settings密码确认字段+GroupService真实课程名"
                    - cell "2" [ref=e480]:
                      - generic [ref=e481]: "2"
                    - cell "2" [ref=e482]:
                      - generic [ref=e483]: "2"
                    - cell "2026/7/3 22:01" [ref=e484]:
                      - generic [ref=e485]: 2026/7/3 22:01
                    - cell "编辑 删除" [ref=e486]:
                      - generic [ref=e487]:
                        - button "编辑" [ref=e488] [cursor=pointer]:
                          - generic [ref=e489]: 编辑
                        - button "删除" [ref=e490] [cursor=pointer]:
                          - generic [ref=e491]: 删除
                  - 'row "系统管理员 fix: P0批量修复-CourseMapper+GroupService真实课程名+SecurityConfig+评估默认值 11 5 2026/7/3 21:55 编辑 删除" [ref=e492]':
                    - cell "系统管理员" [ref=e493]:
                      - generic [ref=e494]: 系统管理员
                    - 'cell "fix: P0批量修复-CourseMapper+GroupService真实课程名+SecurityConfig+评估默认值" [ref=e495]':
                      - generic [ref=e496]: "fix: P0批量修复-CourseMapper+GroupService真实课程名+SecurityConfig+评估默认值"
                    - cell "11" [ref=e497]:
                      - generic [ref=e498]: "11"
                    - cell "5" [ref=e499]:
                      - generic [ref=e500]: "5"
                    - cell "2026/7/3 21:55" [ref=e501]:
                      - generic [ref=e502]: 2026/7/3 21:55
                    - cell "编辑 删除" [ref=e503]:
                      - generic [ref=e504]:
                        - button "编辑" [ref=e505] [cursor=pointer]:
                          - generic [ref=e506]: 编辑
                        - button "删除" [ref=e507] [cursor=pointer]:
                          - generic [ref=e508]: 删除
                  - 'row "系统管理员 fix: SecurityConfig Users pattern */disable修复+双审计报告完成 1 1 2026/7/3 21:53 编辑 删除" [ref=e509]':
                    - cell "系统管理员" [ref=e510]:
                      - generic [ref=e511]: 系统管理员
                    - 'cell "fix: SecurityConfig Users pattern */disable修复+双审计报告完成" [ref=e512]':
                      - generic [ref=e513]: "fix: SecurityConfig Users pattern */disable修复+双审计报告完成"
                    - cell "1" [ref=e514]:
                      - generic [ref=e515]: "1"
                    - cell "1" [ref=e516]:
                      - generic [ref=e517]: "1"
                    - cell "2026/7/3 21:53" [ref=e518]:
                      - generic [ref=e519]: 2026/7/3 21:53
                    - cell "编辑 删除" [ref=e520]:
                      - generic [ref=e521]:
                        - button "编辑" [ref=e522] [cursor=pointer]:
                          - generic [ref=e523]: 编辑
                        - button "删除" [ref=e524] [cursor=pointer]:
                          - generic [ref=e525]: 删除
                  - 'row "系统管理员 feat: 全部课程关联-course_objective加course_id+CourseController+3门课程+cdio映射加course_id 30 5 2026/7/3 21:41 编辑 删除" [ref=e526]':
                    - cell "系统管理员" [ref=e527]:
                      - generic [ref=e528]: 系统管理员
                    - 'cell "feat: 全部课程关联-course_objective加course_id+CourseController+3门课程+cdio映射加course_id" [ref=e529]':
                      - generic [ref=e530]: "feat: 全部课程关联-course_objective加course_id+CourseController+3门课程+cdio映射加course_id"
                    - cell "30" [ref=e531]:
                      - generic [ref=e532]: "30"
                    - cell "5" [ref=e533]:
                      - generic [ref=e534]: "5"
                    - cell "2026/7/3 21:41" [ref=e535]:
                      - generic [ref=e536]: 2026/7/3 21:41
                    - cell "编辑 删除" [ref=e537]:
                      - generic [ref=e538]:
                        - button "编辑" [ref=e539] [cursor=pointer]:
                          - generic [ref=e540]: 编辑
                        - button "删除" [ref=e541] [cursor=pointer]:
                          - generic [ref=e542]: 删除
                  - 'row "系统管理员 feat: 课程-小组关联-course表+project_group.course_id+UI显示课程名 23 2 2026/7/3 21:39 编辑 删除" [ref=e543]':
                    - cell "系统管理员" [ref=e544]:
                      - generic [ref=e545]: 系统管理员
                    - 'cell "feat: 课程-小组关联-course表+project_group.course_id+UI显示课程名" [ref=e546]':
                      - generic [ref=e547]: "feat: 课程-小组关联-course表+project_group.course_id+UI显示课程名"
                    - cell "23" [ref=e548]:
                      - generic [ref=e549]: "23"
                    - cell "2" [ref=e550]:
                      - generic [ref=e551]: "2"
                    - cell "2026/7/3 21:39" [ref=e552]:
                      - generic [ref=e553]: 2026/7/3 21:39
                    - cell "编辑 删除" [ref=e554]:
                      - generic [ref=e555]:
                        - button "编辑" [ref=e556] [cursor=pointer]:
                          - generic [ref=e557]: 编辑
                        - button "删除" [ref=e558] [cursor=pointer]:
                          - generic [ref=e559]: 删除
                  - 'row "系统管理员 feat: 阿里图标替换为内联SVG组件-零外部依赖+Layout瘦身+图标统一 56 52 2026/7/3 21:30 编辑 删除" [ref=e560]':
                    - cell "系统管理员" [ref=e561]:
                      - generic [ref=e562]: 系统管理员
                    - 'cell "feat: 阿里图标替换为内联SVG组件-零外部依赖+Layout瘦身+图标统一" [ref=e563]':
                      - generic [ref=e564]: "feat: 阿里图标替换为内联SVG组件-零外部依赖+Layout瘦身+图标统一"
                    - cell "56" [ref=e565]:
                      - generic [ref=e566]: "56"
                    - cell "52" [ref=e567]:
                      - generic [ref=e568]: "52"
                    - cell "2026/7/3 21:30" [ref=e569]:
                      - generic [ref=e570]: 2026/7/3 21:30
                    - cell "编辑 删除" [ref=e571]:
                      - generic [ref=e572]:
                        - button "编辑" [ref=e573] [cursor=pointer]:
                          - generic [ref=e574]: 编辑
                        - button "删除" [ref=e575] [cursor=pointer]:
                          - generic [ref=e576]: 删除
                  - 'row "系统管理员 refactor: Layout scoped CSS+analysis loading state+全面规范修复汇总 9 0 2026/7/3 21:27 编辑 删除" [ref=e577]':
                    - cell "系统管理员" [ref=e578]:
                      - generic [ref=e579]: 系统管理员
                    - 'cell "refactor: Layout scoped CSS+analysis loading state+全面规范修复汇总" [ref=e580]':
                      - generic [ref=e581]: "refactor: Layout scoped CSS+analysis loading state+全面规范修复汇总"
                    - cell "9" [ref=e582]:
                      - generic [ref=e583]: "9"
                    - cell "0" [ref=e584]:
                      - generic [ref=e585]: "0"
                    - cell "2026/7/3 21:27" [ref=e586]:
                      - generic [ref=e587]: 2026/7/3 21:27
                    - cell "编辑 删除" [ref=e588]:
                      - generic [ref=e589]:
                        - button "编辑" [ref=e590] [cursor=pointer]:
                          - generic [ref=e591]: 编辑
                        - button "删除" [ref=e592] [cursor=pointer]:
                          - generic [ref=e593]: 删除
                  - 'row "系统管理员 refactor: 前端规范修复-移除HelloWorld+图标按需加载+API环境变量+404路由+角色守卫 16 109 2026/7/3 21:23 编辑 删除" [ref=e594]':
                    - cell "系统管理员" [ref=e595]:
                      - generic [ref=e596]: 系统管理员
                    - 'cell "refactor: 前端规范修复-移除HelloWorld+图标按需加载+API环境变量+404路由+角色守卫" [ref=e597]':
                      - generic [ref=e598]: "refactor: 前端规范修复-移除HelloWorld+图标按需加载+API环境变量+404路由+角色守卫"
                    - cell "16" [ref=e599]:
                      - generic [ref=e600]: "16"
                    - cell "109" [ref=e601]:
                      - generic [ref=e602]: "109"
                    - cell "2026/7/3 21:23" [ref=e603]:
                      - generic [ref=e604]: 2026/7/3 21:23
                    - cell "编辑 删除" [ref=e605]:
                      - generic [ref=e606]:
                        - button "编辑" [ref=e607] [cursor=pointer]:
                          - generic [ref=e608]: 编辑
                        - button "删除" [ref=e609] [cursor=pointer]:
                          - generic [ref=e610]: 删除
                  - 'row "系统管理员 fix: P0安全修复-XSS移除v-html+登录页移除硬编码凭证+缺省凭据移除 4 49 2026/7/3 21:19 编辑 删除" [ref=e611]':
                    - cell "系统管理员" [ref=e612]:
                      - generic [ref=e613]: 系统管理员
                    - 'cell "fix: P0安全修复-XSS移除v-html+登录页移除硬编码凭证+缺省凭据移除" [ref=e614]':
                      - generic [ref=e615]: "fix: P0安全修复-XSS移除v-html+登录页移除硬编码凭证+缺省凭据移除"
                    - cell "4" [ref=e616]:
                      - generic [ref=e617]: "4"
                    - cell "49" [ref=e618]:
                      - generic [ref=e619]: "49"
                    - cell "2026/7/3 21:19" [ref=e620]:
                      - generic [ref=e621]: 2026/7/3 21:19
                    - cell "编辑 删除" [ref=e622]:
                      - generic [ref=e623]:
                        - button "编辑" [ref=e624] [cursor=pointer]:
                          - generic [ref=e625]: 编辑
                        - button "删除" [ref=e626] [cursor=pointer]:
                          - generic [ref=e627]: 删除
                  - 'row "系统管理员 fix: QA页管理端加学生选择器-管理员/教师可查看任意学生对话 3 141 2026/7/3 21:15 编辑 删除" [ref=e628]':
                    - cell "系统管理员" [ref=e629]:
                      - generic [ref=e630]: 系统管理员
                    - 'cell "fix: QA页管理端加学生选择器-管理员/教师可查看任意学生对话" [ref=e631]':
                      - generic [ref=e632]: "fix: QA页管理端加学生选择器-管理员/教师可查看任意学生对话"
                    - cell "3" [ref=e633]:
                      - generic [ref=e634]: "3"
                    - cell "141" [ref=e635]:
                      - generic [ref=e636]: "141"
                    - cell "2026/7/3 21:15" [ref=e637]:
                      - generic [ref=e638]: 2026/7/3 21:15
                    - cell "编辑 删除" [ref=e639]:
                      - generic [ref=e640]:
                        - button "编辑" [ref=e641] [cursor=pointer]:
                          - generic [ref=e642]: 编辑
                        - button "删除" [ref=e643] [cursor=pointer]:
                          - generic [ref=e644]: 删除
                  - 'row "系统管理员 feat: 小组管理完善-用户下拉选择+每人可移除+确认弹窗+教师ID自动+空状态提示 2 1 2026/7/3 21:11 编辑 删除" [ref=e645]':
                    - cell "系统管理员" [ref=e646]:
                      - generic [ref=e647]: 系统管理员
                    - 'cell "feat: 小组管理完善-用户下拉选择+每人可移除+确认弹窗+教师ID自动+空状态提示" [ref=e648]':
                      - generic [ref=e649]: "feat: 小组管理完善-用户下拉选择+每人可移除+确认弹窗+教师ID自动+空状态提示"
                    - cell "2" [ref=e650]:
                      - generic [ref=e651]: "2"
                    - cell "1" [ref=e652]:
                      - generic [ref=e653]: "1"
                    - cell "2026/7/3 21:11" [ref=e654]:
                      - generic [ref=e655]: 2026/7/3 21:11
                    - cell "编辑 删除" [ref=e656]:
                      - generic [ref=e657]:
                        - button "编辑" [ref=e658] [cursor=pointer]:
                          - generic [ref=e659]: 编辑
                        - button "删除" [ref=e660] [cursor=pointer]:
                          - generic [ref=e661]: 删除
                  - 'row "系统管理员 fix: Git提交按组过滤-groupId参数+后端接口+前端适配 5 3 2026/7/3 21:06 编辑 删除" [ref=e662]':
                    - cell "系统管理员" [ref=e663]:
                      - generic [ref=e664]: 系统管理员
                    - 'cell "fix: Git提交按组过滤-groupId参数+后端接口+前端适配" [ref=e665]':
                      - generic [ref=e666]: "fix: Git提交按组过滤-groupId参数+后端接口+前端适配"
                    - cell "5" [ref=e667]:
                      - generic [ref=e668]: "5"
                    - cell "3" [ref=e669]:
                      - generic [ref=e670]: "3"
                    - cell "2026/7/3 21:06" [ref=e671]:
                      - generic [ref=e672]: 2026/7/3 21:06
                    - cell "编辑 删除" [ref=e673]:
                      - generic [ref=e674]:
                        - button "编辑" [ref=e675] [cursor=pointer]:
                          - generic [ref=e676]: 编辑
                        - button "删除" [ref=e677] [cursor=pointer]:
                          - generic [ref=e678]: 删除
                  - 'row "系统管理员 fix: Git仓库配置显示小组名+后端返回groupName+前端表格加小组列 23 24 2026/7/3 21:00 编辑 删除" [ref=e679]':
                    - cell "系统管理员" [ref=e680]:
                      - generic [ref=e681]: 系统管理员
                    - 'cell "fix: Git仓库配置显示小组名+后端返回groupName+前端表格加小组列" [ref=e682]':
                      - generic [ref=e683]: "fix: Git仓库配置显示小组名+后端返回groupName+前端表格加小组列"
                    - cell "23" [ref=e684]:
                      - generic [ref=e685]: "23"
                    - cell "24" [ref=e686]:
                      - generic [ref=e687]: "24"
                    - cell "2026/7/3 21:00" [ref=e688]:
                      - generic [ref=e689]: 2026/7/3 21:00
                    - cell "编辑 删除" [ref=e690]:
                      - generic [ref=e691]:
                        - button "编辑" [ref=e692] [cursor=pointer]:
                          - generic [ref=e693]: 编辑
                        - button "删除" [ref=e694] [cursor=pointer]:
                          - generic [ref=e695]: 删除
                  - 'row "系统管理员 docs: 使用文档更新-Git自动同步完整流程(学生绑定+教师配置Token+组内匹配) 18 1 2026/7/3 20:55 编辑 删除" [ref=e696]':
                    - cell "系统管理员" [ref=e697]:
                      - generic [ref=e698]: 系统管理员
                    - 'cell "docs: 使用文档更新-Git自动同步完整流程(学生绑定+教师配置Token+组内匹配)" [ref=e699]':
                      - generic [ref=e700]: "docs: 使用文档更新-Git自动同步完整流程(学生绑定+教师配置Token+组内匹配)"
                    - cell "18" [ref=e701]:
                      - generic [ref=e702]: "18"
                    - cell "1" [ref=e703]:
                      - generic [ref=e704]: "1"
                    - cell "2026/7/3 20:55" [ref=e705]:
                      - generic [ref=e706]: 2026/7/3 20:55
                    - cell "编辑 删除" [ref=e707]:
                      - generic [ref=e708]:
                        - button "编辑" [ref=e709] [cursor=pointer]:
                          - generic [ref=e710]: 编辑
                        - button "删除" [ref=e711] [cursor=pointer]:
                          - generic [ref=e712]: 删除
                  - 'row "系统管理员 fix: Git同步仅匹配小组成员+Fine-grained token支持+教师默认绑定 44 68 2026/7/3 20:53 编辑 删除" [ref=e713]':
                    - cell "系统管理员" [ref=e714]:
                      - generic [ref=e715]: 系统管理员
                    - 'cell "fix: Git同步仅匹配小组成员+Fine-grained token支持+教师默认绑定" [ref=e716]':
                      - generic [ref=e717]: "fix: Git同步仅匹配小组成员+Fine-grained token支持+教师默认绑定"
                    - cell "44" [ref=e718]:
                      - generic [ref=e719]: "44"
                    - cell "68" [ref=e720]:
                      - generic [ref=e721]: "68"
                    - cell "2026/7/3 20:53" [ref=e722]:
                      - generic [ref=e723]: 2026/7/3 20:53
                    - cell "编辑 删除" [ref=e724]:
                      - generic [ref=e725]:
                        - button "编辑" [ref=e726] [cursor=pointer]:
                          - generic [ref=e727]: 编辑
                        - button "删除" [ref=e728] [cursor=pointer]:
                          - generic [ref=e729]: 删除
                  - 'row "系统管理员 fix: Git绑定API改为/auth/git+前端适配+公开仓库同步支持 5 5 2026/7/3 20:47 编辑 删除" [ref=e730]':
                    - cell "系统管理员" [ref=e731]:
                      - generic [ref=e732]: 系统管理员
                    - 'cell "fix: Git绑定API改为/auth/git+前端适配+公开仓库同步支持" [ref=e733]':
                      - generic [ref=e734]: "fix: Git绑定API改为/auth/git+前端适配+公开仓库同步支持"
                    - cell "5" [ref=e735]:
                      - generic [ref=e736]: "5"
                    - cell "5" [ref=e737]:
                      - generic [ref=e738]: "5"
                    - cell "2026/7/3 20:47" [ref=e739]:
                      - generic [ref=e740]: 2026/7/3 20:47
                    - cell "编辑 删除" [ref=e741]:
                      - generic [ref=e742]:
                        - button "编辑" [ref=e743] [cursor=pointer]:
                          - generic [ref=e744]: 编辑
                        - button "删除" [ref=e745] [cursor=pointer]:
                          - generic [ref=e746]: 删除
                  - 'row "系统管理员 fix: 邀请码展示可复制+个人中心可编辑资料+缺失邀请码补全 93 87 2026/7/3 20:39 编辑 删除" [ref=e747]':
                    - cell "系统管理员" [ref=e748]:
                      - generic [ref=e749]: 系统管理员
                    - 'cell "fix: 邀请码展示可复制+个人中心可编辑资料+缺失邀请码补全" [ref=e750]':
                      - generic [ref=e751]: "fix: 邀请码展示可复制+个人中心可编辑资料+缺失邀请码补全"
                    - cell "93" [ref=e752]:
                      - generic [ref=e753]: "93"
                    - cell "87" [ref=e754]:
                      - generic [ref=e755]: "87"
                    - cell "2026/7/3 20:39" [ref=e756]:
                      - generic [ref=e757]: 2026/7/3 20:39
                    - cell "编辑 删除" [ref=e758]:
                      - generic [ref=e759]:
                        - button "编辑" [ref=e760] [cursor=pointer]:
                          - generic [ref=e761]: 编辑
                        - button "删除" [ref=e762] [cursor=pointer]:
                          - generic [ref=e763]: 删除
                  - 'row "系统管理员 feat: Git身份绑定 - 学生绑定Git用户名+邮箱, 同步时自动匹配提交到对应学生 41 126 2026/7/3 20:33 编辑 删除" [ref=e764]':
                    - cell "系统管理员" [ref=e765]:
                      - generic [ref=e766]: 系统管理员
                    - 'cell "feat: Git身份绑定 - 学生绑定Git用户名+邮箱, 同步时自动匹配提交到对应学生" [ref=e767]':
                      - generic [ref=e768]: "feat: Git身份绑定 - 学生绑定Git用户名+邮箱, 同步时自动匹配提交到对应学生"
                    - cell "41" [ref=e769]:
                      - generic [ref=e770]: "41"
                    - cell "126" [ref=e771]:
                      - generic [ref=e772]: "126"
                    - cell "2026/7/3 20:33" [ref=e773]:
                      - generic [ref=e774]: 2026/7/3 20:33
                    - cell "编辑 删除" [ref=e775]:
                      - generic [ref=e776]:
                        - button "编辑" [ref=e777] [cursor=pointer]:
                          - generic [ref=e778]: 编辑
                        - button "删除" [ref=e779] [cursor=pointer]:
                          - generic [ref=e780]: 删除
                  - 'row "系统管理员 feat: 聊天式问答界面 - 会话列表+消息气泡+文件上传+MaxKB自动回复+typing动画 253 53 2026/7/3 20:22 编辑 删除" [ref=e781]':
                    - cell "系统管理员" [ref=e782]:
                      - generic [ref=e783]: 系统管理员
                    - 'cell "feat: 聊天式问答界面 - 会话列表+消息气泡+文件上传+MaxKB自动回复+typing动画" [ref=e784]':
                      - generic [ref=e785]: "feat: 聊天式问答界面 - 会话列表+消息气泡+文件上传+MaxKB自动回复+typing动画"
                    - cell "253" [ref=e786]:
                      - generic [ref=e787]: "253"
                    - cell "53" [ref=e788]:
                      - generic [ref=e789]: "53"
                    - cell "2026/7/3 20:22" [ref=e790]:
                      - generic [ref=e791]: 2026/7/3 20:22
                    - cell "编辑 删除" [ref=e792]:
                      - generic [ref=e793]:
                        - button "编辑" [ref=e794] [cursor=pointer]:
                          - generic [ref=e795]: 编辑
                        - button "删除" [ref=e796] [cursor=pointer]:
                          - generic [ref=e797]: 删除
                  - 'row "系统管理员 feat: 统一个人中心 - 所有角色可查看个人信息+修改密码+我的小组/成绩/概览 95 70 2026/7/3 20:20 编辑 删除" [ref=e798]':
                    - cell "系统管理员" [ref=e799]:
                      - generic [ref=e800]: 系统管理员
                    - 'cell "feat: 统一个人中心 - 所有角色可查看个人信息+修改密码+我的小组/成绩/概览" [ref=e801]':
                      - generic [ref=e802]: "feat: 统一个人中心 - 所有角色可查看个人信息+修改密码+我的小组/成绩/概览"
                    - cell "95" [ref=e803]:
                      - generic [ref=e804]: "95"
                    - cell "70" [ref=e805]:
                      - generic [ref=e806]: "70"
                    - cell "2026/7/3 20:20" [ref=e807]:
                      - generic [ref=e808]: 2026/7/3 20:20
                    - cell "编辑 删除" [ref=e809]:
                      - generic [ref=e810]:
                        - button "编辑" [ref=e811] [cursor=pointer]:
                          - generic [ref=e812]: 编辑
                        - button "删除" [ref=e813] [cursor=pointer]:
                          - generic [ref=e814]: 删除
                  - 'row "系统管理员 feat: 系统设置页面 - 密码修改+用户管理+学生达成度查询+各角色入口 98 0 2026/7/3 20:18 编辑 删除" [ref=e815]':
                    - cell "系统管理员" [ref=e816]:
                      - generic [ref=e817]: 系统管理员
                    - 'cell "feat: 系统设置页面 - 密码修改+用户管理+学生达成度查询+各角色入口" [ref=e818]':
                      - generic [ref=e819]: "feat: 系统设置页面 - 密码修改+用户管理+学生达成度查询+各角色入口"
                    - cell "98" [ref=e820]:
                      - generic [ref=e821]: "98"
                    - cell "0" [ref=e822]:
                      - generic [ref=e823]: "0"
                    - cell "2026/7/3 20:18" [ref=e824]:
                      - generic [ref=e825]: 2026/7/3 20:18
                    - cell "编辑 删除" [ref=e826]:
                      - generic [ref=e827]:
                        - button "编辑" [ref=e828] [cursor=pointer]:
                          - generic [ref=e829]: 编辑
                        - button "删除" [ref=e830] [cursor=pointer]:
                          - generic [ref=e831]: 删除
                  - 'row "系统管理员 feat: Gitee/GitHub API自动采集Git提交 - 仓库配置+定时同步+手动同步+用户邮箱匹配 246 134 2026/7/3 20:15 编辑 删除" [ref=e832]':
                    - cell "系统管理员" [ref=e833]:
                      - generic [ref=e834]: 系统管理员
                    - 'cell "feat: Gitee/GitHub API自动采集Git提交 - 仓库配置+定时同步+手动同步+用户邮箱匹配" [ref=e835]':
                      - generic [ref=e836]: "feat: Gitee/GitHub API自动采集Git提交 - 仓库配置+定时同步+手动同步+用户邮箱匹配"
                    - cell "246" [ref=e837]:
                      - generic [ref=e838]: "246"
                    - cell "134" [ref=e839]:
                      - generic [ref=e840]: "134"
                    - cell "2026/7/3 20:15" [ref=e841]:
                      - generic [ref=e842]: 2026/7/3 20:15
                    - cell "编辑 删除" [ref=e843]:
                      - generic [ref=e844]:
                        - button "编辑" [ref=e845] [cursor=pointer]:
                          - generic [ref=e846]: 编辑
                        - button "删除" [ref=e847] [cursor=pointer]:
                          - generic [ref=e848]: 删除
                  - 'row "系统管理员 fix: 看板支持回退和拖拽 - DOING可退回TODO+DONE可退回DOING+拖动切换列 24 6 2026/7/3 20:12 编辑 删除" [ref=e849]':
                    - cell "系统管理员" [ref=e850]:
                      - generic [ref=e851]: 系统管理员
                    - 'cell "fix: 看板支持回退和拖拽 - DOING可退回TODO+DONE可退回DOING+拖动切换列" [ref=e852]':
                      - generic [ref=e853]: "fix: 看板支持回退和拖拽 - DOING可退回TODO+DONE可退回DOING+拖动切换列"
                    - cell "24" [ref=e854]:
                      - generic [ref=e855]: "24"
                    - cell "6" [ref=e856]:
                      - generic [ref=e857]: "6"
                    - cell "2026/7/3 20:12" [ref=e858]:
                      - generic [ref=e859]: 2026/7/3 20:12
                    - cell "编辑 删除" [ref=e860]:
                      - generic [ref=e861]:
                        - button "编辑" [ref=e862] [cursor=pointer]:
                          - generic [ref=e863]: 编辑
                        - button "删除" [ref=e864] [cursor=pointer]:
                          - generic [ref=e865]: 删除
                  - 'row "系统管理员 ux: 页面打开即显示数据，无需额外点击 - 小组直接展示成员+QA自动加载统计+达成度自动计算 13 11 2026/7/3 20:10 编辑 删除" [ref=e866]':
                    - cell "系统管理员" [ref=e867]:
                      - generic [ref=e868]: 系统管理员
                    - 'cell "ux: 页面打开即显示数据，无需额外点击 - 小组直接展示成员+QA自动加载统计+达成度自动计算" [ref=e869]':
                      - generic [ref=e870]: "ux: 页面打开即显示数据，无需额外点击 - 小组直接展示成员+QA自动加载统计+达成度自动计算"
                    - cell "13" [ref=e871]:
                      - generic [ref=e872]: "13"
                    - cell "11" [ref=e873]:
                      - generic [ref=e874]: "11"
                    - cell "2026/7/3 20:10" [ref=e875]:
                      - generic [ref=e876]: 2026/7/3 20:10
                    - cell "编辑 删除" [ref=e877]:
                      - generic [ref=e878]:
                        - button "编辑" [ref=e879] [cursor=pointer]:
                          - generic [ref=e880]: 编辑
                        - button "删除" [ref=e881] [cursor=pointer]:
                          - generic [ref=e882]: 删除
                  - 'row "系统管理员 fix: 修复全部P1问题 40 170 2026/7/3 20:07 编辑 删除" [ref=e883]':
                    - cell "系统管理员" [ref=e884]:
                      - generic [ref=e885]: 系统管理员
                    - 'cell "fix: 修复全部P1问题" [ref=e886]':
                      - generic [ref=e887]: "fix: 修复全部P1问题"
                    - cell "40" [ref=e888]:
                      - generic [ref=e889]: "40"
                    - cell "170" [ref=e890]:
                      - generic [ref=e891]: "170"
                    - cell "2026/7/3 20:07" [ref=e892]:
                      - generic [ref=e893]: 2026/7/3 20:07
                    - cell "编辑 删除" [ref=e894]:
                      - generic [ref=e895]:
                        - button "编辑" [ref=e896] [cursor=pointer]:
                          - generic [ref=e897]: 编辑
                        - button "删除" [ref=e898] [cursor=pointer]:
                          - generic [ref=e899]: 删除
                  - 'row "系统管理员 fix: P0安全修复 - SecurityConfig规则顺序+MaxKB认证+批量计算全员+suggestion删除范围 47 18 2026/7/3 20:01 编辑 删除" [ref=e900]':
                    - cell "系统管理员" [ref=e901]:
                      - generic [ref=e902]: 系统管理员
                    - 'cell "fix: P0安全修复 - SecurityConfig规则顺序+MaxKB认证+批量计算全员+suggestion删除范围" [ref=e903]':
                      - generic [ref=e904]: "fix: P0安全修复 - SecurityConfig规则顺序+MaxKB认证+批量计算全员+suggestion删除范围"
                    - cell "47" [ref=e905]:
                      - generic [ref=e906]: "47"
                    - cell "18" [ref=e907]:
                      - generic [ref=e908]: "18"
                    - cell "2026/7/3 20:01" [ref=e909]:
                      - generic [ref=e910]: 2026/7/3 20:01
                    - cell "编辑 删除" [ref=e911]:
                      - generic [ref=e912]:
                        - button "编辑" [ref=e913] [cursor=pointer]:
                          - generic [ref=e914]: 编辑
                        - button "删除" [ref=e915] [cursor=pointer]:
                          - generic [ref=e916]: 删除
                  - 'row "系统管理员 feat: OBE-CDIO软件工程课程教学评价管理系统 v1.0 5922 0 2026/7/3 19:53 编辑 删除" [ref=e917]':
                    - cell "系统管理员" [ref=e918]:
                      - generic [ref=e919]: 系统管理员
                    - 'cell "feat: OBE-CDIO软件工程课程教学评价管理系统 v1.0" [ref=e920]':
                      - generic [ref=e921]: "feat: OBE-CDIO软件工程课程教学评价管理系统 v1.0"
                    - cell "5922" [ref=e922]:
                      - generic [ref=e923]: "5922"
                    - cell "0" [ref=e924]:
                      - generic [ref=e925]: "0"
                    - cell "2026/7/3 19:53" [ref=e926]:
                      - generic [ref=e927]: 2026/7/3 19:53
                    - cell "编辑 删除" [ref=e928]:
                      - generic [ref=e929]:
                        - button "编辑" [ref=e930] [cursor=pointer]:
                          - generic [ref=e931]: 编辑
                        - button "删除" [ref=e932] [cursor=pointer]:
                          - generic [ref=e933]: 删除
                  - 'row "系统管理员 fix: 修复中文编码问题 5 2 2026/6/23 13:00 编辑 删除" [ref=e934]':
                    - cell "系统管理员" [ref=e935]:
                      - generic [ref=e936]: 系统管理员
                    - 'cell "fix: 修复中文编码问题" [ref=e937]':
                      - generic [ref=e938]: "fix: 修复中文编码问题"
                    - cell "5" [ref=e939]:
                      - generic [ref=e940]: "5"
                    - cell "2" [ref=e941]:
                      - generic [ref=e942]: "2"
                    - cell "2026/6/23 13:00" [ref=e943]:
                      - generic [ref=e944]: 2026/6/23 13:00
                    - cell "编辑 删除" [ref=e945]:
                      - generic [ref=e946]:
                        - button "编辑" [ref=e947] [cursor=pointer]:
                          - generic [ref=e948]: 编辑
                        - button "删除" [ref=e949] [cursor=pointer]:
                          - generic [ref=e950]: 删除
                  - 'row "系统管理员 feat: 改进建议自动生成 110 20 2026/6/23 12:00 编辑 删除" [ref=e951]':
                    - cell "系统管理员" [ref=e952]:
                      - generic [ref=e953]: 系统管理员
                    - 'cell "feat: 改进建议自动生成" [ref=e954]':
                      - generic [ref=e955]: "feat: 改进建议自动生成"
                    - cell "110" [ref=e956]:
                      - generic [ref=e957]: "110"
                    - cell "20" [ref=e958]:
                      - generic [ref=e959]: "20"
                    - cell "2026/6/23 12:00" [ref=e960]:
                      - generic [ref=e961]: 2026/6/23 12:00
                    - cell "编辑 删除" [ref=e962]:
                      - generic [ref=e963]:
                        - button "编辑" [ref=e964] [cursor=pointer]:
                          - generic [ref=e965]: 编辑
                        - button "删除" [ref=e966] [cursor=pointer]:
                          - generic [ref=e967]: 删除
                  - 'row "系统管理员 docs: 完善Swagger接口文档 3 0 2026/6/23 11:00 编辑 删除" [ref=e968]':
                    - cell "系统管理员" [ref=e969]:
                      - generic [ref=e970]: 系统管理员
                    - 'cell "docs: 完善Swagger接口文档" [ref=e971]':
                      - generic [ref=e972]: "docs: 完善Swagger接口文档"
                    - cell "3" [ref=e973]:
                      - generic [ref=e974]: "3"
                    - cell "0" [ref=e975]:
                      - generic [ref=e976]: "0"
                    - cell "2026/6/23 11:00" [ref=e977]:
                      - generic [ref=e978]: 2026/6/23 11:00
                    - cell "编辑 删除" [ref=e979]:
                      - generic [ref=e980]:
                        - button "编辑" [ref=e981] [cursor=pointer]:
                          - generic [ref=e982]: 编辑
                        - button "删除" [ref=e983] [cursor=pointer]:
                          - generic [ref=e984]: 删除
                  - 'row "系统管理员 feat: 过程/终结分层达成度 95 15 2026/6/23 10:00 编辑 删除" [ref=e985]':
                    - cell "系统管理员" [ref=e986]:
                      - generic [ref=e987]: 系统管理员
                    - 'cell "feat: 过程/终结分层达成度" [ref=e988]':
                      - generic [ref=e989]: "feat: 过程/终结分层达成度"
                    - cell "95" [ref=e990]:
                      - generic [ref=e991]: "95"
                    - cell "15" [ref=e992]:
                      - generic [ref=e993]: "15"
                    - cell "2026/6/23 10:00" [ref=e994]:
                      - generic [ref=e995]: 2026/6/23 10:00
                    - cell "编辑 删除" [ref=e996]:
                      - generic [ref=e997]:
                        - button "编辑" [ref=e998] [cursor=pointer]:
                          - generic [ref=e999]: 编辑
                        - button "删除" [ref=e1000] [cursor=pointer]:
                          - generic [ref=e1001]: 删除
                  - 'row "系统管理员 fix: 修正软删除查询遗漏 12 4 2026/6/23 09:00 编辑 删除" [ref=e1002]':
                    - cell "系统管理员" [ref=e1003]:
                      - generic [ref=e1004]: 系统管理员
                    - 'cell "fix: 修正软删除查询遗漏" [ref=e1005]':
                      - generic [ref=e1006]: "fix: 修正软删除查询遗漏"
                    - cell "12" [ref=e1007]:
                      - generic [ref=e1008]: "12"
                    - cell "4" [ref=e1009]:
                      - generic [ref=e1010]: "4"
                    - cell "2026/6/23 09:00" [ref=e1011]:
                      - generic [ref=e1012]: 2026/6/23 09:00
                    - cell "编辑 删除" [ref=e1013]:
                      - generic [ref=e1014]:
                        - button "编辑" [ref=e1015] [cursor=pointer]:
                          - generic [ref=e1016]: 编辑
                        - button "删除" [ref=e1017] [cursor=pointer]:
                          - generic [ref=e1018]: 删除
                  - 'row "系统管理员 feat: AHP权重校验功能 75 5 2026/6/22 13:00 编辑 删除" [ref=e1019]':
                    - cell "系统管理员" [ref=e1020]:
                      - generic [ref=e1021]: 系统管理员
                    - 'cell "feat: AHP权重校验功能" [ref=e1022]':
                      - generic [ref=e1023]: "feat: AHP权重校验功能"
                    - cell "75" [ref=e1024]:
                      - generic [ref=e1025]: "75"
                    - cell "5" [ref=e1026]:
                      - generic [ref=e1027]: "5"
                    - cell "2026/6/22 13:00" [ref=e1028]:
                      - generic [ref=e1029]: 2026/6/22 13:00
                    - cell "编辑 删除" [ref=e1030]:
                      - generic [ref=e1031]:
                        - button "编辑" [ref=e1032] [cursor=pointer]:
                          - generic [ref=e1033]: 编辑
                        - button "删除" [ref=e1034] [cursor=pointer]:
                          - generic [ref=e1035]: 删除
                  - 'row "系统管理员 refactor: 统一异常处理 60 40 2026/6/22 12:00 编辑 删除" [ref=e1036]':
                    - cell "系统管理员" [ref=e1037]:
                      - generic [ref=e1038]: 系统管理员
                    - 'cell "refactor: 统一异常处理" [ref=e1039]':
                      - generic [ref=e1040]: "refactor: 统一异常处理"
                    - cell "60" [ref=e1041]:
                      - generic [ref=e1042]: "60"
                    - cell "40" [ref=e1043]:
                      - generic [ref=e1044]: "40"
                    - cell "2026/6/22 12:00" [ref=e1045]:
                      - generic [ref=e1046]: 2026/6/22 12:00
                    - cell "编辑 删除" [ref=e1047]:
                      - generic [ref=e1048]:
                        - button "编辑" [ref=e1049] [cursor=pointer]:
                          - generic [ref=e1050]: 编辑
                        - button "删除" [ref=e1051] [cursor=pointer]:
                          - generic [ref=e1052]: 删除
                  - 'row "系统管理员 feat: 五维雷达图数据接口 90 10 2026/6/22 11:00 编辑 删除" [ref=e1053]':
                    - cell "系统管理员" [ref=e1054]:
                      - generic [ref=e1055]: 系统管理员
                    - 'cell "feat: 五维雷达图数据接口" [ref=e1056]':
                      - generic [ref=e1057]: "feat: 五维雷达图数据接口"
                    - cell "90" [ref=e1058]:
                      - generic [ref=e1059]: "90"
                    - cell "10" [ref=e1060]:
                      - generic [ref=e1061]: "10"
                    - cell "2026/6/22 11:00" [ref=e1062]:
                      - generic [ref=e1063]: 2026/6/22 11:00
                    - cell "编辑 删除" [ref=e1064]:
                      - generic [ref=e1065]:
                        - button "编辑" [ref=e1066] [cursor=pointer]:
                          - generic [ref=e1067]: 编辑
                        - button "删除" [ref=e1068] [cursor=pointer]:
                          - generic [ref=e1069]: 删除
                  - 'row "系统管理员 fix: 修复个人成绩计算精度问题 8 3 2026/6/22 10:00 编辑 删除" [ref=e1070]':
                    - cell "系统管理员" [ref=e1071]:
                      - generic [ref=e1072]: 系统管理员
                    - 'cell "fix: 修复个人成绩计算精度问题" [ref=e1073]':
                      - generic [ref=e1074]: "fix: 修复个人成绩计算精度问题"
                    - cell "8" [ref=e1075]:
                      - generic [ref=e1076]: "8"
                    - cell "3" [ref=e1077]:
                      - generic [ref=e1078]: "3"
                    - cell "2026/6/22 10:00" [ref=e1079]:
                      - generic [ref=e1080]: 2026/6/22 10:00
                    - cell "编辑 删除" [ref=e1081]:
                      - generic [ref=e1082]:
                        - button "编辑" [ref=e1083] [cursor=pointer]:
                          - generic [ref=e1084]: 编辑
                        - button "删除" [ref=e1085] [cursor=pointer]:
                          - generic [ref=e1086]: 删除
                  - 'row "系统管理员 feat: MaxKB问答记录同步 130 30 2026/6/22 09:00 编辑 删除" [ref=e1087]':
                    - cell "系统管理员" [ref=e1088]:
                      - generic [ref=e1089]: 系统管理员
                    - 'cell "feat: MaxKB问答记录同步" [ref=e1090]':
                      - generic [ref=e1091]: "feat: MaxKB问答记录同步"
                    - cell "130" [ref=e1092]:
                      - generic [ref=e1093]: "130"
                    - cell "30" [ref=e1094]:
                      - generic [ref=e1095]: "30"
                    - cell "2026/6/22 09:00" [ref=e1096]:
                      - generic [ref=e1097]: 2026/6/22 09:00
                    - cell "编辑 删除" [ref=e1098]:
                      - generic [ref=e1099]:
                        - button "编辑" [ref=e1100] [cursor=pointer]:
                          - generic [ref=e1101]: 编辑
                        - button "删除" [ref=e1102] [cursor=pointer]:
                          - generic [ref=e1103]: 删除
                  - 'row "系统管理员 docs: 添加API文档注释 5 0 2026/6/21 13:00 编辑 删除" [ref=e1104]':
                    - cell "系统管理员" [ref=e1105]:
                      - generic [ref=e1106]: 系统管理员
                    - 'cell "docs: 添加API文档注释" [ref=e1107]':
                      - generic [ref=e1108]: "docs: 添加API文档注释"
                    - cell "5" [ref=e1109]:
                      - generic [ref=e1110]: "5"
                    - cell "0" [ref=e1111]:
                      - generic [ref=e1112]: "0"
                    - cell "2026/6/21 13:00" [ref=e1113]:
                      - generic [ref=e1114]: 2026/6/21 13:00
                    - cell "编辑 删除" [ref=e1115]:
                      - generic [ref=e1116]:
                        - button "编辑" [ref=e1117] [cursor=pointer]:
                          - generic [ref=e1118]: 编辑
                        - button "删除" [ref=e1119] [cursor=pointer]:
                          - generic [ref=e1120]: 删除
                  - 'row "系统管理员 feat: 项目日志和里程碑功能 220 20 2026/6/21 12:00 编辑 删除" [ref=e1121]':
                    - cell "系统管理员" [ref=e1122]:
                      - generic [ref=e1123]: 系统管理员
                    - 'cell "feat: 项目日志和里程碑功能" [ref=e1124]':
                      - generic [ref=e1125]: "feat: 项目日志和里程碑功能"
                    - cell "220" [ref=e1126]:
                      - generic [ref=e1127]: "220"
                    - cell "20" [ref=e1128]:
                      - generic [ref=e1129]: "20"
                    - cell "2026/6/21 12:00" [ref=e1130]:
                      - generic [ref=e1131]: 2026/6/21 12:00
                    - cell "编辑 删除" [ref=e1132]:
                      - generic [ref=e1133]:
                        - button "编辑" [ref=e1134] [cursor=pointer]:
                          - generic [ref=e1135]: 编辑
                        - button "删除" [ref=e1136] [cursor=pointer]:
                          - generic [ref=e1137]: 删除
                  - 'row "系统管理员 fix: 修复角色分配Bug 10 5 2026/6/21 11:00 编辑 删除" [ref=e1138]':
                    - cell "系统管理员" [ref=e1139]:
                      - generic [ref=e1140]: 系统管理员
                    - 'cell "fix: 修复角色分配Bug" [ref=e1141]':
                      - generic [ref=e1142]: "fix: 修复角色分配Bug"
                    - cell "10" [ref=e1143]:
                      - generic [ref=e1144]: "10"
                    - cell "5" [ref=e1145]:
                      - generic [ref=e1146]: "5"
                    - cell "2026/6/21 11:00" [ref=e1147]:
                      - generic [ref=e1148]: 2026/6/21 11:00
                    - cell "编辑 删除" [ref=e1149]:
                      - generic [ref=e1150]:
                        - button "编辑" [ref=e1151] [cursor=pointer]:
                          - generic [ref=e1152]: 编辑
                        - button "删除" [ref=e1153] [cursor=pointer]:
                          - generic [ref=e1154]: 删除
                  - 'row "系统管理员 feat: 小组管理模块 150 5 2026/6/21 10:00 编辑 删除" [ref=e1155]':
                    - cell "系统管理员" [ref=e1156]:
                      - generic [ref=e1157]: 系统管理员
                    - 'cell "feat: 小组管理模块" [ref=e1158]':
                      - generic [ref=e1159]: "feat: 小组管理模块"
                    - cell "150" [ref=e1160]:
                      - generic [ref=e1161]: "150"
                    - cell "5" [ref=e1162]:
                      - generic [ref=e1163]: "5"
                    - cell "2026/6/21 10:00" [ref=e1164]:
                      - generic [ref=e1165]: 2026/6/21 10:00
                    - cell "编辑 删除" [ref=e1166]:
                      - generic [ref=e1167]:
                        - button "编辑" [ref=e1168] [cursor=pointer]:
                          - generic [ref=e1169]: 编辑
                        - button "删除" [ref=e1170] [cursor=pointer]:
                          - generic [ref=e1171]: 删除
                  - 'row "系统管理员 refactor: 优化达成度计算性能 80 45 2026/6/21 09:00 编辑 删除" [ref=e1172]':
                    - cell "系统管理员" [ref=e1173]:
                      - generic [ref=e1174]: 系统管理员
                    - 'cell "refactor: 优化达成度计算性能" [ref=e1175]':
                      - generic [ref=e1176]: "refactor: 优化达成度计算性能"
                    - cell "80" [ref=e1177]:
                      - generic [ref=e1178]: "80"
                    - cell "45" [ref=e1179]:
                      - generic [ref=e1180]: "45"
                    - cell "2026/6/21 09:00" [ref=e1181]:
                      - generic [ref=e1182]: 2026/6/21 09:00
                    - cell "编辑 删除" [ref=e1183]:
                      - generic [ref=e1184]:
                        - button "编辑" [ref=e1185] [cursor=pointer]:
                          - generic [ref=e1186]: 编辑
                        - button "删除" [ref=e1187] [cursor=pointer]:
                          - generic [ref=e1188]: 删除
                  - 'row "系统管理员 feat: 课程目标CRUD接口 180 10 2026/6/20 13:00 编辑 删除" [ref=e1189]':
                    - cell "系统管理员" [ref=e1190]:
                      - generic [ref=e1191]: 系统管理员
                    - 'cell "feat: 课程目标CRUD接口" [ref=e1192]':
                      - generic [ref=e1193]: "feat: 课程目标CRUD接口"
                    - cell "180" [ref=e1194]:
                      - generic [ref=e1195]: "180"
                    - cell "10" [ref=e1196]:
                      - generic [ref=e1197]: "10"
                    - cell "2026/6/20 13:00" [ref=e1198]:
                      - generic [ref=e1199]: 2026/6/20 13:00
                    - cell "编辑 删除" [ref=e1200]:
                      - generic [ref=e1201]:
                        - button "编辑" [ref=e1202] [cursor=pointer]:
                          - generic [ref=e1203]: 编辑
                        - button "删除" [ref=e1204] [cursor=pointer]:
                          - generic [ref=e1205]: 删除
                  - 'row "系统管理员 fix: 修复Token过期判断逻辑 15 8 2026/6/20 12:00 编辑 删除" [ref=e1206]':
                    - cell "系统管理员" [ref=e1207]:
                      - generic [ref=e1208]: 系统管理员
                    - 'cell "fix: 修复Token过期判断逻辑" [ref=e1209]':
                      - generic [ref=e1210]: "fix: 修复Token过期判断逻辑"
                    - cell "15" [ref=e1211]:
                      - generic [ref=e1212]: "15"
                    - cell "8" [ref=e1213]:
                      - generic [ref=e1214]: "8"
                    - cell "2026/6/20 12:00" [ref=e1215]:
                      - generic [ref=e1216]: 2026/6/20 12:00
                    - cell "编辑 删除" [ref=e1217]:
                      - generic [ref=e1218]:
                        - button "编辑" [ref=e1219] [cursor=pointer]:
                          - generic [ref=e1220]: 编辑
                        - button "删除" [ref=e1221] [cursor=pointer]:
                          - generic [ref=e1222]: 删除
                  - 'row "系统管理员 feat: 实现JWT认证模块 200 15 2026/6/20 11:00 编辑 删除" [ref=e1223]':
                    - cell "系统管理员" [ref=e1224]:
                      - generic [ref=e1225]: 系统管理员
                    - 'cell "feat: 实现JWT认证模块" [ref=e1226]':
                      - generic [ref=e1227]: "feat: 实现JWT认证模块"
                    - cell "200" [ref=e1228]:
                      - generic [ref=e1229]: "200"
                    - cell "15" [ref=e1230]:
                      - generic [ref=e1231]: "15"
                    - cell "2026/6/20 11:00" [ref=e1232]:
                      - generic [ref=e1233]: 2026/6/20 11:00
                    - cell "编辑 删除" [ref=e1234]:
                      - generic [ref=e1235]:
                        - button "编辑" [ref=e1236] [cursor=pointer]:
                          - generic [ref=e1237]: 编辑
                        - button "删除" [ref=e1238] [cursor=pointer]:
                          - generic [ref=e1239]: 删除
                  - 'row "系统管理员 feat: 配置PostgreSQL和Redis连接 45 2 2026/6/20 10:00 编辑 删除" [ref=e1240]':
                    - cell "系统管理员" [ref=e1241]:
                      - generic [ref=e1242]: 系统管理员
                    - 'cell "feat: 配置PostgreSQL和Redis连接" [ref=e1243]':
                      - generic [ref=e1244]: "feat: 配置PostgreSQL和Redis连接"
                    - cell "45" [ref=e1245]:
                      - generic [ref=e1246]: "45"
                    - cell "2" [ref=e1247]:
                      - generic [ref=e1248]: "2"
                    - cell "2026/6/20 10:00" [ref=e1249]:
                      - generic [ref=e1250]: 2026/6/20 10:00
                    - cell "编辑 删除" [ref=e1251]:
                      - generic [ref=e1252]:
                        - button "编辑" [ref=e1253] [cursor=pointer]:
                          - generic [ref=e1254]: 编辑
                        - button "删除" [ref=e1255] [cursor=pointer]:
                          - generic [ref=e1256]: 删除
                  - 'row "系统管理员 feat: 初始化Spring Boot项目结构 120 0 2026/6/20 09:00 编辑 删除" [ref=e1257]':
                    - cell "系统管理员" [ref=e1258]:
                      - generic [ref=e1259]: 系统管理员
                    - 'cell "feat: 初始化Spring Boot项目结构" [ref=e1260]':
                      - generic [ref=e1261]: "feat: 初始化Spring Boot项目结构"
                    - cell "120" [ref=e1262]:
                      - generic [ref=e1263]: "120"
                    - cell "0" [ref=e1264]:
                      - generic [ref=e1265]: "0"
                    - cell "2026/6/20 09:00" [ref=e1266]:
                      - generic [ref=e1267]: 2026/6/20 09:00
                    - cell "编辑 删除" [ref=e1268]:
                      - generic [ref=e1269]:
                        - button "编辑" [ref=e1270] [cursor=pointer]:
                          - generic [ref=e1271]: 编辑
                        - button "删除" [ref=e1272] [cursor=pointer]:
                          - generic [ref=e1273]: 删除
```

# Test source

```ts
  1   | const { test, expect } = require('@playwright/test');
  2   | const { apiLogin, apiRequest } = require('./helpers');
  3   | 
  4   | test.describe('前端渲染验证（DOM内容检查）', () => {
  5   | 
  6   |   let adminToken;
  7   |   test.beforeAll(async ({ request }) => {
  8   |     adminToken = (await apiLogin(request, 'testadm', 'admin123')).token;
  9   |   });
  10  | 
  11  |   // 通用工具：获取页面上表格所有行的文本内容
  12  |   async function getTableRows(page, tableIndex = 0) {
  13  |     const tables = page.locator('.el-table__body');
  14  |     if (await tables.count() <= tableIndex) return [];
  15  |     const rows = tables.nth(tableIndex).locator('tr');
  16  |     const count = await rows.count();
  17  |     const data = [];
  18  |     for (let i = 0; i < count; i++) {
  19  |       const cells = rows.nth(i).locator('.cell');
  20  |       const cellCount = await cells.count();
  21  |       const row = [];
  22  |       for (let j = 0; j < cellCount; j++) {
  23  |         row.push((await cells.nth(j).textContent()).trim());
  24  |       }
  25  |       data.push(row);
  26  |     }
  27  |     return data;
  28  |   }
  29  | 
  30  |   // 通用工具：登录并导航
  31  |   async function loginAndGo(page, menu) {
  32  |     await page.goto('http://localhost:5173/#/login');
  33  |     await page.fill('input[placeholder="用户名"]', 'testadm');
  34  |     await page.fill('input[placeholder="密码"]', 'admin123');
  35  |     await page.click('button:has-text("登 录")');
  36  |     await page.waitForURL('**/#/course**', { timeout: 10000 });
  37  |     if (menu) {
  38  |       await page.click(`.el-menu-item:has-text("${menu}")`);
  39  |       await page.waitForTimeout(500);
  40  |     }
  41  |   }
  42  | 
  43  |   async function selectFirstGroup(page) {
  44  |     await page.click('.el-select');
  45  |     await page.waitForSelector('.el-select-dropdown__item', { state: 'visible' });
  46  |     await page.locator('.el-select-dropdown__item').first().click();
  47  |     await page.waitForTimeout(500);
  48  |   }
  49  | 
  50  |   // ========== 关键UI验证 ==========
  51  | 
  52  |   test('RENDER-01: Git提交表所有列均非空', async ({ page }) => {
  53  |     await loginAndGo(page, '项目追踪');
  54  |     await selectFirstGroup(page);
  55  |     await page.click('.el-tabs__item:has-text("Git 提交")');
  56  |     await page.waitForTimeout(1500);
  57  | 
  58  |     const rows = await getTableRows(page, 0);
  59  |     if (rows.length === 0) return; // 没有Git提交数据时跳过
  60  | 
  61  |     const firstRow = rows[0];
  62  |     // 列顺序：用户、提交信息、+行数、-行数、时间、操作
  63  |     expect(firstRow.length, 'Git提交表应有至少5列').toBeGreaterThanOrEqual(5);
  64  | 
  65  |     // 用户列不能为空
  66  |     expect(firstRow[0], `Git提交第1行用户列为空。全行数据: ${JSON.stringify(firstRow)}`).not.toBe('');
  67  |     // +行数列必须是数字
  68  |     const adds = parseInt(firstRow[2]);
  69  |     expect(isNaN(adds), `Git提交+行数不是数字: "${firstRow[2]}"`).toBe(false);
  70  |     // -行数列必须是数字
  71  |     const dels = parseInt(firstRow[3]);
> 72  |     expect(isNaN(dels), `Git提交-行数不是数字: "${firstRow[3]}"`).toBe(false);
      |                                                           ^ Error: Git提交-行数不是数字: "未开始"
  73  |     // 时间列不能为空
  74  |     expect(firstRow[4], `Git提交第1行时间列为空`).not.toBe('');
  75  |   });
  76  | 
  77  |   test('RENDER-02: 个人成绩表所有列均非空且贡献系数在0-1', async ({ page }) => {
  78  |     await loginAndGo(page, '多元评价');
  79  |     await selectFirstGroup(page);
  80  |     await page.click('.el-tabs__item:has-text("个人成绩")');
  81  |     await page.waitForTimeout(500);
  82  | 
  83  |     // 如果没数据，先计算
  84  |     const calcBtn = page.locator('button:has-text("计算全员成绩")');
  85  |     if (await calcBtn.count() > 0) {
  86  |       await calcBtn.click();
  87  |       await page.waitForTimeout(1500);
  88  |     }
  89  | 
  90  |     const rows = await getTableRows(page, 0);
  91  |     if (rows.length === 0) return;
  92  | 
  93  |     const firstRow = rows[0];
  94  |     // 列：用户、小组分、贡献系数、加分、最终成绩
  95  |     expect(firstRow.length).toBeGreaterThanOrEqual(5);
  96  | 
  97  |     // 用户名不能为空
  98  |     expect(firstRow[0], `用户列为空`).not.toBe('');
  99  |     // 小组分是数字
  100 |     const groupScore = parseFloat(firstRow[1]);
  101 |     expect(isNaN(groupScore), `小组分不是数字: "${firstRow[1]}"`).toBe(false);
  102 |     // 贡献系数在0-1范围内
  103 |     const ratio = parseFloat(firstRow[2].replace('%', ''));
  104 |     if (!isNaN(ratio)) {
  105 |       expect(ratio, `贡献系数${ratio}超出[0,100]`).toBeGreaterThanOrEqual(0);
  106 |       expect(ratio, `贡献系数${ratio}超出[0,100]`).toBeLessThanOrEqual(100);
  107 |     }
  108 |     // 最终成绩是数字
  109 |     const final = parseFloat(firstRow[4]);
  110 |     expect(isNaN(final), `最终成绩不是数字: "${firstRow[4]}"`).toBe(false);
  111 |   });
  112 | 
  113 |   test('RENDER-03: 小组卡片显示组名和成员数（不是ID）', async ({ page }) => {
  114 |     await loginAndGo(page, '小组管理');
  115 |     await page.waitForTimeout(1000);
  116 | 
  117 |     // 定位第一张小组卡片
  118 |     const firstCard = page.locator('.group-card, .el-card').first();
  119 |     const cardText = await firstCard.textContent();
  120 | 
  121 |     // 组名应该包含中文（不是纯数字ID）
  122 |     const groupNameMatch = cardText.match(/软件工程|实战|测试/);
  123 |     expect(groupNameMatch, `组名不应是纯ID，卡片内容: ${cardText.substring(0, 80)}`).not.toBeNull();
  124 | 
  125 |     // 成员数应该显示数字/数字格式
  126 |     const memberMatch = cardText.match(/(\d+)\s*\/\s*(\d+)/);
  127 |     expect(memberMatch, `成员数格式应为 X/Y，卡片内容: ${cardText.substring(0, 80)}`).not.toBeNull();
  128 | 
  129 |     // 邀请码应该显示6位字符（不是空）
  130 |     const inviteMatch = cardText.match(/[A-Z0-9]{6}/);
  131 |     expect(inviteMatch, `邀请码应为6位字符，卡片内容: ${cardText.substring(0, 80)}`).not.toBeNull();
  132 |   });
  133 | 
  134 |   test('RENDER-04: 达成度表显示百分比数字', async ({ page }) => {
  135 |     await loginAndGo(page, '达成度分析');
  136 |     await selectFirstGroup(page);
  137 |     await page.click('button:has-text("计算达成度")');
  138 |     await page.waitForTimeout(2000);
  139 | 
  140 |     const rows = await getTableRows(page, 0);
  141 |     if (rows.length === 0) return;
  142 | 
  143 |     const firstRow = rows[0];
  144 |     // 列：目标标题、维度、权重、达成度
  145 |     expect(firstRow.length).toBeGreaterThanOrEqual(4);
  146 | 
  147 |     // 目标标题非空
  148 |     expect(firstRow[0], '目标标题为空').not.toBe('');
  149 |     // 权重含%号
  150 |     expect(firstRow[2], `权重不含%: "${firstRow[2]}"`).toContain('%');
  151 |     // 达成度含%号
  152 |     expect(firstRow[3], `达成度不含%: "${firstRow[3]}"`).toContain('%');
  153 |   });
  154 | 
  155 |   test('RENDER-05: 课程目标树至少显示目标条目', async ({ page }) => {
  156 |     await loginAndGo(page, '课程目标');
  157 |     await page.waitForTimeout(1000);
  158 | 
  159 |     const treeNodes = page.locator('.el-tree-node');
  160 |     const count = await treeNodes.count();
  161 |     expect(count, '课程目标树为空').toBeGreaterThan(0);
  162 | 
  163 |     // 第一个节点应该有文字内容
  164 |     const firstLabel = treeNodes.first().locator('.el-tree-node__label');
  165 |     const text = await firstLabel.textContent();
  166 |     expect(text.length, '树节点文字为空').toBeGreaterThan(0);
  167 |     // 应该包含类似 OBJ- 的编号
  168 |     expect(text, `树节点不包含OBJ编号: "${text}"`).toMatch(/OBJ|objective/i);
  169 |   });
  170 | 
  171 |   test('RENDER-06: 设置页用户管理表所有列非空', async ({ page }) => {
  172 |     await loginAndGo(page, '个人中心');
```