// trends.jsx — Trends screen with ACWR chart + pace zones + PRs

function RCTrendsScreen({ theme = 'dark' }) {
  const t = rcTokens(theme);
  const weeks = RC_TREND_WEEKS;
  const maxKm = Math.max(...weeks.map(w => w.km));
  const chartH = 160;
  const chartW = 340;
  const barW = (chartW - 20) / weeks.length;
  const acwrMin = 0.6, acwrMax = 1.6;
  const acwrY = (v) => chartH - ((v - acwrMin) / (acwrMax - acwrMin)) * chartH;
  const capY = acwrY(1.5);

  return (
    <RCScreen theme={theme} eyebrow="최근 12주" title="추이" navTitle="추이">
        {/* ACWR chart */}
        <div style={{ padding: '0 20px 16px' }}>
          <RCCard theme={theme} padding={16} radius={18}>
            <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 14 }}>
              <div>
                <div style={{ fontSize: 11, fontWeight: 600, letterSpacing: 0.6, color: t.textFaint, textTransform: 'uppercase' }}>주간 거리 · ACWR</div>
                <div className="rc-mono" style={{ fontSize: 24, fontWeight: 600, color: t.text, marginTop: 2 }}>
                  43<span style={{ fontSize: 13, color: t.textDim, marginLeft: 2 }}>km · 이번 주</span>
                </div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: 10, fontWeight: 600, color: t.textFaint, letterSpacing: 0.4, textTransform: 'uppercase' }}>ACWR</div>
                <div className="rc-mono" style={{ fontSize: 16, fontWeight: 600, color: RC_ZONES.base.color }}>1.08</div>
              </div>
            </div>

            <svg viewBox={`0 0 ${chartW} ${chartH + 24}`} width="100%" style={{ display: 'block' }}>
              {/* cap line 1.5 */}
              <line x1="0" y1={capY} x2={chartW} y2={capY} stroke={RC_ZONES.interval.color} strokeWidth="1" strokeDasharray="3 3" opacity="0.7"/>
              <text x={chartW - 2} y={capY - 4} fontSize="9" fill={RC_ZONES.interval.color} textAnchor="end" fontFamily={RC_FONT_MONO}>1.5 상한</text>
              {/* safe band 0.8–1.3 */}
              <rect x="0" y={acwrY(1.3)} width={chartW} height={acwrY(0.8) - acwrY(1.3)} fill={RC_ZONES.base.color} opacity="0.06"/>

              {/* bars */}
              {weeks.map((w, i) => {
                const h = (w.km / 60) * chartH * 0.9;
                const x = i * barW + 4;
                const y = chartH - h;
                const isNow = i === weeks.length - 1;
                return (
                  <g key={i}>
                    <rect x={x} y={y} width={barW - 8} height={h} rx="2"
                      fill={isNow ? t.text : t.borderStrong} opacity={isNow ? 1 : 0.5}/>
                  </g>
                );
              })}

              {/* ACWR line */}
              <polyline
                points={weeks.map((w, i) => `${i * barW + barW/2},${acwrY(w.acwr)}`).join(' ')}
                fill="none" stroke={RC_ZONES.threshold.color} strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
              {weeks.map((w, i) => (
                <circle key={i} cx={i * barW + barW/2} cy={acwrY(w.acwr)} r={i === weeks.length - 1 ? 3.5 : 2}
                  fill={RC_ZONES.threshold.color} stroke={t.card} strokeWidth={i === weeks.length - 1 ? 2 : 0}/>
              ))}

              {/* x labels (sparse) */}
              {weeks.map((w, i) => (i % 3 === 0 || i === weeks.length - 1) && (
                <text key={i} x={i * barW + barW/2} y={chartH + 16} fontSize="9" fill={t.textFaint} textAnchor="middle" fontFamily={RC_FONT_MONO}>
                  {w.w}
                </text>
              ))}
            </svg>

            <div style={{ display: 'flex', gap: 14, marginTop: 10, fontSize: 11 }}>
              <LegendItem theme={theme} color={t.text} label="주간 거리 (km)" square />
              <LegendItem theme={theme} color={RC_ZONES.threshold.color} label="ACWR" />
              <LegendItem theme={theme} color={RC_ZONES.interval.color} label="1.5 상한" dashed />
            </div>
          </RCCard>
        </div>

        {/* Pace zones */}
        <RCSectionHeader theme={theme} title="페이스 존" action={<span style={{ color: t.textFaint }} className="rc-mono">업데이트 2주 전</span>} />
        <div style={{ padding: '0 20px 16px' }}>
          <RCCard theme={theme} padding={0} radius={16}>
            {Object.values(RC_PACE_ZONES).map((z, i, a) => (
              <div key={z.label} style={{
                display: 'flex', alignItems: 'center', gap: 12,
                padding: '12px 14px', borderBottom: i < a.length - 1 ? `0.5px solid ${t.divider}` : 'none',
              }}>
                <div style={{ fontSize: 12, fontWeight: 600, color: t.text, width: 72 }}>{z.label}</div>
                <div style={{ fontSize: 11, color: t.textFaint, width: 42 }}>{z.ko}</div>
                <div className="rc-mono" style={{ flex: 1, fontSize: 12, color: t.textDim, textAlign: 'right' }}>{z.range}</div>
                <div className="rc-mono" style={{ fontSize: 13, color: t.text, fontWeight: 600, width: 52, textAlign: 'right' }}>{z.target}</div>
              </div>
            ))}
          </RCCard>
        </div>

        {/* PRs */}
        <RCSectionHeader theme={theme} title="개인 기록" />
        <div style={{ padding: '0 20px', display: 'flex', flexDirection: 'column', gap: 8 }}>
          {RC_PRS.map(pr => (
            <div key={pr.dist} style={{
              display: 'flex', alignItems: 'center', gap: 14,
              padding: '14px 16px', borderRadius: 14,
              background: t.card, border: `0.5px solid ${t.border}`,
            }}>
              <div style={{ fontSize: 13, fontWeight: 700, color: t.text, width: 56, letterSpacing: 0.3 }}>{pr.dist}</div>
              <div className="rc-mono" style={{ flex: 1, fontSize: 20, fontWeight: 500, color: t.text, letterSpacing: -0.3 }}>{pr.time}</div>
              {pr.deltaDir !== 'none' && (
                <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                  <RCIcon name={pr.deltaDir === 'up' ? 'arrowUp' : 'arrowDown'} size={12}
                    color={pr.deltaDir === 'up' ? RC_ZONES.base.color : RC_ZONES.interval.color}/>
                  <span className="rc-mono" style={{ fontSize: 12, fontWeight: 600,
                    color: pr.deltaDir === 'up' ? RC_ZONES.base.color : RC_ZONES.interval.color }}>{pr.delta}</span>
                </div>
              )}
              <div style={{ fontSize: 11, color: t.textFaint, width: 64, textAlign: 'right' }}>{pr.when}</div>
            </div>
          ))}
        </div>
    </RCScreen>
  );
}

function LegendItem({ color, label, square, dashed, theme }) {
  const t = rcTokens(theme);
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
      {square ? (
        <div style={{ width: 8, height: 8, background: color, borderRadius: 1 }} />
      ) : dashed ? (
        <svg width="14" height="2"><line x1="0" y1="1" x2="14" y2="1" stroke={color} strokeWidth="1.5" strokeDasharray="2 2"/></svg>
      ) : (
        <div style={{ width: 10, height: 2, background: color, borderRadius: 1 }} />
      )}
      <span style={{ fontSize: 10, color: t.textDim, fontWeight: 500 }}>{label}</span>
    </div>
  );
}

Object.assign(window, { RCTrendsScreen });
