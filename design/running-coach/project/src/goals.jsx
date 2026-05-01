// goals.jsx — Goals & Availability screen

function RCGoalsScreen({ theme = 'dark' }) {
  const t = rcTokens(theme);
  const g = RC_USER.goalRace;
  const phases = [
    { key: 'base',  label: 'Base',  ko: '기초' },
    { key: 'build', label: 'Build', ko: '빌드' },
    { key: 'peak',  label: 'Peak',  ko: '피크' },
    { key: 'taper', label: 'Taper', ko: '테이퍼' },
  ];
  const phaseIdx = phases.findIndex(p => p.key === RC_USER.phase);
  const [avail, setAvail] = React.useState({
    mon: true, tue: false, wed: true, thu: true, fri: false, sat: true, sun: true,
  });
  const [maxDur, setMaxDur] = React.useState(90);
  const days = [['mon','월'],['tue','화'],['wed','수'],['thu','목'],['fri','금'],['sat','토'],['sun','일']];

  return (
    <RCScreen theme={theme} eyebrow="레이스 · 가용성" title="목표" navTitle="목표">
        {/* Race card */}
        <div style={{ padding: '0 20px 16px' }}>
          <div style={{ padding: 2, borderRadius: 20,
            background: `linear-gradient(135deg, ${RC_ZONES.threshold.color}50, ${RC_ZONES.threshold.color}10)` }}>
            <div style={{ borderRadius: 18, background: t.card, padding: 20 }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14 }}>
                <span style={{ fontSize: 10, fontWeight: 700, color: RC_ZONES.threshold.color, letterSpacing: 1, textTransform: 'uppercase' }}>A 목표 레이스</span>
                <span style={{ fontSize: 11, color: t.textFaint, fontWeight: 500 }}>2026년 6월 14일</span>
              </div>
              <div style={{ fontSize: 22, fontWeight: 700, color: t.text, letterSpacing: -0.3, marginBottom: 16 }}>{g.name}</div>
              <div style={{ display: 'flex', gap: 0 }}>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 10, fontWeight: 700, color: t.textFaint, letterSpacing: 0.6, textTransform: 'uppercase', marginBottom: 3 }}>거리</div>
                  <div className="rc-mono" style={{ fontSize: 20, fontWeight: 500, color: t.text }}>21.1<span style={{ fontSize: 11, color: t.textFaint, marginLeft: 1 }}>km</span></div>
                </div>
                <div style={{ width: 0.5, background: t.divider, margin: '0 12px' }} />
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 10, fontWeight: 700, color: t.textFaint, letterSpacing: 0.6, textTransform: 'uppercase', marginBottom: 3 }}>남은 주</div>
                  <div className="rc-mono" style={{ fontSize: 20, fontWeight: 500, color: t.text }}>{g.weeksLeft}<span style={{ fontSize: 11, color: t.textFaint, marginLeft: 1 }}>주</span></div>
                </div>
                <div style={{ width: 0.5, background: t.divider, margin: '0 12px' }} />
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 10, fontWeight: 700, color: t.textFaint, letterSpacing: 0.6, textTransform: 'uppercase', marginBottom: 3 }}>목표 페이스</div>
                  <div className="rc-mono" style={{ fontSize: 20, fontWeight: 500, color: t.text }}>4:42<span style={{ fontSize: 11, color: t.textFaint, marginLeft: 1 }}>/km</span></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Phase progress */}
        <RCSectionHeader theme={theme} title="트레이닝 단계" />
        <div style={{ padding: '0 20px 16px' }}>
          <RCCard theme={theme} padding={16} radius={16}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
              {phases.map((p, i) => (
                <div key={p.key} style={{ textAlign: 'center', flex: 1 }}>
                  <div style={{ fontSize: 12, fontWeight: 700, color: i === phaseIdx ? t.text : t.textFaint, marginBottom: 2 }}>{p.label}</div>
                  <div style={{ fontSize: 10, color: i === phaseIdx ? RC_ZONES.threshold.color : t.textFaint }}>{p.ko}</div>
                </div>
              ))}
            </div>
            <div style={{ height: 6, borderRadius: 3, background: t.divider, position: 'relative', overflow: 'hidden' }}>
              <div style={{ height: '100%', width: `${((phaseIdx + RC_USER.phaseProgress) / phases.length) * 100}%`,
                background: RC_ZONES.threshold.color, borderRadius: 3, transition: 'width 0.5s' }} />
            </div>
            <div style={{ fontSize: 11, color: t.textDim, marginTop: 10 }}>
              Build 단계 2/4주 차 · 강도와 거리 점진적 증가 중
            </div>
          </RCCard>
        </div>

        {/* Availability */}
        <RCSectionHeader theme={theme} title="주간 가용성" action={<span style={{ color: t.textFaint }}>탭하여 토글</span>} />
        <div style={{ padding: '0 20px 16px' }}>
          <RCCard theme={theme} padding={16} radius={16}>
            <div style={{ display: 'flex', gap: 6, marginBottom: 18 }}>
              {days.map(([k, ko]) => (
                <button key={k} onClick={() => setAvail(a => ({ ...a, [k]: !a[k] }))} style={{
                  flex: 1, height: 56, borderRadius: 10, cursor: 'pointer',
                  background: avail[k] ? RC_ZONES.base.softStrong : t.bg,
                  border: `0.5px solid ${avail[k] ? RC_ZONES.base.color + '50' : t.border}`,
                  display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 2,
                }}>
                  <span style={{ fontSize: 11, fontWeight: 700, color: avail[k] ? RC_ZONES.base.color : t.textFaint }}>{ko}</span>
                  <RCIcon name={avail[k] ? 'check' : 'close'} size={12} color={avail[k] ? RC_ZONES.base.color : t.textMuted} stroke={2.5} />
                </button>
              ))}
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 8 }}>
                <span style={{ fontSize: 13, fontWeight: 600, color: t.text }}>최대 세션 시간</span>
                <span className="rc-mono" style={{ fontSize: 16, fontWeight: 600, color: t.text }}>{maxDur}<span style={{ fontSize: 10, color: t.textFaint, marginLeft: 2 }}>분</span></span>
              </div>
              <input type="range" min="30" max="180" step="15" value={maxDur}
                onChange={e => setMaxDur(+e.target.value)}
                style={{ width: '100%', accentColor: RC_ZONES.threshold.color }}/>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 10, color: t.textFaint, marginTop: 2 }}>
                <span>30분</span><span>180분</span>
              </div>
            </div>
          </RCCard>
        </div>
    </RCScreen>
  );
}

Object.assign(window, { RCGoalsScreen });
