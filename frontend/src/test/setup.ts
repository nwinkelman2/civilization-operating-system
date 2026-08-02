import '@testing-library/jest-dom'

// jsdom doesn't implement matchMedia. Provide a minimal mock so components
// that check viewport width (e.g. App's mobile-nav breakpoint tracking)
// don't crash in tests. Defaults to "not matching" (desktop-sized).
if (typeof window !== 'undefined' && !window.matchMedia) {
  window.matchMedia = (query: string): MediaQueryList => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: () => {},
    removeListener: () => {},
    addEventListener: () => {},
    removeEventListener: () => {},
    dispatchEvent: () => false,
  })
}
