#!/usr/bin/env node
// 兼容 HttpRunner YAML 格式的测试运行器
// 用法: node run-yaml-tests.js data-driven-endpoints.yaml

const fs = require('fs');
const yaml = require('js-yaml');

const file = process.argv[2];
if (!file) { console.error('Usage: node run-yaml-tests.js <test.yaml>'); process.exit(1); }

const testcase = yaml.load(fs.readFileSync(file, 'utf8'));
const config = testcase.config || {};
const base_url = (config.base_url || 'http://localhost:8989').replace(/\/$/, '');
const variables = config.variables || {};
const teststeps = testcase.teststeps || [];

// 内置变量
const builtins = { timestamp: String(Date.now()), random: String(Math.floor(Math.random() * 9000 + 1000)) };

let pass = 0, fail = 0;

async function run() {
  for (const step of teststeps) {
    const req = step.request || {};
    let url = req.url;

    // 统一变量替换函数
    const resolveVar = (name) => variables[name] ?? builtins[name] ?? '';
    const replaceVars = (str) => str.replace(/\$(\w+)/g, (_, name) => String(resolveVar(name)));

    // 替换URL中的$变量
    url = replaceVars(url);
    // URL拼接：兼容 $base 变量和 base_url
    if (url.startsWith('$base')) {
      url = url.replace('$base', base_url);
    } else if (url.startsWith('/')) {
      url = base_url + url;
    }

    const method = (req.method || 'GET').toUpperCase();
    const headers = { ...(config.headers || {}), ...(req.headers || {}) };

    // 替换headers中的$变量
    for (const [k, v] of Object.entries(headers)) {
      if (typeof v === 'string') headers[k] = replaceVars(v);
    }

    let body = req.json ? JSON.stringify(req.json) : (req.body || req.data);
    // 替换body中的$变量
    if (body) body = replaceVars(body);
    if (req.json && !headers['Content-Type']) headers['Content-Type'] = 'application/json';

    try {
      const start = Date.now();
      const res = await fetch(url, { method, headers, body: body || undefined });
      const resText = await res.text();
      let resJson;
      try { resJson = JSON.parse(resText); } catch { resJson = resText; }

      const checkCtx = {
        status_code: res.status,
        headers: Object.fromEntries(res.headers.entries()),
        body: resJson
      };

      const assertions = step.validate || [];
      let stepPass = true;
      for (const a of assertions) {
        const [type, args] = Object.entries(a)[0];
        const [checkPath, expected] = args;
        const actual = resolvePath(checkCtx, checkPath);

        let ok = false;
        switch (type) {
          case 'eq': case 'equals': ok = actual === expected; break;
          case 'lt': case 'less_than': ok = actual < expected; break;
          case 'le': case 'less_or_equals': ok = actual <= expected; break;
          case 'gt': case 'greater_than': ok = actual > expected; break;
          case 'ge': case 'greater_or_equals': ok = actual >= expected; break;
          case 'ne': case 'not_equals': ok = actual !== expected; break;
          case 'contains': ok = String(actual).includes(expected); break;
          case 'type_match': ok = typeCheck(actual, expected); break;
          case 'len_gt': case 'length_greater_than': ok = (actual?.length || 0) > expected; break;
          case 'len_ge': ok = (actual?.length || 0) >= expected; break;
          case 'startswith': ok = String(actual).startsWith(expected); break;
          default: ok = actual === expected;
        }

        if (!ok) {
          console.log(`  FAIL [${type}] ${checkPath} expected=${expected} actual=${JSON.stringify(actual)}`);
          stepPass = false;
        } else {
          // console.log(`  PASS [${type}] ${checkPath} = ${JSON.stringify(actual)}`);
        }
      }

      // Extract variables
      const extract = step.extract || {};
      for (const [varName, jmesPath] of Object.entries(extract)) {
        variables[varName] = resolvePath(checkCtx, jmesPath);
      }

      console.log(`${stepPass ? 'PASS' : 'FAIL'} ${method} ${url} [${res.status} ${Date.now()-start}ms]`);
      stepPass ? pass++ : fail++;
    } catch (e) {
      console.log(`ERROR ${method} ${url}: ${e.message}`);
      fail++;
    }
  }

  console.log(`\n${pass} passed, ${fail} failed, ${pass+fail} total`);
  process.exit(fail > 0 ? 1 : 0);
}

function resolvePath(ctx, path) {
  if (!path) return undefined;
  // 顶层key直接查
  if (path === 'status_code') return ctx.status_code;
  if (path.startsWith('headers.')) {
    const headerKey = path.substring(8).replace(/^"/, '').replace(/"$/, '');
    return ctx.headers[headerKey.toLowerCase()];
  }
  // body路径：逐层深入
  const parts = path.replace(/^body\./, '').split('.');
  let val = ctx.body;
  for (const p of parts) {
    if (val == null) return undefined;
    if (typeof val === 'object') val = val[p];
    else return undefined;
  }
  return val;
}

function typeCheck(val, type) {
  if (type === 'str') return typeof val === 'string';
  if (type === 'int') return typeof val === 'number' && Number.isInteger(val);
  if (type === 'float') return typeof val === 'number';
  if (type === 'list') return Array.isArray(val);
  if (type === 'bool') return typeof val === 'boolean';
  return true;
}

run().catch(e => { console.error(e); process.exit(1); });
