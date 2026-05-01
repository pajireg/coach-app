// primitives.jsx — shared UI primitives for Running Coach
// All accept `theme` and read tokens via rcTokens(theme).

// ── Status bar (navy-aware) ───────────────────────────────────
function RCStatusBar({ theme = 'dark', time = '07:24' }) {
  const c = theme === 'dark' ? '#F5F3EE' : '#0B1220';
  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '14px 28px 6px', height: 44, boxSizing: 'border-box',
      position: 'absolute', top: 0, left: 0, right: 0, zIndex: 50,
      pointerEvents: 'none',
    }}>
      <div style={{ fontFamily: RC_FONT_UI, fontWeight: 600, fontSize: 15, color: c, letterSpacing: -0.1 }}>
        {time}
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
        {/* signal */}
        <svg width="16" height="10" viewBox="0 0 16 10"><g fill={c}>
          <rect x="0" y="7" width="2.5" height="3" rx="0.5"/>
          <rect x="3.5" y="5" width="2.5" height="5" rx="0.5"/>
          <rect x="7" y="3" width="2.5" height="7" rx="0.5"/>
          <rect x="10.5" y="0" width="2.5" height="10" rx="0.5"/>
        </g></svg>
        {/* wifi */}
        <svg width="14" height="10" viewBox="0 0 14 10" fill="none"><g stroke={c} strokeWidth="1.2" fill={c}>
          <path d="M7 3.2 A4 4 0 0 1 10.8 5.5" fill="none"/>
          <path d="M7 1 A6 6 0 0 1 13 4.5" fill="none"/>
          <circle cx="7" cy="8.5" r="1"/>
        </g></svg>
        {/* battery */}
        <svg width="24" height="11" viewBox="0 0 24 11">
          <rect x="0.5" y="0.5" width="20" height="10" rx="2.5" fill="none" stroke={c} strokeOpacity="0.4"/>
          <rect x="2" y="2" width="17" height="7" rx="1.2" fill={c}/>
          <rect x="21.5" y="3.5" width="1.5" height="4" rx="0.5" fill={c} fillOpacity="0.5"/>
        </svg>
      </div>
    </div>
  );
}

// ── Home indicator ────────────────────────────────────────────
function RCHomeIndicator({ theme = 'dark' }) {
  return (
    <div style={{
      position: 'absolute', bottom: 0, left: 0, right: 0,
      height: 28, display: 'flex', alignItems: 'flex-end', justifyContent: 'center',
      paddingBottom: 7, pointerEvents: 'none', zIndex: 100,
    }}>
      <div style={{
        width: 134, height: 5, borderRadius: 100,
        background: theme === 'dark' ? 'rgba(245,243,238,0.55)' : 'rgba(11,18,32,0.3)',
      }} />
    </div>
  );
}

// ── Dynamic island ────────────────────────────────────────────
function RCIsland() {
  return (
    <div style={{
      position: 'absolute', top: 11, left: '50%', transform: 'translateX(-50%)',
      width: 120, height: 34, borderRadius: 22, background: '#000', zIndex: 50,
    }} />
  );
}

