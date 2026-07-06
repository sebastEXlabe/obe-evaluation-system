const { expect } = require('@playwright/test');

const BASE = 'http://localhost:5173';
const API = 'http://localhost:8989/api';

// Helper: login via API and store token
async function apiLogin(request, username, password) {
  const res = await request.post(`${API}/auth/login`, {
    data: { username, password },
    headers: { 'Content-Type': 'application/json' }
  });
  const body = await res.json();
  if (body.code !== 200) throw new Error(`Login failed: ${body.message}`);
  return body.data;
}

// Helper: login via UI
async function uiLogin(page, username, password) {
  await page.goto(`${BASE}/#/login`);
  await page.fill('input[placeholder="用户名"]', username);
  await page.fill('input[placeholder="密码"]', password);
  await page.click('button:has-text("登 录")');
  await page.waitForURL('**/#/course**', { timeout: 10000 });
}

// Helper: navigate via sidebar menu
async function navigateTo(page, menuText) {
  await page.click(`.el-menu-item:has-text("${menuText}")`);
  await page.waitForLoadState('networkidle');
}

// Helper: select dropdown option by text
async function selectOption(page, selector, text) {
  await page.click(selector);
  await page.waitForSelector('.el-select-dropdown__item', { state: 'visible' });
  await page.click(`.el-select-dropdown__item:has-text("${text}")`);
}

// Helper: wait for loading to finish
async function waitForLoad(page) {
  await page.waitForFunction(() => !document.querySelector('.el-loading-mask'), { timeout: 10000 }).catch(() => {});
}

// Helper: check no visible error messages
async function assertNoErrors(page) {
  const errors = await page.locator('.el-message--error').allTextContents();
  expect(errors.length).toBe(0);
}

// Helper: API request with auth
async function apiRequest(request, token, method, path, data) {
  return request[method](`${API}${path}`, {
    data,
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }
  });
}

module.exports = { apiLogin, uiLogin, navigateTo, selectOption, waitForLoad, assertNoErrors, apiRequest, BASE, API };
