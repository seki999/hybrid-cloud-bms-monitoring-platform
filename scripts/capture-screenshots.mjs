import { chromium } from "playwright";
import { mkdir } from "node:fs/promises";
import { resolve } from "node:path";

const baseUrl = process.env.BMS_BASE_URL ?? "http://localhost:8080";
const output = resolve("docs/screenshots");
await mkdir(output, { recursive: true });

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage({ viewport: { width: 1440, height: 960 }, deviceScaleFactor: 1 });

async function capture(name, path, waitFor = "main") {
  const response = await page.goto(`${baseUrl}${path}`, { waitUntil: "networkidle" });
  if (!response?.ok()) throw new Error(`${path} returned HTTP ${response?.status()}`);
  await page.locator(waitFor).first().waitFor({ state: "visible" });
  await page.screenshot({ path: resolve(output, name), fullPage: true });
  console.log(`captured ${name} <- ${path}`);
}

await capture("01-login.png", "/login", ".login-page");
await page.getByLabel("ユーザー名").fill(process.env.BMS_SCREENSHOT_USER ?? "admin");
await page.getByLabel("パスワード").fill(process.env.BMS_SCREENSHOT_PASSWORD ?? "Admin123!");
await Promise.all([page.waitForURL("**/dashboard"), page.getByRole("button", { name: "ログイン" }).click()]);
await page.screenshot({ path: resolve(output, "02-dashboard.png"), fullPage: true });

const routes = [
  ["03-devices.png", "/devices"],
  ["04-device-detail.png", "/devices/1"],
  ["05-events.png", "/events"],
  ["06-event-detail.png", "/events/1"],
  ["07-alerts.png", "/alerts"],
  ["08-alert-detail.png", "/alerts/1"],
  ["09-syslog-history.png", "/history/syslog"],
  ["10-snmp-trap-history.png", "/history/snmp-trap"],
  ["11-system-status.png", "/system/status"],
  ["12-report.png", "/reports/trends"]
];
for (const [name, path] of routes) await capture(name, path);
await browser.close();