// ── Phone frame ───────────────────────────────────────────────
// iOS 26 Liquid Glass — subtle ambient wash behind content, larger corner radius.
function RCPhone({ children, theme = 'dark', width = 390, height = 844, bareHeader = false, onStatusBarTap }) {
  const t = rcTokens(theme);
  // Ambient wash: very soft zone-tinted blobs that Liquid Glass surfaces refract through.
  const wash = theme === 'dark'
    ? `radial-gradient(120% 60% at 0% 0%, rgba(74,158,219,0.12), transparent 55%),
       radial-gradient(100% 50% at 100% 15%, rgba(232,169,76,0.09), transparent 60%),
       radial-gradient(140% 70% at 50% 110%, rgba(143,123,212,0.10), transparent 60%)`
    : `radial-gradient(120% 60% at 0% 0%, rgba(74,158,219,0.14), transparent 55%),
       radial-gradient(100% 50% at 100% 15%, rgba(232,169,76,0.12), transparent 60%),
       radial-gradient(140% 70% at 50% 110%, rgba(143,123,212,0.12), transparent 60%)`;
  return (
    <div className="rc-app" style={{
      width, height, borderRadius: 55, position: 'relative', overflow: 'hidden',
      background: t.bg, color: t.text,
      boxShadow: '0 30px 80px rgba(0,0,0,0.25), 0 0 0 1px rgba(0,0,0,0.1)',
    }}>
      {/* Ambient wash layer — underpins Liquid Glass refraction */}
      <div aria-hidden="true" style={{
        position: 'absolute', inset: 0, background: wash, pointerEvents: 'none',
      }} />
      {/* Content scrolls behind status bar / island for edge-to-edge glass feel */}
      <div style={{ position: 'absolute', inset: '0 0 28px 0', display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
        {children}
      </div>
      <RCIsland />
      <div onClick={onStatusBarTap}>
        <RCStatusBar theme={theme} />
      </div>
      <RCHomeIndicator theme={theme} />
    </div>
  );
}

// ── Bottom tab bar ────────────────────────────────────────────
const RC_TABS = [
  { key: 'home',     label: '오늘',   icon: (c, active) => (
    <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
      <path d="M4 9.5L11 4l7 5.5V17.5a1 1 0 01-1 1h-3.5V13h-5v5.5H5a1 1 0 01-1-1V9.5z" stroke={c} strokeWidth={active ? 2 : 1.6} fill={active ? c : 'none'} strokeLinejoin="round"/>
    </svg>
  ) },
  { key: 'weekly',   label: '주간',   icon: (c, active) => (
    <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
      <rect x="3" y="4.5" width="16" height="15" rx="2" stroke={c} strokeWidth={active ? 2 : 1.6} fill={active ? c+'22' : 'none'}/>
      <path d="M3 9h16" stroke={c} strokeWidth={active ? 2 : 1.6}/>
      <path d="M7 3v3M15 3v3" stroke={c} strokeWidth={active ? 2 : 1.6} strokeLinecap="round"/>
    </svg>
  ) },
  { key: 'trends',   label: '추이',   icon: (c, active) => (
    <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
      <path d="M3 16l5-5 4 3 7-8" stroke={c} strokeWidth={active ? 2.2 : 1.8} strokeLinecap="round" strokeLinejoin="round" fill="none"/>
      <circle cx="8" cy="11" r={active ? 2 : 1.4} fill={c}/>
      <circle cx="12" cy="14" r={active ? 2 : 1.4} fill={c}/>
      <circle cx="19" cy="6" r={active ? 2 : 1.4} fill={c}/>
    </svg>
  ) },
  { key: 'goals',    label: '목표',   icon: (c, active) => (
    <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
      <circle cx="11" cy="11" r="8" stroke={c} strokeWidth={active ? 2 : 1.6} fill="none"/>
      <circle cx="11" cy="11" r="4" stroke={c} strokeWidth={active ? 2 : 1.6} fill={active ? c+'22' : 'none'}/>
      <circle cx="11" cy="11" r={active ? 1.8 : 1.2} fill={c}/>
    </svg>
  ) },
  { key: 'settings', label: '설정',   icon: (c, active) => (
    <svg width="22" height="22" viewBox="0 0 22 22" fill="none">
      <circle cx="11" cy="11" r="3" stroke={c} strokeWidth={active ? 2 : 1.6} fill={active ? c+'22' : 'none'}/>
      <path d="M11 2v2.5M11 17.5V20M2 11h2.5M17.5 11H20M4.5 4.5l1.8 1.8M15.7 15.7l1.8 1.8M4.5 17.5l1.8-1.8M15.7 6.3l1.8-1.8"
        stroke={c} strokeWidth={active ? 2 : 1.6} strokeLinecap="round"/>
    </svg>
  ) },
];

// ── Floating Liquid Glass tab bar ─────────────────────────────
// iOS 26 style: detached from bottom, heavy blur, specular highlight,
// active pill with subtle tint. Absolutely positioned inside RCPhone.
function RCTabBar({ active = 'home', onChange, theme = 'dark' }) {
  const t = rcTokens(theme);
  const barBg = theme === 'dark' ? 'rgba(20,28,48,0.55)' : 'rgba(255,255,255,0.55)';
  const activePill = theme === 'dark' ? 'rgba(255,255,255,0.10)' : 'rgba(11,18,32,0.06)';
  const glassShadow = theme === 'dark'
    ? '0 20px 48px rgba(0,0,0,0.45), 0 1px 0 rgba(255,255,255,0.10) inset, 0 0 0 0.5px rgba(255,255,255,0.14) inset'
    : '0 16px 40px rgba(11,18,32,0.16), 0 1px 0 rgba(255,255,255,0.95) inset, 0 0 0 0.5px rgba(255,255,255,0.75) inset';
  return (
    <div style={{
      position: 'absolute', left: 12, right: 12, bottom: 18, zIndex: 40,
      borderRadius: 30, padding: '7px 6px',
      background: barBg,
      backdropFilter: 'blur(30px) saturate(180%)',
      WebkitBackdropFilter: 'blur(30px) saturate(180%)',
      border: `0.5px solid ${t.border}`,
      boxShadow: glassShadow,
      display: 'flex', gap: 2,
    }}>
      {/* specular top highlight */}
      <div style={{ position: 'absolute', top: 0.5, left: '14%', right: '14%', height: 1,
        background: `linear-gradient(90deg, transparent, ${theme === 'dark' ? 'rgba(255,255,255,0.28)' : 'rgba(255,255,255,0.95)'}, transparent)`,
        pointerEvents: 'none' }} />
      {RC_TABS.map(tab => {
        const isActive = active === tab.key;
        const color = isActive ? t.text : t.textFaint;
        return (
          <button key={tab.key} onClick={() => onChange && onChange(tab.key)} style={{
            flex: 1, background: isActive ? activePill : 'transparent',
            border: 'none', cursor: 'pointer', borderRadius: 22,
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
            padding: '7px 0 5px', color,
            transition: 'all 0.22s cubic-bezier(0.2,0.8,0.2,1)',
          }}>
            {tab.icon(color, isActive)}
            <span style={{ fontSize: 10, fontWeight: isActive ? 600 : 500, letterSpacing: -0.1 }}>{tab.label}</span>
          </button>
        );
      })}
    </div>
  );
}

// ── Session badge ─────────────────────────────────────────────
function RCZoneBadge({ zone, size = 'md', filled = false }) {
  const z = RC_ZONES[zone] || RC_ZONES.rest;
  const sizes = {
    sm: { px: 6, py: 2, fs: 10, radius: 4 },
    md: { px: 8, py: 3, fs: 11, radius: 5 },
    lg: { px: 10, py: 4, fs: 12, radius: 6 },
  };
  const s = sizes[size];
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: 5,
      padding: `${s.py}px ${s.px}px`, borderRadius: s.radius,
      fontSize: s.fs, fontWeight: 600, letterSpacing: 0.3, textTransform: 'uppercase',
      background: filled ? z.color : z.softStrong,
      color: filled ? '#0B1220' : z.color,
    }}>
      <span style={{ width: 5, height: 5, borderRadius: 5, background: filled ? '#0B1220' : z.color, opacity: filled ? 0.8 : 1 }} />
      {z.label}
    </span>
  );
}

