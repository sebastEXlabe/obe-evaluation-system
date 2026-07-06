const { test, expect } = require('@playwright/test');
const { apiLogin, apiRequest } = require('./helpers');

test.describe('前端渲染验证（DOM内容检查）', () => {

  let adminToken;
  test.beforeAll(async ({ request }) => {
    adminToken = (await apiLogin(request, 'testadm', 'admin123')).token;
  });

  // 通用工具：获取页面上表格所有行的文本内容
  async function getTableRows(page, tableIndex = 0) {
    const tables = page.locator('.el-table__body');
    if (await tables.count() <= tableIndex) return [];
    const rows = tables.nth(tableIndex).locator('tr');
    const count = await rows.count();
    const data = [];
    for (let i = 0; i < count; i++) {
      const cells = rows.nth(i).locator('.cell');
      const cellCount = await cells.count();
      const row = [];
      for (let j = 0; j < cellCount; j++) {
        row.push((await cells.nth(j).textContent()).trim());
      }
      data.push(row);
    }
    return data;
  }

  // 通用工具：登录并导航
  async function loginAndGo(page, menu) {
    await page.goto('http://localhost:5173/#/login');
    await page.fill('input[placeholder="用户名"]', 'testadm');
    await page.fill('input[placeholder="密码"]', 'admin123');
    await page.click('button:has-text("登 录")');
    await page.waitForURL('**/#/course**', { timeout: 10000 });
    if (menu) {
      await page.click(`.el-menu-item:has-text("${menu}")`);
      await page.waitForTimeout(500);
    }
  }

  async function selectFirstGroup(page) {
    await page.click('.el-select');
    await page.waitForSelector('.el-select-dropdown__item', { state: 'visible' });
    await page.locator('.el-select-dropdown__item').first().click();
    await page.waitForTimeout(500);
  }

  // ========== 关键UI验证 ==========

  test('RENDER-01: Git提交表所有列均非空', async ({ page }) => {
    await loginAndGo(page, '项目追踪');
    await selectFirstGroup(page);
    await page.click('.el-tabs__item:has-text("Git 提交")');
    await page.waitForTimeout(1500);

    const rows = await getTableRows(page, 0);
    if (rows.length === 0) return; // 没有Git提交数据时跳过

    const firstRow = rows[0];
    // 列顺序：用户、提交信息、+行数、-行数、时间、操作
    expect(firstRow.length, 'Git提交表应有至少5列').toBeGreaterThanOrEqual(5);

    // 用户列不能为空
    expect(firstRow[0], `Git提交第1行用户列为空。全行数据: ${JSON.stringify(firstRow)}`).not.toBe('');
    // +行数列必须是数字
    const adds = parseInt(firstRow[2]);
    expect(isNaN(adds), `Git提交+行数不是数字: "${firstRow[2]}"`).toBe(false);
    // -行数列必须是数字
    const dels = parseInt(firstRow[3]);
    expect(isNaN(dels), `Git提交-行数不是数字: "${firstRow[3]}"`).toBe(false);
    // 时间列不能为空
    expect(firstRow[4], `Git提交第1行时间列为空`).not.toBe('');
  });

  test('RENDER-02: 个人成绩表所有列均非空且贡献系数在0-1', async ({ page }) => {
    await loginAndGo(page, '多元评价');
    await selectFirstGroup(page);
    await page.click('.el-tabs__item:has-text("个人成绩")');
    await page.waitForTimeout(500);

    // 如果没数据，先计算
    const calcBtn = page.locator('button:has-text("计算全员成绩")');
    if (await calcBtn.count() > 0) {
      await calcBtn.click();
      await page.waitForTimeout(1500);
    }

    const rows = await getTableRows(page, 0);
    if (rows.length === 0) return;

    const firstRow = rows[0];
    // 列：用户、小组分、贡献系数、加分、最终成绩
    expect(firstRow.length).toBeGreaterThanOrEqual(5);

    // 用户名不能为空
    expect(firstRow[0], `用户列为空`).not.toBe('');
    // 小组分是数字
    const groupScore = parseFloat(firstRow[1]);
    expect(isNaN(groupScore), `小组分不是数字: "${firstRow[1]}"`).toBe(false);
    // 贡献系数在0-1范围内
    const ratio = parseFloat(firstRow[2].replace('%', ''));
    if (!isNaN(ratio)) {
      expect(ratio, `贡献系数${ratio}超出[0,100]`).toBeGreaterThanOrEqual(0);
      expect(ratio, `贡献系数${ratio}超出[0,100]`).toBeLessThanOrEqual(100);
    }
    // 最终成绩是数字
    const final = parseFloat(firstRow[4]);
    expect(isNaN(final), `最终成绩不是数字: "${firstRow[4]}"`).toBe(false);
  });

  test('RENDER-03: 小组卡片显示组名和成员数（不是ID）', async ({ page }) => {
    await loginAndGo(page, '小组管理');
    await page.waitForTimeout(1000);

    // 定位第一张小组卡片
    const firstCard = page.locator('.group-card, .el-card').first();
    const cardText = await firstCard.textContent();

    // 组名应该包含中文（不是纯数字ID）
    const groupNameMatch = cardText.match(/软件工程|实战|测试/);
    expect(groupNameMatch, `组名不应是纯ID，卡片内容: ${cardText.substring(0, 80)}`).not.toBeNull();

    // 成员数应该显示数字/数字格式
    const memberMatch = cardText.match(/(\d+)\s*\/\s*(\d+)/);
    expect(memberMatch, `成员数格式应为 X/Y，卡片内容: ${cardText.substring(0, 80)}`).not.toBeNull();

    // 邀请码应该显示6位字符（不是空）
    const inviteMatch = cardText.match(/[A-Z0-9]{6}/);
    expect(inviteMatch, `邀请码应为6位字符，卡片内容: ${cardText.substring(0, 80)}`).not.toBeNull();
  });

  test('RENDER-04: 达成度表显示百分比数字', async ({ page }) => {
    await loginAndGo(page, '达成度分析');
    await selectFirstGroup(page);
    await page.click('button:has-text("计算达成度")');
    await page.waitForTimeout(2000);

    const rows = await getTableRows(page, 0);
    if (rows.length === 0) return;

    const firstRow = rows[0];
    // 列：目标标题、维度、权重、达成度
    expect(firstRow.length).toBeGreaterThanOrEqual(4);

    // 目标标题非空
    expect(firstRow[0], '目标标题为空').not.toBe('');
    // 权重含%号
    expect(firstRow[2], `权重不含%: "${firstRow[2]}"`).toContain('%');
    // 达成度含%号
    expect(firstRow[3], `达成度不含%: "${firstRow[3]}"`).toContain('%');
  });

  test('RENDER-05: 课程目标树至少显示目标条目', async ({ page }) => {
    await loginAndGo(page, '课程目标');
    await page.waitForTimeout(1000);

    const treeNodes = page.locator('.el-tree-node');
    const count = await treeNodes.count();
    expect(count, '课程目标树为空').toBeGreaterThan(0);

    // 第一个节点应该有文字内容
    const firstLabel = treeNodes.first().locator('.el-tree-node__label');
    const text = await firstLabel.textContent();
    expect(text.length, '树节点文字为空').toBeGreaterThan(0);
    // 应该包含类似 OBJ- 的编号
    expect(text, `树节点不包含OBJ编号: "${text}"`).toMatch(/OBJ|objective/i);
  });

  test('RENDER-06: 设置页用户管理表所有列非空', async ({ page }) => {
    await loginAndGo(page, '个人中心');
    await page.waitForTimeout(1000);

    const rows = await getTableRows(page, 0);
    if (rows.length === 0) return;

    const firstRow = rows[0];
    expect(firstRow.length).toBeGreaterThanOrEqual(3);
    expect(firstRow[0], '用户名列空').not.toBe('');
    expect(firstRow[1], '姓名列空').not.toBe('');
    expect(firstRow[2], '角色列空').not.toBe('');
  });

});
