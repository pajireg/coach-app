// workout.jsx — Workout Detail screen

function RCWorkoutScreen({ theme = 'dark', onBack, sessionKey = 'threshold', completed = false }) {
  const t = rcTokens(theme);
  const session = RC_SESSIONS[sessionKey] || RC_SESSIONS.threshold;
  const z = RC_ZONES[session.zone];

  const backBtn = (
    <button onClick={onBack} style={{ width: 36, height: 36, borderRadius: 18,
      background: theme === 'dark' ? 'rgba(255,255,255,0.10)' : 'rgba(255,255,255,0.6)',
      backdropFilter: 'blur(20px) saturate(180%)', WebkitBackdropFilter: 'blur(20px) saturate(180%)',
      border: `0.5px solid ${t.borderStrong}`,
      display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', color: t.textDim }}>
      <RCIcon name="chevronLeft" size={16} />
    </button>
  );
  const infoBtn = (
    <button style={{ width: 36, height: 36, borderRadius: 18,
      background: theme === 'dark' ? 'rgba(255,255,255,0.10)' : 'rgba(255,255,255,0.6)',
      backdropFilter: 'blur(20px) saturate(180%)', WebkitBackdropFilter: 'blur(20px) saturate(180%)',
      border: `0.5px solid ${t.borderStrong}`,
      display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', color: t.textDim }}>
      <RCIcon name="info" size={16} />
    </button>
  );

  return (
    <RCScreen theme={theme} navTitle={session.titleKo} leading={backBtn} trailing={infoBtn}>
        {/* Hero */}
        <div style={{ padding: '0 20px 18px' }}>
          <div style={{ fontSize: 12, color: t.textFaint, fontWeight: 500, marginBottom: 8 }}>4월 21일 · 수요일</div>
          <div style={{ marginBottom: 10 }}><RCZoneBadge zone={session.zone} size="md" /></div>
          <div style={{ fontSize: 30, fontWeight: 700, color: t.text, letterSpacing: -0.6, marginBottom: 4 }}>{session.titleKo}</div>
          <div className="rc-mono" style={{ fontSize: 15, color: t.textDim, letterSpacing: -0.2 }}>
            {session.durationMin}분 · {session.distanceKm}km · {session.primaryTarget}
          </div>
        </div>

        {/* Rationale */}
        <div style={{ padding: '0 20px 16px' }}>
          <div style={{
            padding: '14px 16px', borderRadius: 14,
            background: t.card, border: `0.5px solid ${t.border}`,
            borderLeft: `3px solid ${z.color}`,
          }}>
            <div style={{ fontSize: 10, fontWeight: 700, color: z.color, letterSpacing: 1, textTransform: 'uppercase', marginBottom: 6 }}>코치 노트</div>
            <div style={{ fontSize: 13.5, lineHeight: 1.6, color: t.text }}>{session.rationale}</div>
          </div>
        </div>

        {/* Steps — timeline */}
        <RCSectionHeader theme={theme} title="세션 구성" action={<span className="rc-mono" style={{ color: t.textDim }}>{session.durationMin}분</span>} />
        <div style={{ padding: '0 20px' }}>
          {session.steps.map((step, i) => {
            const sz = RC_ZONES[step.kind] || RC_ZONES[session.zone] || RC_ZONES.rest;
            const isLast = i === session.steps.length - 1;
            return (
              <div key={i} style={{ display: 'flex', gap: 14, position: 'relative' }}>
                <div style={{ width: 12, flexShrink: 0, position: 'relative', paddingTop: 18 }}>
                  <div style={{ width: 10, height: 10, borderRadius: 5, background: sz.color, position: 'relative', zIndex: 2 }} />
                  {!isLast && <div style={{ position: 'absolute', left: 4.75, top: 28, bottom: -10, width: 0.5, background: t.borderStrong }} />}
                </div>
                <div style={{ flex: 1, marginBottom: 10, padding: 14, borderRadius: 12,
                  background: t.card, border: `0.5px solid ${t.border}` }}>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
                    <div style={{ fontSize: 14, fontWeight: 600, color: t.text }}>{step.label}</div>
                    <div className="rc-mono" style={{ fontSize: 13, color: t.textDim, fontWeight: 600 }}>{step.durationMin}분</div>
                  </div>
                  <div style={{ display: 'flex', gap: 14, fontSize: 12 }}>
                    <div>
                      <span style={{ color: t.textFaint, marginRight: 4 }}>페이스</span>
                      <span className="rc-mono" style={{ color: sz.color, fontWeight: 600 }}>{step.pace}</span>
                    </div>
                    <div>
                      <span style={{ color: t.textFaint, marginRight: 4 }}>HR</span>
                      <span className="rc-mono" style={{ color: t.text, fontWeight: 600 }}>{step.hr}</span>
                    </div>
                    {step.reps && (
                      <div>
                        <span style={{ color: t.textFaint, marginRight: 4 }}>반복</span>
                        <span className="rc-mono" style={{ color: t.text, fontWeight: 600 }}>×{step.reps}</span>
                      </div>
                    )}
                  </div>
                  {step.note && (
                    <div style={{ fontSize: 11.5, color: t.textDim, marginTop: 8, paddingTop: 8, borderTop: `0.5px solid ${t.divider}` }}>
                      {step.note}
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>

        {/* Garmin badge */}
        <div style={{ padding: '8px 20px 16px' }}>
          <div style={{
            display: 'flex', alignItems: 'center', gap: 10,
            padding: '12px 14px', borderRadius: 12,
            background: RC_ZONES.base.soft, border: `0.5px solid ${RC_ZONES.base.color}40`,
          }}>
            <div style={{ width: 28, height: 28, borderRadius: 8, background: RC_ZONES.base.color,
              display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <RCIcon name="watch" size={14} color="#0B1220" />
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 12.5, fontWeight: 600, color: t.text }}>Garmin에 업로드됨</div>
              <div style={{ fontSize: 11, color: t.textDim }}>4월 21일 오전 5:30 예정</div>
            </div>
            <RCIcon name="check" size={16} color={RC_ZONES.base.color} />
          </div>
        </div>

        {/* Actual vs Planned (completed) */}
        {completed && (
          <>
            <RCSectionHeader theme={theme} title="실제 vs 계획" />
            <div style={{ padding: '0 20px' }}>
              <RCCard theme={theme} padding={16} radius={16}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 12, marginBottom: 14 }}>
                  <CompareStat theme={theme} label="거리" planned="8.5" actual="8.6" unit="km" delta="+0.1" good />
                  <CompareStat theme={theme} label="평균 페이스" planned="4:32" actual="4:34" unit="/km" delta="+2s" good />
                  <CompareStat theme={theme} label="시간" planned="40" actual="41" unit="분" delta="+1" good />
                </div>
                <div style={{ paddingTop: 14, borderTop: `0.5px solid ${t.divider}` }}>
                  <div style={{ fontSize: 10, fontWeight: 700, color: t.textFaint, letterSpacing: 0.8, textTransform: 'uppercase', marginBottom: 8 }}>HR 존 분포</div>
                  <HRZoneBar theme={theme} zones={[
                    { k: 'Z1', pct: 0.08, c: RC_ZONES.recovery.color },
                    { k: 'Z2', pct: 0.22, c: RC_ZONES.base.color },
                    { k: 'Z3', pct: 0.15, c: '#BFC8D6' },
                    { k: 'Z4', pct: 0.42, c: RC_ZONES.threshold.color },
                    { k: 'Z5', pct: 0.13, c: RC_ZONES.interval.color },
                  ]} />
                </div>
              </RCCard>
            </div>
          </>
        )}

      {/* Action bar */}
      {!completed && (
        <div style={{
          position: 'absolute', bottom: 28, left: 20, right: 20, zIndex: 30,
          display: 'flex', gap: 10,
        }}>
          <RCButton theme={theme} variant="secondary" size="md" style={{ flex: 1 }}>연기</RCButton>
          <RCButton theme={theme} size="md" style={{ flex: 2 }}>
            시작 <RCIcon name="chevronRight" size={14} />
          </RCButton>
        </div>
      )}
    </RCScreen>
  );
}

function CompareStat({ label, planned, actual, unit, delta, good, theme }) {
  const t = rcTokens(theme);
  return (
    <div>
      <div style={{ fontSize: 10, fontWeight: 700, color: t.textFaint, letterSpacing: 0.6, textTransform: 'uppercase', marginBottom: 4 }}>{label}</div>
      <div className="rc-mono" style={{ fontSize: 18, fontWeight: 600, color: t.text, letterSpacing: -0.3 }}>
        {actual}<span style={{ fontSize: 10, color: t.textFaint, marginLeft: 1 }}>{unit}</span>
      </div>
      <div className="rc-mono" style={{ fontSize: 10, color: t.textFaint, marginTop: 2 }}>계획 {planned}{unit}</div>
      <div className="rc-mono" style={{ fontSize: 10, color: good ? RC_ZONES.base.color : RC_ZONES.interval.color, fontWeight: 600, marginTop: 1 }}>
        {delta}
      </div>
    </div>
  );
}

function HRZoneBar({ zones, theme }) {
  const t = rcTokens(theme);
  return (
    <div>
      <div style={{ display: 'flex', height: 14, borderRadius: 3, overflow: 'hidden' }}>
        {zones.map(z => <div key={z.k} style={{ flex: z.pct, background: z.c }} />)}
      </div>
      <div style={{ display: 'flex', marginTop: 6, gap: 10 }}>
        {zones.map(z => (
          <div key={z.k} style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
            <div style={{ width: 6, height: 6, borderRadius: 3, background: z.c }} />
            <span className="rc-mono" style={{ fontSize: 10, color: t.textDim, fontWeight: 600 }}>{z.k}</span>
            <span className="rc-mono" style={{ fontSize: 10, color: t.textFaint }}>{Math.round(z.pct * 100)}%</span>
          </div>
        ))}
      </div>
    </div>
  );
}

Object.assign(window, { RCWorkoutScreen });
