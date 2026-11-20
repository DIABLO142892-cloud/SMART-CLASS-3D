const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

const base = 'http://127.0.0.1:8080/smartclass3d';
const output = path.join(__dirname, '..', 'docs', 'screenshots');
fs.mkdirSync(output, { recursive: true });

async function verifyRole(browser, role, email, password) {
  const context = await browser.newContext({ viewport: { width: 1440, height: 960 }, colorScheme: 'light' });
  const page = await context.newPage();
  page.setDefaultTimeout(15000);
  const errors = [];
  page.on('console', message => { if (message.type() === 'error') errors.push(`console: ${message.text()}`); });
  page.on('pageerror', error => errors.push(`page: ${error.message}`));
  page.on('requestfailed', request => errors.push(`request: ${request.method()} ${request.url()} ${request.failure()?.errorText}`));

  await page.goto(`${base}/login`, { waitUntil: 'networkidle' });
  console.log(`CHECK ${role}: login page loaded`);
  if (role === 'STUDENT') await page.screenshot({ path: path.join(output, '01-login.png'), fullPage: true });
  await page.selectOption('select[name="role"]', role);
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', password);
  await Promise.all([
    page.waitForURL('**/dashboard'),
    page.click('button[type="submit"]'),
  ]);
  console.log(`CHECK ${role}: authenticated`);
  await page.waitForFunction(() => document.querySelector('#sync-state')?.textContent.includes('Live'));
  await page.waitForSelector('#room-grid .room-card', { state: 'attached' });
  const roomCount = await page.locator('#room-grid .room-card').count();
  if (roomCount < 16) throw new Error(`${role}: expected at least 16 room cards, got ${roomCount}`);
  const canvas = await page.locator('#campus-canvas').boundingBox();
  if (!canvas || canvas.width < 300 || canvas.height < 250) throw new Error(`${role}: campus canvas did not render`);

  if (role === 'STUDENT') {
    if (await page.locator('[data-view="admin"]').isVisible()) throw new Error('Student can see Administration navigation');
    await page.screenshot({ path: path.join(output, '02-student-dashboard.png'), fullPage: true });
    await page.click('[data-view="finder"]');
    await page.fill('#search', 'A-101');
    if (await page.locator('#room-grid .room-card').count() !== 1) throw new Error('Classroom search filter failed');
    await page.waitForTimeout(350);
    await page.screenshot({ path: path.join(output, '03-classroom-finder.png'), fullPage: true });
  }
  if (role === 'LECTURER') {
    await page.click('[data-view="finder"]');
    if (await page.locator('[data-status-room]').count() === 0) throw new Error('Lecturer status controls are missing');
    await page.waitForTimeout(350);
    await page.screenshot({ path: path.join(output, '04-lecturer-dashboard.png'), fullPage: true });
  }
  if (role === 'ADMIN') {
    if (!(await page.locator('[data-view="admin"]').isVisible())) throw new Error('Admin navigation is hidden');
    await page.click('[data-view="admin"]');
    await page.waitForSelector('#admin-panel tbody tr');
    await page.click('[data-admin="auditLogs"]');
    await page.waitForSelector('#admin-panel tbody tr');
    await page.waitForTimeout(350);
    await page.screenshot({ path: path.join(output, '05-admin-dashboard.png'), fullPage: true });
  }

  if (errors.length) throw new Error(`${role} browser errors:\n${errors.join('\n')}`);
  console.log(`PASS  ${role.toLowerCase()} browser: ${roomCount} live rooms, no console or request errors`);
  await context.close();
}

(async () => {
  const browser = await chromium.launch({
    headless: true,
    executablePath: process.env.CHROME_PATH || 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
  });
  try {
    await verifyRole(browser, 'STUDENT', 'student@smartclass.com', 'Student@123');
    await verifyRole(browser, 'LECTURER', 'lecturer@smartclass.com', 'Lecturer@123');
    await verifyRole(browser, 'ADMIN', 'admin@smartclass.com', 'Admin@123');
    console.log('RESULT: visual and browser behavior QA completed successfully.');
  } finally {
    await browser.close();
  }
})().catch(error => { console.error(`FAIL  ${error.stack || error}`); process.exit(1); });