// ── Button ────────────────────────────────────────────────────
// Primary: solid high-contrast.
// Secondary: Liquid Glass pill (blur + specular).
function RCButton({ children, onClick, variant = 'primary', theme = 'dark', full = false, size = 'md', style = {} }) {
  const t = rcTokens(theme);
  const sizes = {
    sm: { h: 36, fs: 13, px: 14, radius: 18 },
    md: { h: 50, fs: 15, px: 20, radius: 25 },
    lg: { h: 56, fs: 16, px: 24, radius: 28 },
  };
  const s = sizes[size];
  const variants = {
    primary: {
      bg: t.text, color: t.bg, border: 'none',
      shadow: theme === 'dark'
        ? '0 10px 24px rgba(0,0,0,0.45), 0 1px 0 rgba(255,255,255,0.18) inset'
        : '0 10px 24px rgba(11,18,32,0.22), 0 1px 0 rgba(255,255,255,0.22) inset',
    },
    secondary: {
      bg: theme === 'dark' ? 'rgba(255,255,255,0.10)' : 'rgba(255,255,255,0.55)',
      color: t.text,
      border: `0.5px solid ${t.borderStrong}`,
      shadow: theme === 'dark'
        ? '0 1px 0 rgba(255,255,255,0.10) inset'
        : '0 1px 0 rgba(255,255,255,0.9) inset, 0 4px 12px rgba(11,18,32,0.06)',
      backdrop: 'blur(24px) saturate(180%)',
    },
    ghost: { bg: 'transparent', color: t.text, border: 'none' },
  };
  const v = variants[variant];
  return (
    <button onClick={onClick} style={{
      height: s.h, padding: `0 ${s.px}px`, borderRadius: s.radius,
      background: v.bg, color: v.color, border: v.border,
      backdropFilter: v.backdrop, WebkitBackdropFilter: v.backdrop,
      boxShadow: v.shadow,
      fontSize: s.fs, fontWeight: 600, letterSpacing: -0.1,
      cursor: 'pointer', width: full ? '100%' : 'auto',
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8,
      transition: 'transform 0.08s, opacity 0.15s',
      position: 'relative', overflow: 'hidden',
      ...style,
    }}
      onMouseDown={(e) => e.currentTarget.style.transform = 'scale(0.97)'}
      onMouseUp={(e) => e.currentTarget.style.transform = 'scale(1)'}
      onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
    >{children}</button>
  );
}

