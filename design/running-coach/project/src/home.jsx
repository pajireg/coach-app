// home.jsx — Home / Today screen with 4 hero variations
// Variants: 'hero-bold' (default), 'hero-split', 'hero-mono', 'hero-zoned'

function RCScoreChip({ score, expanded, onToggle, theme = 'dark', variant = 'bar' }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[score.zone];
  const pct = score.value / score.max;
  const color = z.color;

  if (variant === 'ring') {
    const r = 18, cx = 24, cy = 24, C = 2 * Math.PI * r;
    return (
      <button onClick={onToggle} style={{
        flex: 1, background: t.card, border: `0.5px solid ${t.border}`,
        borderRadius: 16, padding: '12px 10px', cursor: 'pointer',
        display: 'flex', alignItems: 'center', gap: 10, minWidth: 0,
      }}>
        <svg width="48" height="48" viewBox="0 0 48 48" style={{ flexShrink: 0 }}>
          <circle cx={cx} cy={cy} r={r} fill="none" stroke={t.divider} strokeWidth="3"/>
          <circle cx={cx} cy={cy} r={r} fill="none" stroke={color} strokeWidth="3"
            strokeDasharray={`${C*pct} ${C}`} strokeLinecap="round"
            transform={`rotate(-90 ${cx} ${cy})`} />
          <text x="24" y="28" textAnchor="middle" fontSize="13" fontWeight="700" fill={t.text} fontFamily={RC_FONT_MONO}>{score.value}</text>
        </svg>
        <div style={{ minWidth: 0, textAlign: 'left' }}>
          <div style={{ fontSize: 10, fontWeight: 600, letterSpacing: 0.6, color: t.textFaint, textTransform: 'uppercase' }}>{score.label}</div>
          <div style={{ fontSize: 12, color, fontWeight: 600, marginTop: 1 }}>{score.tier}</div>
        </div>
      </button>
    );
  }

  // bar variant (default)
  return (
    <button onClick={onToggle} style={{
      flex: 1, background: t.card, border: `0.5px solid ${expanded ? color + '80' : t.border}`,
      borderRadius: 14, padding: '12px 12px 10px', cursor: 'pointer',
      display: 'flex', flexDirection: 'column', gap: 8, minWidth: 0,
      textAlign: 'left', transition: 'border-color 0.2s',
    }}>
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between' }}>
        <span style={{ fontSize: 10, fontWeight: 600, letterSpacing: 0.6, color: t.textFaint, textTransform: 'uppercase' }}>{score.label}</span>
        <span className="rc-mono" style={{ fontSize: 18, fontWeight: 600, color: t.text }}>{score.value}</span>
      </div>
      <div style={{ height: 3, borderRadius: 2, background: t.divider, overflow: 'hidden' }}>
        <div style={{ height: '100%', width: `${pct * 100}%`, background: color, transition: 'width 0.5s' }} />
      </div>
      <div style={{ fontSize: 11, color: t.textDim, fontWeight: 500 }}>{score.tier}</div>
    </button>
  );
}

function RCScoreExplain({ score, theme = 'dark' }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[score.zone];
  return (
    <div style={{
      animation: 'rc-slide-up 0.25s ease-out',
      background: z.soft, border: `0.5px solid ${z.color}30`,
      borderRadius: 12, padding: '12px 14px', margin: '0 20px',
    }}>
      <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
        <div style={{ width: 4, height: 4, borderRadius: 4, background: z.color, marginTop: 7, flexShrink: 0 }} />
        <div style={{ fontSize: 13, lineHeight: 1.55, color: t.textDim }}>{score.explain}</div>
      </div>
    </div>
  );
}

