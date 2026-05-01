// weekly.jsx — Weekly Plan (3 layout variants: strip+list, calendar, timeline)

function WeekStripBadge({ day, theme, onClick, compact = false }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[day.zone];
  const isToday = day.today;
  const isDone = day.done;
  const days = { mon: '월', tue: '화', wed: '수', thu: '목', fri: '금', sat: '토', sun: '일' };
  return (
    <button onClick={onClick} style={{
      flex: 1, background: 'none', border: 'none', cursor: 'pointer',
      display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6,
      padding: compact ? '4px 0' : '6px 0',
    }}>
      <span style={{ fontSize: 10, fontWeight: 600, color: isToday ? t.text : t.textFaint, letterSpacing: 0.3 }}>
        {days[day.day]}
      </span>
      <div style={{
        width: 38, height: 38, borderRadius: 10,
        background: isToday ? z.color : (day.zone === 'rest' ? t.card : z.softStrong),
        border: isToday ? 'none' : `0.5px solid ${day.zone === 'rest' ? t.border : z.color + '40'}`,
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        position: 'relative',
      }}>
        <span className="rc-tnum" style={{
          fontSize: 14, fontWeight: 700,
          color: isToday ? '#0B1220' : (day.zone === 'rest' ? t.textDim : z.color),
        }}>{day.date}</span>
        {isDone && (
          <div style={{ position: 'absolute', top: -3, right: -3, width: 12, height: 12, borderRadius: 6,
            background: t.bg, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <RCIcon name="check" size={8} color={RC_ZONES.base.color} stroke={3} />
          </div>
        )}
      </div>
      {!compact && (
        <div style={{ width: 4, height: 4, borderRadius: 4, background: z.color, opacity: day.zone === 'rest' ? 0.3 : 1 }} />
      )}
    </button>
  );
}

function WeeklyListRow({ day, theme, onClick, onLongPress }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[day.zone];
  const muted = day.zone === 'rest';
  const [menuOpen, setMenuOpen] = React.useState(false);
  const timerRef = React.useRef(null);
  const days = { mon: 'MON', tue: 'TUE', wed: 'WED', thu: 'THU', fri: 'FRI', sat: 'SAT', sun: 'SUN' };

  const down = () => { timerRef.current = setTimeout(() => setMenuOpen(true), 480); };
  const up = () => { clearTimeout(timerRef.current); };

  return (
    <div style={{ position: 'relative' }}>
      <button onClick={onClick} onMouseDown={down} onMouseUp={up} onMouseLeave={up}
        onTouchStart={down} onTouchEnd={up}
        style={{
          display: 'flex', alignItems: 'center', gap: 14, width: '100%',
          padding: '14px 20px', background: 'none', border: 'none',
          borderBottom: `0.5px solid ${t.divider}`,
          cursor: 'pointer', textAlign: 'left',
          opacity: muted ? 0.55 : 1,
        }}>
        <div style={{
          width: 48, flexShrink: 0, textAlign: 'center',
          paddingRight: 14, borderRight: `0.5px solid ${t.divider}`,
        }}>
          <div className="rc-mono" style={{ fontSize: 20, fontWeight: 500, color: day.today ? t.text : t.textDim, letterSpacing: -0.5 }}>
            {day.date}
          </div>
          <div style={{ fontSize: 9, fontWeight: 700, color: day.today ? z.color : t.textFaint, letterSpacing: 0.6, marginTop: 1 }}>
            {days[day.day]}
          </div>
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 3 }}>
            <div style={{ width: 4, height: 14, borderRadius: 2, background: z.color, flexShrink: 0 }} />
            <div style={{ fontSize: 15, fontWeight: 600, color: t.text, letterSpacing: -0.2 }}>{day.name}</div>
            {day.today && <span style={{
              fontSize: 9, fontWeight: 700, letterSpacing: 0.6, padding: '2px 5px',
              borderRadius: 3, background: t.text, color: t.bg,
            }}>오늘</span>}
            {day.done && <RCIcon name="check" size={12} color={RC_ZONES.base.color} stroke={2.5} />}
          </div>
          <div style={{ fontSize: 12, color: t.textDim, lineHeight: 1.4, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
            {day.rationale}
          </div>
        </div>
        <div style={{ textAlign: 'right', flexShrink: 0 }}>
          {day.zone !== 'rest' ? (
            <>
              <div className="rc-mono" style={{ fontSize: 14, fontWeight: 600, color: t.text }}>{day.mins}</div>
              <div style={{ fontSize: 9, color: t.textFaint, fontWeight: 600, letterSpacing: 0.4 }}>MIN</div>
            </>
          ) : (
            <div style={{ fontSize: 11, color: t.textFaint, fontWeight: 500 }}>—</div>
          )}
        </div>
      </button>
      {menuOpen && (
        <div onClick={() => setMenuOpen(false)} style={{
          position: 'absolute', top: '100%', right: 16, zIndex: 20,
          background: t.cardHi, border: `0.5px solid ${t.borderStrong}`,
          borderRadius: 12, padding: 4, minWidth: 160,
          boxShadow: '0 8px 24px rgba(0,0,0,0.3)',
          animation: 'rc-slide-up 0.15s',
        }}>
          {['건너뛰기', '일정 변경', '강도 조정'].map(a => (
            <div key={a} style={{ padding: '10px 12px', fontSize: 13, color: t.text, borderRadius: 8, cursor: 'pointer' }}>
              {a}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function RCWeeklyScreen({ theme = 'dark', onNav, variant = 'strip' }) {
  const t = rcTokens(theme);
  const totalKm = RC_WEEK.filter(d => d.zone !== 'rest').reduce((s, d) => s + (d.mins / 5), 0);
  const totalMin = RC_WEEK.reduce((s, d) => s + d.mins, 0);
  const doneCount = RC_WEEK.filter(d => d.done).length;

  return (
    <RCScreen theme={theme} eyebrow="이번 주 · 4월 15–21일" title="주간 플랜" navTitle="주간 플랜">
      <div style={{ padding: '0 20px 16px', display: 'flex', gap: 18 }}>
          <div>
            <div className="rc-mono" style={{ fontSize: 18, fontWeight: 600, color: t.text }}>{doneCount}<span style={{ fontSize: 12, color: t.textFaint }}>/{RC_WEEK.length}</span></div>
            <div style={{ fontSize: 10, color: t.textFaint, fontWeight: 600, letterSpacing: 0.4, textTransform: 'uppercase' }}>완료</div>
          </div>
          <div style={{ width: 0.5, background: t.divider }} />
          <div>
            <div className="rc-mono" style={{ fontSize: 18, fontWeight: 600, color: t.text }}>{Math.round(totalMin)}<span style={{ fontSize: 12, color: t.textFaint }}>분</span></div>
            <div style={{ fontSize: 10, color: t.textFaint, fontWeight: 600, letterSpacing: 0.4, textTransform: 'uppercase' }}>계획 총량</div>
          </div>
          <div style={{ width: 0.5, background: t.divider }} />
          <div>
            <div className="rc-mono" style={{ fontSize: 18, fontWeight: 600, color: RC_ZONES.base.color }}>1.08</div>
            <div style={{ fontSize: 10, color: t.textFaint, fontWeight: 600, letterSpacing: 0.4, textTransform: 'uppercase' }}>ACWR</div>
          </div>
      </div>

      {/* Week strip (common to strip + calendar) */}
      {variant !== 'timeline' && (
        <div style={{ padding: '0 14px 14px', display: 'flex', gap: 4 }}>
          {RC_WEEK.map(d => (
            <WeekStripBadge key={d.day} day={d} theme={theme} onClick={() => onNav && onNav('workout', d)} compact={variant === 'calendar'} />
          ))}
        </div>
      )}

      <div>
        {variant === 'strip' && (
          <div>
            {RC_WEEK.map(d => (
              <WeeklyListRow key={d.day} day={d} theme={theme} onClick={() => onNav && onNav('workout', d)} />
            ))}
          </div>
        )}

        {variant === 'calendar' && (
          <div style={{ padding: '6px 20px 20px' }}>
            {RC_WEEK.map((d, i) => {
              const z = RC_ZONES[d.zone];
              const muted = d.zone === 'rest';
              return (
                <div key={d.day} onClick={() => onNav && onNav('workout', d)}
                  style={{
                    display: 'flex', gap: 12, marginBottom: 10, padding: 14,
                    borderRadius: 14, background: d.today ? z.softStrong : t.card,
                    border: `0.5px solid ${d.today ? z.color + '60' : t.border}`,
                    cursor: 'pointer', opacity: muted && !d.today ? 0.6 : 1,
                  }}>
                  <div style={{ width: 36, textAlign: 'center', flexShrink: 0 }}>
                    <div className="rc-mono" style={{ fontSize: 22, fontWeight: 500, color: t.text, lineHeight: 1 }}>{d.date}</div>
                    <div style={{ fontSize: 9, fontWeight: 700, color: z.color, letterSpacing: 0.6, marginTop: 2 }}>
                      {['MON','TUE','WED','THU','FRI','SAT','SUN'][i]}
                    </div>
                  </div>
                  <div style={{ width: 0.5, background: t.divider }} />
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 4 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <RCZoneBadge zone={d.zone} size="sm" />
                        {d.done && <RCIcon name="check" size={12} color={RC_ZONES.base.color} stroke={2.5} />}
                      </div>
                      {d.mins > 0 && (
                        <span className="rc-mono" style={{ fontSize: 12, color: t.textDim, fontWeight: 600 }}>{d.mins}분</span>
                      )}
                    </div>
                    <div style={{ fontSize: 15, fontWeight: 600, color: t.text, marginBottom: 2 }}>{d.name}</div>
                    <div style={{ fontSize: 12, color: t.textDim, lineHeight: 1.4 }}>{d.rationale}</div>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {variant === 'timeline' && (
          <div style={{ padding: '14px 20px 20px' }}>
            {RC_WEEK.map((d, i) => {
              const z = RC_ZONES[d.zone];
              const muted = d.zone === 'rest';
              const days = ['월','화','수','목','금','토','일'];
              return (
                <div key={d.day} style={{ display: 'flex', gap: 14, position: 'relative', minHeight: 64 }}>
                  {/* spine */}
                  <div style={{ width: 40, flexShrink: 0, position: 'relative', paddingTop: 10 }}>
                    <div style={{ fontSize: 10, fontWeight: 700, color: d.today ? z.color : t.textFaint, letterSpacing: 0.6 }}>{days[i]}</div>
                    <div className="rc-mono" style={{ fontSize: 18, fontWeight: 500, color: t.text, marginTop: 2 }}>{d.date}</div>
                    {i < RC_WEEK.length - 1 && (
                      <div style={{ position: 'absolute', left: 34, top: 42, bottom: -10, width: 0.5, background: t.divider }} />
                    )}
                    <div style={{
                      position: 'absolute', left: 28, top: 16, width: 12, height: 12, borderRadius: 6,
                      background: d.today ? z.color : t.bg, border: `1.5px solid ${z.color}`,
                    }} />
                  </div>
                  <div onClick={() => onNav && onNav('workout', d)} style={{
                    flex: 1, background: d.today ? z.softStrong : t.card,
                    border: `0.5px solid ${d.today ? z.color + '50' : t.border}`,
                    borderRadius: 12, padding: 12, marginBottom: 8, cursor: 'pointer',
                    opacity: muted && !d.today ? 0.55 : 1,
                  }}>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 4 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <div style={{ width: 3, height: 14, background: z.color, borderRadius: 2 }} />
                        <span style={{ fontSize: 14, fontWeight: 600, color: t.text }}>{d.name}</span>
                      </div>
                      <span className="rc-mono" style={{ fontSize: 11, color: t.textDim }}>{d.mins > 0 ? `${d.mins}분` : '—'}</span>
                    </div>
                    <div style={{ fontSize: 11.5, color: t.textDim, lineHeight: 1.4, paddingLeft: 11 }}>{d.rationale}</div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </RCScreen>
  );
}

Object.assign(window, { RCWeeklyScreen });