// ── Card ──────────────────────────────────────────────────────
// iOS 26 Liquid Glass: translucent material + blur + hairline specular.
// Pass glass={false} to get a solid surface for cases that need opacity (modals, etc).
function RCCard({ children, theme = 'dark', padding = 20, radius = 22, elevated = false, accent, style = {}, onClick, glass = true }) {
  const t = rcTokens(theme);
  const bg = glass
    ? (elevated ? t.cardHi : t.card)
    : (elevated ? t.bgElev2 : t.bgElev);
  const shadow = glass
    ? (theme === 'dark'
      ? '0 1px 0 rgba(255,255,255,0.06) inset, 0 0 0 0.5px rgba(255,255,255,0.05) inset'
      : '0 1px 0 rgba(255,255,255,0.7) inset, 0 0 0 0.5px rgba(255,255,255,0.4) inset')
    : 'none';
  return (
    <div onClick={onClick} style={{
      background: bg,
      backdropFilter: glass ? 'blur(24px) saturate(170%)' : 'none',
      WebkitBackdropFilter: glass ? 'blur(24px) saturate(170%)' : 'none',
      border: `0.5px solid ${t.border}`,
      borderRadius: radius, padding,
      position: 'relative', overflow: 'hidden',
      cursor: onClick ? 'pointer' : 'default',
      boxShadow: shadow,
      ...style,
    }}>
      {glass && (
        <div aria-hidden="true" style={{ position: 'absolute', top: 0, left: 14, right: 14, height: 1,
          background: `linear-gradient(90deg, transparent, ${theme === 'dark' ? 'rgba(255,255,255,0.18)' : 'rgba(255,255,255,0.9)'}, transparent)`,
          pointerEvents: 'none' }} />
      )}
      {accent && (
        <div style={{
          position: 'absolute', top: 0, left: 0, bottom: 0, width: 3,
          background: accent,
        }} />
      )}
      {children}
    </div>
  );
}