// ── Hero variants ─────────────────────────────────────────────
function HeroBold({ session, theme, onStart }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[session.zone];
  return (
    <div style={{
      margin: '0 20px', padding: '22px 22px 20px',
      borderRadius: 24, background: t.card, border: `0.5px solid ${t.border}`,
      position: 'relative', overflow: 'hidden',
    }}>
      <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 3, background: z.color }} />
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14 }}>
        <RCZoneBadge zone={session.zone} size="md" />
        <span style={{ fontSize: 12, color: t.textFaint, fontWeight: 500 }}>오늘 · 수요일</span>
      </div>
      <div style={{ fontSize: 28, fontWeight: 700, letterSpacing: -0.6, color: t.text, marginBottom: 2 }}>
        {session.titleKo}
      </div>
      <div style={{ fontSize: 14, color: t.textDim, marginBottom: 22, lineHeight: 1.5 }}>
        {session.headline}
      </div>

      <div style={{ display: 'flex', gap: 0, alignItems: 'baseline', marginBottom: 18 }}>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 10, fontWeight: 600, letterSpacing: 0.8, color: t.textFaint, textTransform: 'uppercase', marginBottom: 4 }}>시간</div>
          <div className="rc-mono" style={{ fontSize: 32, fontWeight: 500, color: t.text, letterSpacing: -1 }}>
            {session.durationMin}<span style={{ fontSize: 16, color: t.textDim, marginLeft: 2 }}>분</span>
          </div>
        </div>
        <div style={{ width: 0.5, height: 48, background: t.divider, margin: '0 16px' }} />
        <div style={{ flex: 1.1 }}>
          <div style={{ fontSize: 10, fontWeight: 600, letterSpacing: 0.8, color: t.textFaint, textTransform: 'uppercase', marginBottom: 4 }}>목표 페이스</div>
          <div className="rc-mono" style={{ fontSize: 32, fontWeight: 500, color: t.text, letterSpacing: -1 }}>
            {session.primaryTarget.split('/')[0]}
            <span style={{ fontSize: 16, color: t.textDim, marginLeft: 2 }}>/km</span>
          </div>
        </div>
      </div>

      <RCButton theme={theme} full size="md" onClick={onStart}>
        세션 상세 보기
        <RCIcon name="chevronRight" size={14} />
      </RCButton>
    </div>
  );
}

function HeroSplit({ session, theme, onStart }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[session.zone];
  return (
    <div style={{
      margin: '0 20px', borderRadius: 24, overflow: 'hidden',
      border: `0.5px solid ${t.border}`,
    }}>
      <div style={{
        padding: '18px 22px', background: z.softStrong,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      }}>
        <div>
          <div style={{ fontSize: 11, fontWeight: 600, letterSpacing: 1.2, color: z.color, textTransform: 'uppercase', marginBottom: 3 }}>
            {z.label} · 오늘
          </div>
          <div style={{ fontSize: 22, fontWeight: 700, color: t.text, letterSpacing: -0.4 }}>{session.titleKo}</div>
        </div>
        <div style={{ width: 44, height: 44, borderRadius: 22, background: z.color,
          display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <RCIcon name="bolt" size={20} color="#0B1220" />
        </div>
      </div>
      <div style={{ padding: 20, background: t.card }}>
        <div style={{ fontSize: 13, color: t.textDim, marginBottom: 18, lineHeight: 1.55 }}>
          {session.headline}
        </div>
        <div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
          <StatCell theme={theme} label="시간"  value={session.durationMin} unit="분" />
          <StatCell theme={theme} label="거리"  value={session.distanceKm} unit="km" />
          <StatCell theme={theme} label="페이스" value={session.primaryTarget.split('/')[0]} unit="/km" mono />
        </div>
        <RCButton theme={theme} full size="md" onClick={onStart}>
          세션 상세 보기 <RCIcon name="chevronRight" size={14} />
        </RCButton>
      </div>
    </div>
  );
}

function HeroMono({ session, theme, onStart }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[session.zone];
  return (
    <div style={{
      margin: '0 20px', padding: '24px 22px',
      borderRadius: 20, background: t.card, border: `0.5px solid ${t.border}`,
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 18 }}>
        <div style={{ width: 8, height: 8, borderRadius: 8, background: z.color }} />
        <div style={{ fontSize: 11, fontWeight: 600, letterSpacing: 1.2, color: t.textDim, textTransform: 'uppercase' }}>
          오늘의 세션 · {z.label}
        </div>
      </div>
      <div className="rc-mono" style={{
        fontSize: 44, fontWeight: 500, color: t.text,
        letterSpacing: -1.5, lineHeight: 1, marginBottom: 8,
      }}>
        {session.durationMin}<span style={{ fontSize: 20, color: t.textDim }}>분</span>
      </div>
      <div style={{ fontSize: 22, fontWeight: 700, color: t.text, letterSpacing: -0.3, marginBottom: 2 }}>
        {session.titleKo}
      </div>
      <div style={{ fontSize: 13, color: t.textDim, marginBottom: 18 }}>{session.headline}</div>

      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        paddingTop: 14, borderTop: `0.5px solid ${t.divider}` }}>
        <div style={{ display: 'flex', gap: 14 }}>
          <InlineStat theme={theme} label="페이스" value={session.primaryTarget.split('/')[0]} mono />
          <InlineStat theme={theme} label="거리" value={`${session.distanceKm}km`} mono />
        </div>
        <button onClick={onStart} style={{
          background: 'none', border: 'none', cursor: 'pointer',
          color: t.text, fontSize: 13, fontWeight: 600,
          display: 'flex', alignItems: 'center', gap: 4,
        }}>
          상세 <RCIcon name="chevronRight" size={12} />
        </button>
      </div>
    </div>
  );
}

