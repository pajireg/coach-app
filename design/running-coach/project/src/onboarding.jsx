// onboarding.jsx — Onboarding flow (welcome → Garmin → goal → availability → generating)

function RCOnboardingScreen({ theme = 'dark', step = 0, onStepChange, onComplete }) {
  const t = rcTokens(theme);
  const steps = ['welcome', 'garmin', 'goal', 'availability', 'generating'];
  const current = steps[step] || 'welcome';

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      {/* progress bar */}
      <div style={{ padding: '10px 20px 4px', display: 'flex', gap: 4 }}>
        {steps.slice(0, 4).map((s, i) => (
          <div key={s} style={{
            flex: 1, height: 3, borderRadius: 2,
            background: i <= step ? t.text : t.borderStrong,
            transition: 'background 0.3s',
          }} />
        ))}
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '40px 24px 24px', display: 'flex', flexDirection: 'column' }}>
        {current === 'welcome' && (
          <div style={{ display: 'flex', flexDirection: 'column', flex: 1, justifyContent: 'center', alignItems: 'flex-start' }}>
            <div style={{ width: 56, height: 56, borderRadius: 28, marginBottom: 24,
              background: `linear-gradient(135deg, ${RC_ZONES.threshold.color}, ${RC_ZONES.interval.color})`,
              display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <svg width="28" height="28" viewBox="0 0 28 28"><path d="M8 4l16 10-16 10V4z" fill="#0B1220"/></svg>
            </div>
            <div style={{ fontSize: 32, fontWeight: 700, color: t.text, letterSpacing: -0.8, lineHeight: 1.15, marginBottom: 12 }}>
              당신의<br/>러닝 코치입니다
            </div>
            <div style={{ fontSize: 15, color: t.textDim, lineHeight: 1.55, marginBottom: 32 }}>
              Garmin 데이터를 읽고 주간 플랜을 만들어드려요. 과훈련을 막는 안전 규칙이 항상 적용됩니다.
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 32 }}>
              {[
                ['01', '실제 회복 상태에 따라 매주 조정'],
                ['02', 'ACWR 기반 부상 리스크 관리'],
                ['03', 'Garmin · Google Calendar 연동'],
              ].map(([n, txt]) => (
                <div key={n} style={{ display: 'flex', gap: 14, alignItems: 'flex-start' }}>
                  <span className="rc-mono" style={{ fontSize: 12, color: t.textFaint, fontWeight: 600, marginTop: 1 }}>{n}</span>
                  <span style={{ fontSize: 13, color: t.text, lineHeight: 1.5, flex: 1 }}>{txt}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {current === 'garmin' && (
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 11, fontWeight: 700, color: RC_ZONES.recovery.color, letterSpacing: 1, textTransform: 'uppercase', marginBottom: 12 }}>STEP 01 · 연결</div>
            <div style={{ fontSize: 26, fontWeight: 700, color: t.text, letterSpacing: -0.5, marginBottom: 10 }}>Garmin과 연결해주세요</div>
            <div style={{ fontSize: 13.5, color: t.textDim, lineHeight: 1.55, marginBottom: 28 }}>
              러닝 기록 · 수면 · HRV · 컨디션 데이터를 읽어 코치가 매일 상태를 파악합니다.
            </div>
            <div style={{ padding: 20, borderRadius: 18, background: t.card, border: `0.5px solid ${t.border}`, marginBottom: 16 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
                <div style={{ width: 48, height: 48, borderRadius: 12, background: t.cardHi,
                  display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <RCIcon name="watch" size={22} color={t.text} />
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 15, fontWeight: 600, color: t.text }}>Garmin Connect</div>
                  <div style={{ fontSize: 11.5, color: t.textDim, marginTop: 2 }}>읽기 권한만 요청합니다</div>
                </div>
              </div>
            </div>
            <div style={{ fontSize: 11, color: t.textFaint, lineHeight: 1.5 }}>
              연결하지 않아도 진행할 수 있지만, 플랜 정확도가 크게 떨어집니다.
            </div>
          </div>
        )}

        {current === 'goal' && (
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 11, fontWeight: 700, color: RC_ZONES.threshold.color, letterSpacing: 1, textTransform: 'uppercase', marginBottom: 12 }}>STEP 02 · 목표</div>
            <div style={{ fontSize: 26, fontWeight: 700, color: t.text, letterSpacing: -0.5, marginBottom: 10 }}>어떤 레이스를 준비하세요?</div>
            <div style={{ fontSize: 13.5, color: t.textDim, lineHeight: 1.55, marginBottom: 24 }}>
              목표 거리에 맞춰 주기화된 플랜을 생성합니다.
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
              {[
                { d: '5K',   sub: '5 km',     c: RC_ZONES.recovery.color },
                { d: '10K',  sub: '10 km',    c: RC_ZONES.base.color },
                { d: 'Half', sub: '21.1 km',  c: RC_ZONES.threshold.color, active: true },
                { d: 'Full', sub: '42.2 km',  c: RC_ZONES.interval.color },
              ].map(o => (
                <div key={o.d} style={{
                  padding: 18, borderRadius: 14, cursor: 'pointer',
                  background: o.active ? o.c + '18' : t.card,
                  border: `0.5px solid ${o.active ? o.c + '80' : t.border}`,
                }}>
                  <div style={{ fontSize: 22, fontWeight: 700, color: o.active ? o.c : t.text, letterSpacing: -0.3 }}>{o.d}</div>
                  <div className="rc-mono" style={{ fontSize: 11, color: t.textDim, marginTop: 2 }}>{o.sub}</div>
                </div>
              ))}
            </div>
            <div style={{ marginTop: 20, padding: 14, borderRadius: 12, background: t.card, border: `0.5px solid ${t.border}` }}>
              <div style={{ fontSize: 11, fontWeight: 700, color: t.textFaint, letterSpacing: 0.6, textTransform: 'uppercase', marginBottom: 4 }}>레이스 날짜</div>
              <div className="rc-mono" style={{ fontSize: 16, fontWeight: 600, color: t.text }}>2026. 06. 14</div>
            </div>
          </div>
        )}

        {current === 'availability' && (
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: 11, fontWeight: 700, color: RC_ZONES.base.color, letterSpacing: 1, textTransform: 'uppercase', marginBottom: 12 }}>STEP 03 · 가용성</div>
            <div style={{ fontSize: 26, fontWeight: 700, color: t.text, letterSpacing: -0.5, marginBottom: 10 }}>일주일에 며칠 뛸 수 있나요?</div>
            <div style={{ fontSize: 13.5, color: t.textDim, lineHeight: 1.55, marginBottom: 24 }}>
              가능한 요일을 선택해주세요. 강도는 코치가 분배합니다.
            </div>
            <div style={{ display: 'flex', gap: 6 }}>
              {[['월',1],['화',0],['수',1],['목',1],['금',0],['토',1],['일',1]].map(([d, a], i) => (
                <div key={i} style={{
                  flex: 1, height: 64, borderRadius: 12, cursor: 'pointer',
                  background: a ? RC_ZONES.base.softStrong : t.card,
                  border: `0.5px solid ${a ? RC_ZONES.base.color + '50' : t.border}`,
                  display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 3,
                }}>
                  <span style={{ fontSize: 13, fontWeight: 700, color: a ? RC_ZONES.base.color : t.textFaint }}>{d}</span>
                  <RCIcon name={a ? 'check' : 'close'} size={12} color={a ? RC_ZONES.base.color : t.textMuted} stroke={2.5} />
                </div>
              ))}
            </div>
            <div style={{ marginTop: 20, padding: 14, borderRadius: 12, background: t.card, border: `0.5px solid ${t.border}` }}>
              <div style={{ fontSize: 12, color: t.textDim, marginBottom: 8 }}>최대 세션 시간</div>
              <div className="rc-mono" style={{ fontSize: 20, fontWeight: 500, color: t.text }}>90<span style={{ fontSize: 12, color: t.textFaint, marginLeft: 2 }}>분</span></div>
            </div>
          </div>
        )}

        {current === 'generating' && (
          <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
            <div style={{ position: 'relative', width: 64, height: 64, marginBottom: 24 }}>
              <div style={{ position: 'absolute', inset: 0, borderRadius: 32, border: `2px solid ${t.divider}` }} />
              <div style={{ position: 'absolute', inset: 0, borderRadius: 32,
                border: `2px solid ${RC_ZONES.threshold.color}`, borderRightColor: 'transparent', borderBottomColor: 'transparent',
                animation: 'rc-spin 1s linear infinite' }} />
              <style>{`@keyframes rc-spin { to { transform: rotate(360deg) } }`}</style>
            </div>
            <div style={{ fontSize: 22, fontWeight: 700, color: t.text, letterSpacing: -0.3, marginBottom: 8 }}>첫 플랜 만드는 중…</div>
            <div style={{ fontSize: 13, color: t.textDim, lineHeight: 1.6, maxWidth: 260 }}>
              데이터를 분석해 8주 주기화 플랜을 생성하고 있어요.
            </div>
            <div style={{ marginTop: 28, display: 'flex', flexDirection: 'column', gap: 8, alignItems: 'flex-start' }}>
              {['회복 상태 분석', 'ACWR 안전 범위 계산', '주기화 템플릿 선택', '세션 분배 중'].map((l, i) => (
                <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 10, fontSize: 12, color: i < 3 ? t.textDim : t.text }}>
                  <RCIcon name={i < 3 ? 'check' : 'dot'} size={12} color={i < 3 ? RC_ZONES.base.color : RC_ZONES.threshold.color} stroke={2.5} />
                  {l}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* bottom action */}
      <div style={{ padding: '0 24px 28px', display: 'flex', gap: 10 }}>
        {step > 0 && current !== 'generating' && (
          <RCButton theme={theme} variant="secondary" size="md" onClick={() => onStepChange(step - 1)}>이전</RCButton>
        )}
        {current !== 'generating' && (
          <RCButton theme={theme} size="md" full onClick={() => {
            if (step < steps.length - 1) onStepChange(step + 1);
            else onComplete && onComplete();
          }}>
            {current === 'welcome' ? '시작하기' : current === 'availability' ? '플랜 생성' : '다음'}
          </RCButton>
        )}
        {current === 'generating' && (
          <RCButton theme={theme} size="md" full onClick={onComplete}>완료</RCButton>
        )}
      </div>
    </div>
  );
}

Object.assign(window, { RCOnboardingScreen });