// ── Glass nav bar ─────────────────────────────────────────────
// iOS 26 floating navigation bar that appears when content scrolls under it.
// Pass `scrolled` to trigger glass background reveal; when at top it is transparent.
function RCNavBar({ theme = 'dark', scrolled = false, title, trailing, leading, subtitle }) {
  const t = rcTokens(theme);
  return (
    <div style={{
      position: 'absolute', top: 0, left: 0, right: 0, zIndex: 30,
      paddingTop: 44, // clear status bar
      background: scrolled
        ? (theme === 'dark' ? 'rgba(20,28,48,0.55)' : 'rgba(255,255,255,0.55)')
        : 'transparent',
      backdropFilter: scrolled ? 'blur(30px) saturate(180%)' : 'none',
      WebkitBackdropFilter: scrolled ? 'blur(30px) saturate(180%)' : 'none',
      borderBottom: scrolled ? `0.5px solid ${t.border}` : '0.5px solid transparent',
      boxShadow: scrolled
        ? (theme === 'dark'
          ? '0 1px 0 rgba(255,255,255,0.08) inset, 0 6px 20px rgba(0,0,0,0.25)'
          : '0 1px 0 rgba(255,255,255,0.85) inset, 0 6px 20px rgba(11,18,32,0.06)')
        : 'none',
      transition: 'background 0.25s, backdrop-filter 0.25s, border-color 0.25s, box-shadow 0.25s',
    }}>
      <div style={{
        minHeight: 44, padding: '4px 16px 10px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8,
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, minWidth: 0 }}>
          {leading}
          {title && (
            <div style={{ minWidth: 0 }}>
              <div style={{ fontSize: 17, fontWeight: 600, color: t.text, letterSpacing: -0.3,
                opacity: scrolled ? 1 : 0, transition: 'opacity 0.2s',
                whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
              }}>{title}</div>
              {subtitle && scrolled && (
                <div style={{ fontSize: 11, color: t.textFaint, marginTop: -1 }}>{subtitle}</div>
              )}
            </div>
          )}
        </div>
        {trailing}
      </div>
    </div>
  );
}

// iOS 26 screen scaffold — handles large-title collapse into floating nav bar.
// Children are rendered inside the scroll view. Pass `eyebrow`, `title`, `trailing`, `leading`.
function RCScreen({ theme = 'dark', title, eyebrow, trailing, leading, navTitle, children }) {
  const t = rcTokens(theme);
  const scrollRef = React.useRef(null);
  const [scrolled, setScrolled] = React.useState(false);
  const onScroll = (e) => setScrolled(e.target.scrollTop > 28);
  return (
    <div style={{ position: 'relative', height: '100%' }}>
      <RCNavBar theme={theme} scrolled={scrolled} title={navTitle || title} trailing={trailing} leading={leading} />
      <div ref={scrollRef} onScroll={onScroll} style={{ height: '100%', overflow: 'auto', paddingTop: 44, paddingBottom: 120 }}>
        {(title || eyebrow) && (
          <div style={{ padding: '12px 20px 14px' }}>
            {eyebrow && <div style={{ fontSize: 12, fontWeight: 500, color: t.textFaint, letterSpacing: 0.2, marginBottom: 4 }}>{eyebrow}</div>}
            {title && <div style={{ fontSize: 32, fontWeight: 700, color: t.text, letterSpacing: -0.8, lineHeight: 1.1 }}>{title}</div>}
          </div>
        )}
        {children}
      </div>
    </div>
  );
}

// ── Section header ────────────────────────────────────────────
function RCSectionHeader({ title, action, theme = 'dark', subtitle }) {
  const t = rcTokens(theme);
  return (
    <div style={{ padding: '6px 20px 10px', display: 'flex', alignItems: 'baseline', justifyContent: 'space-between' }}>
      <div>
        <div style={{ fontSize: 11, fontWeight: 600, letterSpacing: 1.2, textTransform: 'uppercase', color: t.textFaint }}>{title}</div>
        {subtitle && <div style={{ fontSize: 13, color: t.textDim, marginTop: 2 }}>{subtitle}</div>}
      </div>
      {action && (
        <div style={{ fontSize: 13, color: t.textDim, fontWeight: 500 }}>{action}</div>
      )}
    </div>
  );
}

// ── Divider ───────────────────────────────────────────────────
function RCDivider({ theme = 'dark', style = {} }) {
  const t = rcTokens(theme);
  return <div style={{ height: 0.5, background: t.divider, ...style }} />;
}