function HeroZoned({ session, theme, onStart }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[session.zone];
  return (
    <div style={{
      margin: '0 20px', padding: 2, borderRadius: 24,
      background: `linear-gradient(135deg, ${z.color}40, ${z.color}08)`,
    }}>
      <div style={{ borderRadius: 22, background: t.card, padding: '20px 22px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
          <RCZoneBadge zone={session.zone} size="md" filled />
          <span style={{ fontSize: 11, color: t.textFaint, fontWeight: 600, letterSpacing: 0.3 }}>05:30 예정</span>
        </div>
        <div style={{ fontSize: 26, fontWeight: 700, color: t.text, letterSpacing: -0.5, marginBottom: 2 }}>
          {session.titleKo}
        </div>
        <div style={{ fontSize: 13, color: t.textDim, marginBottom: 18, lineHeight: 1.5 }}>
          {session.headline}
        </div>

        {/* target visualization */}
        <div style={{
          padding: 14, borderRadius: 14, background: t.bg,
          border: `0.5px solid ${t.border}`, marginBottom: 14,
        }}>
          <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 8 }}>
            <span style={{ fontSize: 11, fontWeight: 600, letterSpacing: 0.6, color: t.textFaint, textTransform: 'uppercase' }}>목표 페이스</span>
            <span style={{ fontSize: 11, color: t.textFaint }}>± 10s/km</span>
          </div>
          <div className="rc-mono" style={{ fontSize: 34, fontWeight: 500, color: z.color, letterSpacing: -1 }}>
            {session.primaryTarget.split('/')[0]}<span style={{ fontSize: 14, color: t.textDim }}>/km</span>
          </div>
        </div>

        <div style={{ display: 'flex', gap: 8 }}>
          <RCButton theme={theme} size="md" full onClick={onStart}>상세 보기</RCButton>
        </div>
      </div>
    </div>
  );
}

function StatCell({ label, value, unit, theme, mono }) {
  const t = rcTokens(theme);
  return (
    <div style={{ flex: 1, padding: '10px 0' }}>
      <div style={{ fontSize: 10, fontWeight: 600, letterSpacing: 0.6, color: t.textFaint, textTransform: 'uppercase', marginBottom: 4 }}>{label}</div>
      <div className={mono ? 'rc-mono' : 'rc-tnum'} style={{ fontSize: 22, fontWeight: 600, color: t.text, letterSpacing: -0.5 }}>
        {value}<span style={{ fontSize: 12, color: t.textDim, marginLeft: 2, fontWeight: 500 }}>{unit}</span>
      </div>
    </div>
  );
}

function InlineStat({ label, value, theme, mono }) {
  const t = rcTokens(theme);
  return (
    <div>
      <span style={{ fontSize: 11, color: t.textFaint, marginRight: 6 }}>{label}</span>
      <span className={mono ? 'rc-mono' : 'rc-tnum'} style={{ fontSize: 13, color: t.text, fontWeight: 600 }}>{value}</span>
    </div>
  );
}

