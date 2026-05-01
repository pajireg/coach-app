// settings.jsx — Settings screen

function RCSettingsRow({ label, detail, control, theme, last }) {
  const t = rcTokens(theme);
  return (
    <div style={{
      display: 'flex', alignItems: 'center', padding: '14px 16px',
      borderBottom: last ? 'none' : `0.5px solid ${t.divider}`, gap: 12,
    }}>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, fontWeight: 500, color: t.text }}>{label}</div>
        {detail && <div style={{ fontSize: 11.5, color: t.textDim, marginTop: 2, lineHeight: 1.4 }}>{detail}</div>}
      </div>
      {control}
    </div>
  );
}

function RCToggle({ on, onChange, theme }) {
  const t = rcTokens(theme);
  return (
    <button onClick={() => onChange(!on)} style={{
      width: 44, height: 26, borderRadius: 13, cursor: 'pointer', border: 'none',
      background: on ? RC_ZONES.base.color : t.borderStrong,
      position: 'relative', transition: 'background 0.2s', flexShrink: 0,
    }}>
      <div style={{
        position: 'absolute', top: 2, left: on ? 20 : 2,
        width: 22, height: 22, borderRadius: 11,
        background: '#fff', transition: 'left 0.2s',
        boxShadow: '0 1px 3px rgba(0,0,0,0.2)',
      }} />
    </button>
  );
}

function RCSettingsScreen({ theme = 'dark' }) {
  const t = rcTokens(theme);
  const [times, setTimes] = React.useState(['05:00', '17:00']);
  const [mode, setMode] = React.useState('llm_driven');
  const [garmin, setGarmin] = React.useState(true);
  const [gcal, setGcal] = React.useState(false);
  const [notifs, setNotifs] = React.useState(true);

  return (
    <RCScreen theme={theme} title="설정" navTitle="설정">
        {/* Schedule times */}
        <RCSectionHeader theme={theme} title="스케줄 시각" />
        <div style={{ padding: '0 20px 14px' }}>
          <RCCard theme={theme} padding={0} radius={16}>
            {times.map((time, i) => (
              <div key={i} style={{
                display: 'flex', alignItems: 'center', padding: '14px 16px',
                borderBottom: i < times.length ? `0.5px solid ${t.divider}` : 'none', gap: 12,
              }}>
                <div style={{ width: 28, height: 28, borderRadius: 8, background: t.cardHi,
                  display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <span style={{ fontSize: 11, fontWeight: 700, color: t.textDim }}>{i + 1}</span>
                </div>
                <div style={{ flex: 1, fontSize: 13, color: t.text }}>
                  {i === 0 ? '아침 · 주로 러닝' : '저녁 · 피드백 알림'}
                </div>
                <input type="time" value={time} onChange={e => {
                  const n = [...times]; n[i] = e.target.value; setTimes(n);
                }} className="rc-mono" style={{
                  background: t.cardHi, border: `0.5px solid ${t.border}`, borderRadius: 8,
                  color: t.text, padding: '6px 10px', fontSize: 13, fontWeight: 600,
                  colorScheme: theme === 'dark' ? 'dark' : 'light',
                }} />
              </div>
            ))}
            <div style={{ padding: '10px 16px', borderTop: `0.5px solid ${t.divider}` }}>
              <button onClick={() => setTimes([...times, '12:00'])} style={{
                background: 'none', border: 'none', color: RC_ZONES.recovery.color,
                fontSize: 13, fontWeight: 600, cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 4,
              }}>
                <RCIcon name="plus" size={12} color={RC_ZONES.recovery.color} stroke={2.5} /> 시각 추가
              </button>
            </div>
          </RCCard>
        </div>

        {/* Planner mode */}
        <RCSectionHeader theme={theme} title="플래너 모드" subtitle="주간 플랜 생성 방식" />
        <div style={{ padding: '0 20px 14px', display: 'flex', flexDirection: 'column', gap: 8 }}>
          {[
            { k: 'legacy',     title: '규칙 기반', desc: '주기화 템플릿 + 피드백 규칙으로 플랜 생성 · 예측 가능하고 안정적' },
            { k: 'llm_driven', title: 'AI 코치',   desc: 'LLM이 데이터 전체를 읽고 주간 플랜 생성 · 상황에 더 세밀하게 반응' },
          ].map(o => (
            <button key={o.k} onClick={() => setMode(o.k)} style={{
              textAlign: 'left', padding: 14, borderRadius: 14,
              background: mode === o.k ? RC_ZONES.recovery.soft : t.card,
              border: `0.5px solid ${mode === o.k ? RC_ZONES.recovery.color + '60' : t.border}`,
              cursor: 'pointer', display: 'flex', gap: 12, alignItems: 'flex-start',
            }}>
              <div style={{ width: 18, height: 18, borderRadius: 9, flexShrink: 0, marginTop: 1,
                border: `1.5px solid ${mode === o.k ? RC_ZONES.recovery.color : t.borderStrong}`,
                background: mode === o.k ? RC_ZONES.recovery.color : 'transparent',
                display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                {mode === o.k && <div style={{ width: 6, height: 6, borderRadius: 3, background: '#0B1220' }} />}
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 14, fontWeight: 600, color: t.text }}>{o.title}</div>
                <div style={{ fontSize: 12, color: t.textDim, marginTop: 3, lineHeight: 1.45 }}>{o.desc}</div>
              </div>
            </button>
          ))}
        </div>

        {/* Connections */}
        <RCSectionHeader theme={theme} title="연결 및 알림" />
        <div style={{ padding: '0 20px 14px' }}>
          <RCCard theme={theme} padding={0} radius={16}>
            <RCSettingsRow theme={theme}
              label="Garmin Connect"
              detail={garmin ? '연결됨 · 마지막 동기화 오전 7:24' : '연결 안 됨'}
              control={<RCToggle on={garmin} onChange={setGarmin} theme={theme} />} />
            <RCSettingsRow theme={theme}
              label="Google Calendar"
              detail="세션을 캘린더에 자동 추가"
              control={<RCToggle on={gcal} onChange={setGcal} theme={theme} />} />
            <RCSettingsRow theme={theme}
              label="푸시 알림"
              detail="스케줄 시간 30분 전 알림"
              control={<RCToggle on={notifs} onChange={setNotifs} theme={theme} />}
              last />
          </RCCard>
        </div>
    </RCScreen>
  );
}

Object.assign(window, { RCSettingsScreen });
