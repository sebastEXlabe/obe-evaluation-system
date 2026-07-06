# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: rendering-validation.spec.js >> 前端渲染验证（DOM内容检查） >> RENDER-02: 个人成绩表所有列均非空且贡献系数在0-1
- Location: e2e\rendering-validation.spec.js:77:3

# Error details

```
Error: expect(received).toBeGreaterThanOrEqual(expected)

Expected: >= 5
Received:    4
```

# Page snapshot

```yaml
- generic [ref=e1]:
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
          - link "多元评价" [ref=e63]
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
              - tab "小组评价" [ref=e86]
              - tab "角色评价" [ref=e87]
              - tab "个人成绩" [selected] [ref=e88]
            - tabpanel "个人成绩" [ref=e90]:
              - button "计算全员成绩" [active] [ref=e92] [cursor=pointer]:
                - generic [ref=e93]: 计算全员成绩
              - generic [ref=e95]:
                - table [ref=e97]:
                  - rowgroup [ref=e104]:
                    - row "用户 小组分 贡献系数 加分 最终成绩" [ref=e105]:
                      - columnheader "用户" [ref=e106]:
                        - generic [ref=e107]: 用户
                      - columnheader "小组分" [ref=e108]:
                        - generic [ref=e109]: 小组分
                      - columnheader "贡献系数" [ref=e110]:
                        - generic [ref=e111]: 贡献系数
                      - columnheader "加分" [ref=e112]:
                        - generic [ref=e113]: 加分
                      - columnheader "最终成绩" [ref=e114]:
                        - generic [ref=e115]: 最终成绩
                - table [ref=e120]:
                  - rowgroup [ref=e127]:
                    - row "测试学生 66.33 1 0 0.66" [ref=e128]:
                      - cell "测试学生" [ref=e129]:
                        - generic [ref=e130]: 测试学生
                      - cell "66.33" [ref=e131]:
                        - generic [ref=e132]: "66.33"
                      - cell "1" [ref=e133]:
                        - progressbar [ref=e135]:
                          - generic [ref=e139]: 1%
                      - cell "0" [ref=e140]:
                        - generic [ref=e141]: "0"
                      - cell "0.66" [ref=e142]:
                        - generic [ref=e143]: "0.66"
                    - row "测试学生 66.33 1 0 0.66" [ref=e144]:
                      - cell "测试学生" [ref=e145]:
                        - generic [ref=e146]: 测试学生
                      - cell "66.33" [ref=e147]:
                        - generic [ref=e148]: "66.33"
                      - cell "1" [ref=e149]:
                        - progressbar [ref=e151]:
                          - generic [ref=e155]: 1%
                      - cell "0" [ref=e156]:
                        - generic [ref=e157]: "0"
                      - cell "0.66" [ref=e158]:
                        - generic [ref=e159]: "0.66"
                    - row "李明 66.33 1 0 0.66" [ref=e160]:
                      - cell "李明" [ref=e161]:
                        - generic [ref=e162]: 李明
                      - cell "66.33" [ref=e163]:
                        - generic [ref=e164]: "66.33"
                      - cell "1" [ref=e165]:
                        - progressbar [ref=e167]:
                          - generic [ref=e171]: 1%
                      - cell "0" [ref=e172]:
                        - generic [ref=e173]: "0"
                      - cell "0.66" [ref=e174]:
                        - generic [ref=e175]: "0.66"
                    - row "系统管理员 66.33 99.33999999999999 1.45 67.35" [ref=e176]:
                      - cell "系统管理员" [ref=e177]:
                        - generic [ref=e178]: 系统管理员
                      - cell "66.33" [ref=e179]:
                        - generic [ref=e180]: "66.33"
                      - cell "99.33999999999999" [ref=e181]:
                        - progressbar [ref=e183]:
                          - generic [ref=e187]: 99%
                      - cell "1.45" [ref=e188]:
                        - generic [ref=e189]: "1.45"
                      - cell "67.35" [ref=e190]:
                        - generic [ref=e191]: "67.35"
  - alert [ref=e193]:
    - img [ref=e195]
    - paragraph [ref=e197]: 成绩计算完成
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
> 95  |     expect(firstRow.length).toBeGreaterThanOrEqual(5);
      |                             ^ Error: expect(received).toBeGreaterThanOrEqual(expected)
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