// ── Home screen ───────────────────────────────────────────────
function RCHomeScreen({ theme = 'dark', onNav, heroVariant = 'bold', sessionKey = 'threshold', completed = false }) {
  const t = rcTokens(theme);
  const [expanded, setExpanded] = React.useState(null);
  const [refreshing, setRefreshing] = React.useState(false);
  const session = RC_SESSIONS[sessionKey] || RC_SESSIONS.threshold;

  const heroMap = { bold: HeroBold, split: HeroSplit, mono: HeroMono, zoned: HeroZoned };
  const Hero = heroMap[heroVariant] || HeroBold;

  const scrollRef = React.useRef(null);
  const [pullOffset, setPullOffset] = React.useState(0);

  const [scrolled, setScrolled] = React.useState(false);
  const onScroll = (e) => setScrolled(e.target.scrollTop > 24);

  const refreshBtn = (
    <button onClick={() => { setRefreshing(true); setTimeout(() => setRefreshing(false), 900); }}
      style={{ width: 36, height: 36, borderRadius: 18,
        background: theme === 'dark' ? 'rgba(255,255,255,0.10)' : 'rgba(255,255,255,0.6)',
        backdropFilter: 'blur(20px) saturate(180%)', WebkitBackdropFilter: 'blur(20px) saturate(180%)',
        border: `0.5px solid ${t.borderStrong}`,
        boxShadow: theme === 'dark' ? '0 1px 0 rgba(255,255,255,0.08) inset' : '0 1px 0 rgba(255,255,255,0.9) inset',
        display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', color: t.textDim,
        transform: refreshing ? 'rotate(360deg)' : 'none', transition: 'transform 0.9s' }}>
      <RCIcon name="refresh" size={15} color="currentColor" />
    </button>
  );

  return (
    <div style={{ position: 'relative', height: '100%' }}>
      <RCNavBar theme={theme} scrolled={scrolled} title={`좋은 아침, ${RC_USER.name}`} trailing={refreshBtn} />

      <div ref={scrollRef} onScroll={onScroll} style={{ height: '100%', overflow: 'auto', paddingTop: 44, paddingBottom: 120 }}>
        {/* Large title — collapses into nav bar on scroll */}
        <div style={{ padding: '14px 20px 14px', display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between' }}>
          <div>
            <div style={{ fontSize: 12, fontWeight: 500, color: t.textFaint, letterSpacing: 0.2, marginBottom: 4 }}>2026년 4월 21일 · 수</div>
            <div style={{ fontSize: 32, fontWeight: 700, color: t.text, letterSpacing: -0.8, lineHeight: 1.1 }}>
              좋은 아침이에요,<br/>{RC_USER.name}
            </div>
          </div>
        </div>

        {refreshing && (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
            padding: '4px 0 10px', fontSize: 12, color: t.textDim }}>
            <div style={{ width: 10, height: 10, borderRadius: 5, background: RC_ZONES.base.color, animation: 'rc-pulse-soft 1.2s ease-in-out infinite' }} />
            Garmin 동기화 중…
          </div>
        )}

        {/* HERO */}
        <Hero session={session} theme={theme} onStart={() => onNav && onNav('workout')} />

        {/* SCORE CHIPS */}
        <RCSectionHeader theme={theme} title="오늘의 상태" action={<span>자세히</span>} />
        <div style={{ padding: '0 20px', display: 'flex', gap: 10 }}>
          {Object.values(RC_SCORES).map(s => (
            <RCScoreChip key={s.key} score={s} theme={theme}
              expanded={expanded === s.key}
              onToggle={() => setExpanded(expanded === s.key ? null : s.key)} />
          ))}
        </div>
        {expanded && (
          <div style={{ marginTop: 10 }}>
            <RCScoreExplain score={RC_SCORES[expanded]} theme={theme} />
          </div>
        )}

        {/* YESTERDAY */}
        <RCSectionHeader theme={theme} title="어제 완료" action={<span style={{ color: t.textFaint }}>4월 20일</span>} />
        <div style={{ padding: '0 20px' }}>
          <RCCard theme={theme} padding={16} radius={18} onClick={() => onNav && onNav('workout-done')}>
            <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
              <div style={{
                width: 42, height: 42, borderRadius: 12, flexShrink: 0,
                background: RC_ZONES[RC_YESTERDAY.zone].softStrong,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <RCIcon name="check" size={20} color={RC_ZONES[RC_YESTERDAY.zone].color} />
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 4 }}>
                  <div style={{ fontSize: 14, fontWeight: 600, color: t.text }}>{RC_YESTERDAY.title}</div>
                  <div className="rc-mono" style={{ fontSize: 12, color: RC_ZONES.base.color, fontWeight: 600 }}>
                    ●︎ {RC_YESTERDAY.matchScore}%
                  </div>
                </div>
                <div className="rc-mono" style={{ fontSize: 13, color: t.textDim, letterSpacing: -0.2 }}>
                  {RC_YESTERDAY.distanceKm} km · {RC_YESTERDAY.avgPace} · {RC_YESTERDAY.durationMin}분
                </div>
              </div>
            </div>
          </RCCard>
        </div>

        {/* COACH NOTE */}
        <RCSectionHeader theme={theme} title="코치 노트" />
        <div style={{ padding: '0 20px' }}>
          <div style={{
            padding: '14px 16px', borderRadius: 14,
            background: t.card, border: `0.5px solid ${t.border}`,
            borderLeft: `3px solid ${RC_ZONES[session.zone].color}`,
            fontSize: 13.5, lineHeight: 1.6, color: t.textDim,
          }}>
            "{session.rationale}"
          </div>
        </div>

        {/* ACWR quick */}
        <RCSectionHeader theme={theme} title="주간 안전 지표" action={<span>추이 →</span>} />
        <div style={{ padding: '0 20px' }}>
          <RCCard theme={theme} padding={16} radius={16}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
              <div>
                <div style={{ fontSize: 11, fontWeight: 600, letterSpacing: 0.6, color: t.textFaint, textTransform: 'uppercase' }}>ACWR</div>
                <div style={{ display: 'flex', alignItems: 'baseline', gap: 6, marginTop: 3 }}>
                  <span className="rc-mono" style={{ fontSize: 22, fontWeight: 600, color: t.text }}>1.08</span>
                  <span style={{ fontSize: 12, color: RC_ZONES.base.color, fontWeight: 600 }}>안전</span>
                </div>
              </div>
              <div style={{ fontSize: 11, color: t.textFaint, textAlign: 'right', lineHeight: 1.4 }}>
                급성 43km<br/>만성 40km
              </div>
            </div>
            {/* mini scale */}
            <div style={{ height: 6, borderRadius: 3, background: t.divider, position: 'relative', overflow: 'hidden' }}>
              <div style={{ position: 'absolute', left: '53%', top: -2, bottom: -2, width: 2, background: t.text, borderRadius: 2 }} />
              <div style={{ position: 'absolute', inset: 0,
                background: `linear-gradient(to right, ${RC_ZONES.recovery.color}40 0%, ${RC_ZONES.base.color}60 40%, ${RC_ZONES.threshold.color}60 70%, ${RC_ZONES.interval.color}60 100%)` }} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6, fontSize: 9, color: t.textFaint, fontWeight: 600 }}>
              <span>0.8</span><span>1.0</span><span>1.3</span><span>1.5</span>
            </div>
          </RCCard>
        </div>
      </div>

      {/* FAB — icon-only circle, docked above the floating Liquid Glass tab bar. */}
      <button onClick={() => onNav && onNav('feedback')}
        title="피드백 입력"
        style={{
          position: 'absolute', bottom: 100, right: 20, zIndex: 35,
          width: 52, height: 52, borderRadius: 26,
          background: t.text, color: t.bg, border: 'none',
          cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          boxShadow: theme === 'dark'
            ? '0 14px 32px rgba(0,0,0,0.55), 0 1px 0 rgba(255,255,255,0.18) inset'
            : '0 14px 32px rgba(11,18,32,0.22), 0 1px 0 rgba(255,255,255,0.25) inset',
        }}>
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
          <path d="M4 5h12v8H8l-3 3v-3H4V5z" stroke={t.bg} strokeWidth="1.8" strokeLinejoin="round"/>
          <path d="M10 5v4M8 7h4" stroke={t.bg} strokeWidth="1.8" strokeLinecap="round"/>
        </svg>
      </button>
    </div>
  );
}

Object.assign(window, { RCHomeScreen, RCScoreChip, RCScoreExplain });
