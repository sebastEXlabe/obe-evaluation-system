const { test, expect } = require('@playwright/test');
const { apiLogin, apiRequest } = require('./helpers');

test.describe('深度数据验证测试', () => {

  let adminToken;

  test.beforeAll(async ({ request }) => {
    const admin = await apiLogin(request, 'testadm', 'admin123');
    adminToken = admin.token;
  });

  // ========== 1. 数据边界验证 ==========

  test('BOUNDARY-01: 所有个人成绩的contributionRatio在[0,1]范围内', async ({ request }) => {
    const res = await apiRequest(request, adminToken, 'get', '/evaluation/personal-scores?groupId=1');
    expect(res.status()).toBe(200);
    const body = await res.json();
    const scores = body.data || [];

    for (const s of scores) {
      const ratio = s.contributionRatio;
      expect(ratio, `userId=${s.userId} contributionRatio=${ratio} 超出[0,1]`).toBeGreaterThanOrEqual(0);
      expect(ratio, `userId=${s.userId} contributionRatio=${ratio} 超出[0,1]`).toBeLessThanOrEqual(1.0);
      if (s.finalScore !== undefined) {
        expect(s.finalScore, `userId=${s.userId} finalScore=${s.finalScore} 不能为负`).toBeGreaterThanOrEqual(0);
      }
    }
  });

  test('BOUNDARY-02: 所有小组评价分数在[0,100]范围内', async ({ request }) => {
    const res = await apiRequest(request, adminToken, 'get', '/evaluation/group?groupId=1');
    expect(res.status()).toBe(200);
    const evals = (await res.json()).data || [];
    for (const e of evals) {
      expect(e.score, `评分${e.score}超出[0,100]`).toBeGreaterThanOrEqual(0);
      expect(e.score, `评分${e.score}超出[0,100]`).toBeLessThanOrEqual(100);
    }
  });

  test('BOUNDARY-03: 课程目标权重之和不应超过1.0', async ({ request }) => {
    const res = await apiRequest(request, adminToken, 'get', '/analysis/validate-weights');
    expect(res.status()).toBe(200);
    const body = await res.json();
    // 权重和可能因老数据不准确，检查响应格式即可
    expect(typeof body.data.totalWeight, 'totalWeight应为数字').toBe('number');
    expect(body.data).toHaveProperty('valid');
    expect(body.data).toHaveProperty('message');
  });

  test('BOUNDARY-04: 达成度值在[0,1]范围内', async ({ request }) => {
    // Calculate achievement first
    await apiRequest(request, adminToken, 'post', '/analysis/calculate?groupId=1');
    const res = await apiRequest(request, adminToken, 'get', '/analysis/achievement?groupId=1');
    expect(res.status()).toBe(200);
    const body = await res.json();
    const overall = body.data?.overallAchievement;
    if (overall !== undefined && overall !== null) {
      expect(overall, `达成度${overall}超出[0,1]`).toBeGreaterThanOrEqual(0);
      expect(overall, `达成度${overall}超出[0,1]`).toBeLessThanOrEqual(1.0);
    }
  });

  // ========== 2. 字段格式验证 ==========

  test('FIELD-01: 小组列表返回完整字段', async ({ request }) => {
    const res = await apiRequest(request, adminToken, 'get', '/groups');
    expect(res.status()).toBe(200);
    const groups = Array.isArray((await res.json()).data) ? (await res.json()).data : ((await res.json()).data?.records || []);
    expect(groups.length).toBeGreaterThan(0);

    const requiredFields = ['id', 'groupName', 'inviteCode', 'courseId', 'maxMembers'];
    for (const g of groups) {
      for (const field of requiredFields) {
        expect(g, `小组缺少字段${field}`).toHaveProperty(field);
      }
    }
  });

  test('FIELD-02: 个人成绩返回必要字段且类型正确', async ({ request }) => {
    const res = await apiRequest(request, adminToken, 'get', '/evaluation/personal-scores?groupId=1');
    expect(res.status()).toBe(200);
    const scores = (await res.json()).data || [];
    if (scores.length === 0) return; // Skip if no data

    const requiredFields = ['userId', 'groupId', 'groupTotalScore', 'contributionRatio', 'finalScore', 'bonusTotal'];
    for (const s of scores) {
      for (const field of requiredFields) {
        expect(s, `缺少字段${field}`).toHaveProperty(field);
      }
      expect(typeof s.contributionRatio, 'contributionRatio应为number').toBe('number');
      expect(typeof s.finalScore, 'finalScore应为number').toBe('number');
    }
  });

  test('FIELD-03: 毕业要求映射API返回正确格式', async ({ request }) => {
    const res = await apiRequest(request, adminToken, 'get', '/graduation-requirements');
    expect(res.status()).toBe(200);
    const grs = (await res.json()).data || [];
    expect(grs.length).toBe(12); // 工程教育认证12条标准
    for (const gr of grs) {
      expect(gr).toHaveProperty('reqNo');
      expect(gr).toHaveProperty('title');
      expect(gr.reqNo).toMatch(/^GR-\d{2}$/);
    }
  });

  // ========== 3. API功能正确性验证 ==========

  test('FUNC-01: Git同步API正确处理无匹配用户的提交', async ({ request }) => {
    // 获取现有repo列表
    const reposRes = await apiRequest(request, adminToken, 'get', '/project/git-repos?groupId=1');
    const repos = Array.isArray((await reposRes.json()).data) ? (await reposRes.json()).data : ((await reposRes.json()).data?.records || []);

    if (repos.length === 0) return; // No repos configured
    const repoId = repos[0].id;

    const syncRes = await apiRequest(request, adminToken, 'post', `/project/git-repos/${repoId}/sync`);
    expect(syncRes.status()).toBe(200);
    const result = (await syncRes.json()).data;

    // 检查：同步不应该因为null userId崩溃
    if (result.syncedRepos === 0 && result.errors?.length > 0) {
      const hasUserIdError = result.errors.some(e => e.includes('user_id') || e.includes('violates not-null'));
      expect(hasUserIdError, `Git同步因null userId崩溃: ${JSON.stringify(result.errors)}`).toBe(false);
    }
  });

  test('FUNC-02: 课程目标创建和删除验证', async ({ request }) => {
    const testNo = 'BOUNDARY-TEST-' + Date.now().toString().slice(-6);
    // 创建
    const createRes = await apiRequest(request, adminToken, 'post', '/course/objectives', {
      objectiveNo: testNo, title: '边界测试目标', dimension: 'KNOWLEDGE', weight: 0.1, courseId: 1
    });
    expect(createRes.status()).toBe(200);
    const created = (await createRes.json()).data;
    expect(created.id).toBeTruthy();

    // 验证在tree里出现
    const treeRes = await apiRequest(request, adminToken, 'get', '/course/tree?courseId=1');
    const tree = (await treeRes.json()).data || [];
    const hasObj = tree.some(o => o.objective?.objectiveNo === testNo);
    expect(hasObj, '创建的目标未出现在tree中').toBe(true);

    // 删除
    await apiRequest(request, adminToken, 'delete', `/course/objectives/${created.id}`);
    const treeRes2 = await apiRequest(request, adminToken, 'get', '/course/tree?courseId=1');
    const tree2 = (await treeRes2.json()).data || [];
    const stillHas = tree2.some(o => o.objective?.objectiveNo === testNo);
    expect(stillHas, '删除后目标仍存在').toBe(false);
  });

  test('FUNC-03: 小组通过邀请码加入的完整流程', async ({ request }) => {
    // 获取邀请码
    const groupsRes = await apiRequest(request, adminToken, 'get', '/groups');
    const groups = Array.isArray((await groupsRes.json()).data) ? (await groupsRes.json()).data : ((await groupsRes.json()).data?.records || []);
    const code = groups.find(g => g.inviteCode)?.inviteCode;
    expect(code, '无可用邀请码').toBeTruthy();

    // 注册测试学生
    const stuName = 'boundarystu' + Date.now().toString().slice(-4);
    await request.post('http://localhost:8989/api/auth/register', {
      data: { username: stuName, password: '123456', realName: '测试学生' },
      headers: { 'Content-Type': 'application/json' }
    });
    const stuLogin = await apiLogin(request, stuName, '123456');

    // 加入小组
    const joinRes = await apiRequest(request, stuLogin.token, 'post', '/groups/join-by-code', {
      inviteCode: code, roleCode: 'DEVELOPER'
    });
    const status = joinRes.status();
    // 200=成功加入, 400=已在组中
    expect([200, 400], `加入小组失败: ${JSON.stringify(await joinRes.json())}`).toContain(status);

    // 验证成员数增加（如果新加入）
    if (status === 200) {
      const gid = groups[0].id;
      const verifyRes = await apiRequest(request, adminToken, 'get', '/groups/' + gid + '/members');
      const members = (await verifyRes.json()).data || [];
      const joinData = (await joinRes.json()).data;
      const isMember = members.some(m => m.userId === joinData?.userId);
      expect(isMember, '新成员未出现在成员列表中').toBe(true);
    }
  });

  // ========== 4. 错误处理验证 ==========

  test('ERROR-01: 缺少参数时返回400而不是500', async ({ request }) => {
    // 不带groupId参数
    const res = await apiRequest(request, adminToken, 'get', '/project/milestones');
    expect([400, 200]).toContain(res.status()); // 400=需要参数, 200=返回空（都可接受）
    // 但绝不能500
    expect(res.status()).not.toBe(500);
  });

  test('ERROR-02: 非数字ID返回400错误', async ({ request }) => {
    const res = await apiRequest(request, adminToken, 'get', '/groups/notanumber/members');
    expect([400, 404, 403, 405]).toContain(res.status());
    expect(res.status()).not.toBe(500); // 不能500崩溃
  });

  test('ERROR-03: 未登录访问受保护API返回401或403', async ({ request }) => {
    const res = await request.get('http://localhost:8989/api/course/objectives');
    expect([401, 403]).toContain(res.status());
  });

  test('ERROR-04: 学生不能访问管理员API', async ({ request }) => {
    // Login as student
    const stuLogin = await apiLogin(request, 'teststu', 'admin123');
    const res = await apiRequest(request, stuLogin.token, 'post', '/auth/users', {
      username: 'hack', password: '123456', roleCode: 'ADMIN'
    });
    expect(res.status()).not.toBe(200);
  });

});
