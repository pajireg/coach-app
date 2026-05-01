// tokens.jsx — Running Coach design tokens
// Theme-aware via window.RC_THEME ('dark' | 'light'). Consumers should
// read useTheme() or pass theme down; tokens() returns the active palette.

const RC_PALETTES = {
  dark: {
    bg: '#0B1220',
    bgGrad: 'radial-gradient(120% 80% at 20% 0%, #1A2548 0%, #0B1220 55%, #050811 100%)',
    bgElev: '#121A2B',
    bgElev2: '#1A2236',
    // Liquid glass materials — layered translucency
    card: 'rgba(28,38,60,0.55)',
    cardHi: 'rgba(38,50,78,0.65)',
    glass: 'rgba(18,26,44,0.55)',
    glassStrong: 'rgba(22,32,54,0.72)',
    glassTint: 'rgba(255,255,255,0.06)',
    border: 'rgba(255,255,255,0.10)',
    borderStrong: 'rgba(255,255,255,0.18)',
    hairline: 'rgba(255,255,255,0.22)',
    text: '#F5F3EE',
    textDim: 'rgba(245,243,238,0.72)',
    textFaint: 'rgba(245,243,238,0.50)',
    textMuted: 'rgba(245,243,238,0.32)',
    divider: 'rgba(255,255,255,0.08)',
    shadow: '0 10px 40px rgba(0,0,0,0.35)',
    glassShadow: '0 20px 60px rgba(0,0,0,0.45), 0 1px 0 rgba(255,255,255,0.08) inset, 0 0 0 0.5px rgba(255,255,255,0.12) inset',
  },
  light: {
    bg: '#F2EFE8',
    bgGrad: 'radial-gradient(120% 80% at 20% 0%, #FFFFFF 0%, #F2EFE8 55%, #E6E2D8 100%)',
    bgElev: '#FFFFFF',
    bgElev2: '#FAF8F3',
    // Liquid glass — light: frosted white with warm tint
    card: 'rgba(255,255,255,0.62)',
    cardHi: 'rgba(255,255,255,0.78)',
    glass: 'rgba(255,255,255,0.55)',
    glassStrong: 'rgba(255,255,255,0.78)',
    glassTint: 'rgba(255,255,255,0.35)',
    border: 'rgba(11,18,32,0.08)',
    borderStrong: 'rgba(11,18,32,0.16)',
    hairline: 'rgba(255,255,255,0.9)',
    text: '#0B1220',
    textDim: 'rgba(11,18,32,0.68)',
    textFaint: 'rgba(11,18,32,0.46)',
    textMuted: 'rgba(11,18,32,0.30)',
    divider: 'rgba(11,18,32,0.08)',
    shadow: '0 10px 30px rgba(11,18,32,0.08)',
    glassShadow: '0 16px 48px rgba(11,18,32,0.12), 0 1px 0 rgba(255,255,255,0.9) inset, 0 0 0 0.5px rgba(255,255,255,0.7) inset',
  },
};

// Zone accents — same across themes, tuned to look right on both.
const RC_ZONES = {
  recovery:  { key: 'recovery',  label: 'Recovery',  ko: '회복',   color: '#4A9EDB', soft: 'rgba(74,158,219,0.14)',  softStrong: 'rgba(74,158,219,0.24)' },
  base:      { key: 'base',      label: 'Base',      ko: '베이스', color: '#3FB87F', soft: 'rgba(63,184,127,0.14)',  softStrong: 'rgba(63,184,127,0.24)' },
  threshold: { key: 'threshold', label: 'Threshold', ko: '역치',   color: '#E8A94C', soft: 'rgba(232,169,76,0.16)',  softStrong: 'rgba(232,169,76,0.26)' },
  interval:  { key: 'interval',  label: 'Interval',  ko: '인터벌', color: '#E35D5D', soft: 'rgba(227,93,93,0.14)',   softStrong: 'rgba(227,93,93,0.24)' },
  rest:      { key: 'rest',      label: 'Rest',      ko: '휴식',   color: '#8A8F99', soft: 'rgba(138,143,153,0.14)', softStrong: 'rgba(138,143,153,0.24)' },
  long:      { key: 'long',      label: 'Long',      ko: '장거리', color: '#8F7BD4', soft: 'rgba(143,123,212,0.14)', softStrong: 'rgba(143,123,212,0.24)' },
};

const RC_FONT_UI = '"Pretendard Variable", Pretendard, -apple-system, BlinkMacSystemFont, system-ui, "Segoe UI", Roboto, sans-serif';
const RC_FONT_MONO = '"JetBrains Mono", "SF Mono", ui-monospace, Menlo, monospace';

function rcTokens(theme) {
  return RC_PALETTES[theme] || RC_PALETTES.dark;
}

// Global font injection (once)
if (typeof document !== 'undefined' && !document.getElementById('rc-fonts')) {
  const link = document.createElement('link');
  link.rel = 'stylesheet';
  link.id = 'rc-fonts-pretendard';
  link.href = 'https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.min.css';
  document.head.appendChild(link);

  const link2 = document.createElement('link');
  link2.rel = 'stylesheet';
  link2.id = 'rc-fonts-mono';
  link2.href = 'https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&display=swap';
  document.head.appendChild(link2);

  const style = document.createElement('style');
  style.id = 'rc-fonts';
  style.textContent = `
    .rc-app, .rc-app * { font-family: ${RC_FONT_UI}; font-feature-settings: 'ss01','cv01'; }
    .rc-mono { font-family: ${RC_FONT_MONO}; font-variant-numeric: tabular-nums; letter-spacing: -0.01em; }
    .rc-tnum { font-variant-numeric: tabular-nums; }
    .rc-app ::-webkit-scrollbar { width: 0; height: 0; }
    .rc-app { -webkit-font-smoothing: antialiased; }
    @keyframes rc-pulse-soft { 0%,100% { opacity: 1 } 50% { opacity: 0.55 } }
    @keyframes rc-slide-up { from { opacity: 0; transform: translateY(8px) } to { opacity: 1; transform: translateY(0) } }
    @keyframes rc-fade-in { from { opacity: 0 } to { opacity: 1 } }
  `;
  document.head.appendChild(style);
}

Object.assign(window, { RC_PALETTES, RC_ZONES, RC_FONT_UI, RC_FONT_MONO, rcTokens });
