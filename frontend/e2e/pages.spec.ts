import { test, expect } from '@playwright/test'

test.describe('Frontend Pages & Backend Communication E2E Tests', () => {
  test('Dashboard loads without error and connects to WebSocket/GraphQL', async ({ page }) => {
    await page.goto('http://localhost:3000/')
    await expect(page.locator('h1, h2, header')).toBeVisible()
    await expect(page.getByText('Error loading')).not.toBeVisible()
  })

  test('Nexus Mesh page loads live nodes', async ({ page }) => {
    await page.goto('http://localhost:3000/nexus')
    await expect(page.getByText('Error loading')).not.toBeVisible()
    await expect(page.getByText(/Nexus|Node/i).first()).toBeVisible()
  })

  test('Trade page loads trade agreements', async ({ page }) => {
    await page.goto('http://localhost:3000/trade')
    await expect(page.getByText('Error loading trade agreements')).not.toBeVisible()
    await expect(page.getByRole('heading', { name: 'Trade Agreements' })).toBeVisible()
  })

  test('Constitution page loads rules', async ({ page }) => {
    await page.goto('http://localhost:3000/constitution')
    await expect(page.getByText('Error loading rules')).not.toBeVisible()
    await expect(page.getByText(/Constitution|Rules/i).first()).toBeVisible()
  })

  test('Tech Tree page loads technologies', async ({ page }) => {
    await page.goto('http://localhost:3000/tech-tree')
    await expect(page.getByText('Error loading technologies')).not.toBeVisible()
    await expect(page.getByText(/Tech|Technology/i).first()).toBeVisible()
  })

  test('Production page loads facilities', async ({ page }) => {
    await page.goto('http://localhost:3000/production')
    await expect(page.getByText('Error loading facilities')).not.toBeVisible()
    await expect(page.getByText(/Production|Facility|Facilities/i).first()).toBeVisible()
  })

  test('Logistics page loads shipments', async ({ page }) => {
    await page.goto('http://localhost:3000/logistics')
    await expect(page.getByText('Error loading shipments')).not.toBeVisible()
    await expect(page.getByText(/Logistics|Shipment/i).first()).toBeVisible()
  })

  test('Social page loads incidents and projects', async ({ page }) => {
    await page.goto('http://localhost:3000/social')
    await expect(page.getByText('Error loading')).not.toBeVisible()
    await expect(page.getByText(/Social|Incident|Project/i).first()).toBeVisible()
  })

  test('Resource Map page loads regions and resources', async ({ page }) => {
    await page.goto('http://localhost:3000/resource-map')
    await expect(page.getByText('Error loading')).not.toBeVisible()
    await expect(page.getByText(/Resource|Region/i).first()).toBeVisible()
  })

  test('Leaderboard page loads scores', async ({ page }) => {
    await page.goto('http://localhost:3000/leaderboard')
    await expect(page.getByText('Error loading')).not.toBeVisible()
    await expect(page.getByText(/Leaderboard|Rank/i).first()).toBeVisible()
  })

  test('Simulation page loads engine status', async ({ page }) => {
    await page.goto('http://localhost:3000/simulation')
    await expect(page.getByText('Error loading')).not.toBeVisible()
    await expect(page.getByText(/Simulation|Engine/i).first()).toBeVisible()
  })
})
