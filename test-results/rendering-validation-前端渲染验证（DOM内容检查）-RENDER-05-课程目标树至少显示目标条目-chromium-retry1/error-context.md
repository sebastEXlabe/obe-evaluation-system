# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: rendering-validation.spec.js >> 前端渲染验证（DOM内容检查） >> RENDER-05: 课程目标树至少显示目标条目
- Location: e2e\rendering-validation.spec.js:155:3

# Error details

```
Test timeout of 30000ms exceeded.
```

```
Error: locator.textContent: Test timeout of 30000ms exceeded.
Call log:
  - waiting for locator('.el-tree-node').first().locator('.el-tree-node__label')

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
      - menuitem "课程目标" [active] [ref=e12] [cursor=pointer]:
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
        - link "课程目标" [ref=e63]
      - button "退出登录" [ref=e64] [cursor=pointer]:
        - generic [ref=e65]: 退出登录
    - main [ref=e66]:
      - generic [ref=e67]:
        - generic [ref=e68]:
          - generic [ref=e70] [cursor=pointer]:
            - generic:
              - combobox [ref=e72]
              - generic [ref=e73]: 选择课程
            - img [ref=e76]
          - button "+ 课程目标" [ref=e78] [cursor=pointer]:
            - generic [ref=e79]: + 课程目标
          - button "+ 指标点" [disabled] [ref=e80]:
            - generic [ref=e81]: + 指标点
          - button "+ 评价方式" [disabled] [ref=e82]:
            - generic [ref=e83]: + 评价方式
          - button "+ CDIO映射" [disabled] [ref=e84]:
            - generic [ref=e85]: + CDIO映射
          - button "毕业要求映射" [disabled] [ref=e86]:
            - generic [ref=e87]: 毕业要求映射
          - button "刷新" [ref=e88] [cursor=pointer]:
            - generic [ref=e89]: 刷新
        - tree [ref=e90]:
          - treeitem "OBJ-T1 Master testing methods 能力 权重:50% ✏️ ✕" [ref=e91]:
            - generic [ref=e93] [cursor=pointer]:
              - generic [ref=e95]: OBJ-T1
              - generic [ref=e96]: Master testing methods
              - generic [ref=e98]: 能力
              - generic [ref=e100]: 权重:50%
              - button "✏️" [ref=e101]:
                - generic [ref=e102]: ✏️
              - button "✕" [ref=e103]:
                - generic [ref=e104]: ✕
          - treeitem "E2E-TEST 知识 权重:25% ✏️ ✕" [ref=e105]:
            - generic [ref=e107] [cursor=pointer]:
              - generic [ref=e109]: E2E-TEST
              - generic [ref=e111]: 知识
              - generic [ref=e113]: 权重:25%
              - button "✏️" [ref=e114]:
                - generic [ref=e115]: ✏️
              - button "✕" [ref=e116]:
                - generic [ref=e117]: ✕
          - treeitem "OBJ-01 掌握软件工程基本概念与方法 知识 权重:40% ✏️ ✕" [ref=e118]:
            - generic [ref=e119] [cursor=pointer]:
              - img [ref=e121]
              - generic [ref=e123]:
                - generic [ref=e125]: OBJ-01
                - generic [ref=e126]: 掌握软件工程基本概念与方法
                - generic [ref=e128]: 知识
                - generic [ref=e130]: 权重:40%
                - button "✏️" [ref=e131]:
                  - generic [ref=e132]: ✏️
                - button "✕" [ref=e133]:
                  - generic [ref=e134]: ✕
          - treeitem "OBJ-04 测试通过页面创建课程目标 知识 权重:25% ✏️ ✕" [ref=e135]:
            - generic [ref=e136] [cursor=pointer]:
              - img [ref=e138]
              - generic [ref=e140]:
                - generic [ref=e142]: OBJ-04
                - generic [ref=e143]: 测试通过页面创建课程目标
                - generic [ref=e145]: 知识
                - generic [ref=e147]: 权重:25%
                - button "✏️" [ref=e148]:
                  - generic [ref=e149]: ✏️
                - button "✕" [ref=e150]:
                  - generic [ref=e151]: ✕
          - treeitem "OBJ-02 具备软件系统设计与开发能力 能力 权重:35% ✏️ ✕" [ref=e152]:
            - generic [ref=e153] [cursor=pointer]:
              - img [ref=e155]
              - generic [ref=e157]:
                - generic [ref=e159]: OBJ-02
                - generic [ref=e160]: 具备软件系统设计与开发能力
                - generic [ref=e162]: 能力
                - generic [ref=e164]: 权重:35%
                - button "✏️" [ref=e165]:
                  - generic [ref=e166]: ✏️
                - button "✕" [ref=e167]:
                  - generic [ref=e168]: ✕
          - treeitem "OBJ-03 培养团队协作与工程规范意识 素养 权重:25% ✏️ ✕" [ref=e169]:
            - generic [ref=e170] [cursor=pointer]:
              - img [ref=e172]
              - generic [ref=e174]:
                - generic [ref=e176]: OBJ-03
                - generic [ref=e177]: 培养团队协作与工程规范意识
                - generic [ref=e179]: 素养
                - generic [ref=e181]: 权重:25%
                - button "✏️" [ref=e182]:
                  - generic [ref=e183]: ✏️
                - button "✕" [ref=e184]:
                  - generic [ref=e185]: ✕
```

# Test source

```ts
  65  |     // 用户列不能为空
  66  |     expect(firstRow[0], `Git提交第1行用户列为空。全行数据: ${JSON.stringify(firstRow)}`).not.toBe('');
  67  |     // +行数列必须是数字
  68  |     const adds = parseInt(firstRow[2]);
  69  |     expect(isNaN(adds), `Git提交+行数不是数字: "${firstRow[2]}"`).toBe(false);
  70  |     // -行数列必须是数字
  71  |     const dels = parseInt(firstRow[3]);
  72  |     expect(isNaN(dels), `Git提交-行数不是数字: "${firstRow[3]}"`).toBe(false);
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
> 165 |     const text = await firstLabel.textContent();
      |                                   ^ Error: locator.textContent: Test timeout of 30000ms exceeded.
  166 |     expect(text.length, '树节点文字为空').toBeGreaterThan(0);
  167 |     // 应该包含类似 OBJ- 的编号
  168 |     expect(text, `树节点不包含OBJ编号: "${text}"`).toMatch(/OBJ|objective/i);
  169 |   });
  170 | 
  171 |   test('RENDER-06: 设置页用户管理表所有列非空', async ({ page }) => {
  172 |     await loginAndGo(page, '个人中心');
  173 |     await page.waitForTimeout(1000);
  174 | 
  175 |     const rows = await getTableRows(page, 0);
  176 |     if (rows.length === 0) return;
  177 | 
  178 |     const firstRow = rows[0];
  179 |     expect(firstRow.length).toBeGreaterThanOrEqual(3);
  180 |     expect(firstRow[0], '用户名列空').not.toBe('');
  181 |     expect(firstRow[1], '姓名列空').not.toBe('');
  182 |     expect(firstRow[2], '角色列空').not.toBe('');
  183 |   });
  184 | 
  185 | });
  186 | 
```