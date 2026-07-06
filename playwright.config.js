const { defineConfig, devices } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './e2e',
  timeout: 30000,
  retries: 1,
  workers: 1,
  reporter: [['html', { outputFolder: 'e2e-report' }], ['list']],
  use: {
    baseURL: 'http://localhost:5173',
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
    video: 'retain-on-failure',
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  ],
  webServer: [
    { command: 'cd obe-server && mvn spring-boot:run -q', port: 8989, reuseExistingServer: true, timeout: 30000 },
    { command: 'cd obe-web && npx vite --host', port: 5173, reuseExistingServer: true, timeout: 15000 },
  ],
});