// ── Icon (chevron, arrow, check, plus) ────────────────────────
function RCIcon({ name, size = 16, color = 'currentColor', stroke = 1.8 }) {
  const paths = {
    chevronRight: <path d="M6 4l6 6-6 6" stroke={color} strokeWidth={stroke} fill="none" strokeLinecap="round" strokeLinejoin="round"/>,
    chevronLeft:  <path d="M12 4l-6 6 6 6" stroke={color} strokeWidth={stroke} fill="none" strokeLinecap="round" strokeLinejoin="round"/>,
    chevronDown:  <path d="M4 7l6 6 6-6" stroke={color} strokeWidth={stroke} fill="none" strokeLinecap="round" strokeLinejoin="round"/>,
    chevronUp:    <path d="M4 13l6-6 6 6" stroke={color} strokeWidth={stroke} fill="none" strokeLinecap="round" strokeLinejoin="round"/>,
    plus:         <g stroke={color} strokeWidth={stroke} strokeLinecap="round"><path d="M10 4v12"/><path d="M4 10h12"/></g>,
    check:        <path d="M4 10l4 4 8-8" stroke={color} strokeWidth={stroke} fill="none" strokeLinecap="round" strokeLinejoin="round"/>,
    arrowUp:      <path d="M10 16V4M4 10l6-6 6 6" stroke={color} strokeWidth={stroke} fill="none" strokeLinecap="round" strokeLinejoin="round"/>,
    arrowDown:    <path d="M10 4v12M4 10l6 6 6-6" stroke={color} strokeWidth={stroke} fill="none" strokeLinecap="round" strokeLinejoin="round"/>,
    close:        <g stroke={color} strokeWidth={stroke} strokeLinecap="round"><path d="M5 5l10 10"/><path d="M15 5L5 15"/></g>,
    refresh:      <g stroke={color} strokeWidth={stroke} strokeLinecap="round" strokeLinejoin="round" fill="none"><path d="M3 10a7 7 0 0112-4.9L17 7"/><path d="M17 3v4h-4"/><path d="M17 10a7 7 0 01-12 4.9L3 13"/><path d="M3 17v-4h4"/></g>,
    info:         <g><circle cx="10" cy="10" r="8" stroke={color} strokeWidth={stroke} fill="none"/><circle cx="10" cy="6.5" r="1" fill={color}/><path d="M10 9v6" stroke={color} strokeWidth={stroke} strokeLinecap="round"/></g>,
    heart:        <path d="M10 16s-6-3.5-6-8a3.5 3.5 0 016-2.5A3.5 3.5 0 0116 8c0 4.5-6 8-6 8z" stroke={color} strokeWidth={stroke} fill="none" strokeLinejoin="round"/>,
    bolt:         <path d="M11 2L4 12h5l-1 6 7-10h-5l1-6z" stroke={color} strokeWidth={stroke} fill="none" strokeLinejoin="round"/>,
    watch:        <g stroke={color} strokeWidth={stroke} fill="none"><rect x="5" y="5" width="10" height="10" rx="2"/><path d="M8 2h4M8 18h4M10 8v2l1.5 1" strokeLinecap="round"/></g>,
    bell:         <g stroke={color} strokeWidth={stroke} fill="none" strokeLinejoin="round" strokeLinecap="round"><path d="M5 14h10l-1-2V9a4 4 0 00-8 0v3l-1 2z"/><path d="M8 16a2 2 0 004 0"/></g>,
    dot:          <circle cx="10" cy="10" r="3" fill={color}/>,
    calendar:     <g stroke={color} strokeWidth={stroke} fill="none"><rect x="3" y="4" width="14" height="13" rx="2"/><path d="M3 8h14M7 2v4M13 2v4" strokeLinecap="round"/></g>,
  };
  return (
    <svg width={size} height={size} viewBox="0 0 20 20" style={{ display: 'block' }}>
      {paths[name] || null}
    </svg>
  );
}

Object.assign(window, {
  RCStatusBar, RCHomeIndicator, RCIsland, RCPhone, RCTabBar, RC_TABS,
  RCZoneBadge, RCButton, RCCard, RCSectionHeader, RCDivider, RCIcon, RCNavBar, RCScreen,
});
