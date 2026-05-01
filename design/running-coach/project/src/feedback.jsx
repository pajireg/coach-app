// feedback.jsx — Feedback Input screen

function RCSlider({ label, value, onChange, zone = 'base', theme }) {
  const t = rcTokens(theme);
  const z = RC_ZONES[zone];
  const pct = (value - 1) / 9;
  return (
    <div style={{ padding: '16px 0', borderBottom: `0.5px solid ${t.divider}` }}>
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 12 }}>
        <span style={{ fontSize: 14, fontWeight: 600, color: t.text }}>{label}</span>
        <span className="rc-mono" style={{ fontSize: 22, fontWeight: 500, color: z.color, letterSpacing: -0.5 }}>
          {value}<span style={{ fontSize: 11, color: t.textFaint, marginLeft: 2 }}>/10</span>
        </span>
      </div>
      <div style={{ position: 'relative', height: 32, display: 'flex', alignItems: 'center' }}>
        <div style={{ position: 'absolute', left: 0, right: 0, height: 4, borderRadius: 2, background: t.divider }} />
        <div style={{ position: 'absolute', left: 0, height: 4, width: `${pct * 100}%`, borderRadius: 2, background: z.color }} />
        {[...Array(10)].map((_, i) => (
          <button key={i} onClick={() => onChange(i + 1)} style={{
            position: 'absolute', left: `${(i / 9) * 100}%`, transform: 'translateX(-50%)',
            width: 18, height: 18, borderRadius: 9,
            background: i + 1 === value ? z.color : t.card,
            border: `1.5px solid ${i + 1 === value ? z.color : t.borderStrong}`,
            cursor: 'pointer', padding: 0,
          }} />
        ))}
      </div>
    </div>
  );
}

function BodyZone({ x, y, w, h, active, onClick }) {
  return (
    <ellipse cx={x} cy={y} rx={w} ry={h}
      fill={active ? RC_ZONES.interval.color : 'rgba(255,255,255,0.05)'}
      stroke={active ? RC_ZONES.interval.color : 'rgba(255,255,255,0.2)'}
      strokeWidth="1" style={{ cursor: 'pointer' }}
      onClick={onClick}/>
  );
}

function BodySilhouette({ zones, toggleZone, theme }) {
  const t = rcTokens(theme);
  const stroke = t.borderStrong;
  const fill = t.card;
  return (
    <svg viewBox="0 0 180 220" width="100%" height="220" style={{ display: 'block' }}>
      {/* body outline */}
      <g fill={fill} stroke={stroke} strokeWidth="1">
        <circle cx="90" cy="22" r="14"/>
        <path d="M70 38 L110 38 L115 80 L120 130 L112 180 L105 215 L95 215 L92 170 L88 170 L85 215 L75 215 L68 180 L60 130 L65 80 Z"/>
        <path d="M70 42 L50 55 L40 95 L45 130 L52 125 L50 90 L60 70 Z"/>
        <path d="M110 42 L130 55 L140 95 L135 130 L128 125 L130 90 L120 70 Z"/>
      </g>
      {/* zones */}
      <BodyZone x={90} y={60} w={15} h={8} active={zones.chest} onClick={() => toggleZone('chest')} />
      <BodyZone x={74} y={145} w={8} h={18} active={zones.leftThigh} onClick={() => toggleZone('leftThigh')} />
      <BodyZone x={106} y={145} w={8} h={18} active={zones.rightThigh} onClick={() => toggleZone('rightThigh')} />
      <BodyZone x={74} y={190} w={7} h={14} active={zones.leftCalf} onClick={() => toggleZone('leftCalf')} />
      <BodyZone x={106} y={190} w={7} h={14} active={zones.rightCalf} onClick={() => toggleZone('rightCalf')} />
      <BodyZone x={72} y={170} w={5} h={4} active={zones.leftKnee} onClick={() => toggleZone('leftKnee')} />
      <BodyZone x={108} y={170} w={5} h={4} active={zones.rightKnee} onClick={() => toggleZone('rightKnee')} />
    </svg>
  );
}

function RCFeedbackScreen({ theme = 'dark', onClose }) {
  const t = rcTokens(theme);
  const [fatigue, setFatigue] = React.useState(5);
  const [soreness, setSoreness] = React.useState(3);
  const [motivation, setMotivation] = React.useState(7);
  const [sleep, setSleep] = React.useState(7);
  const [zones, setZones] = React.useState({});
  const [notes, setNotes] = React.useState('');
  const toggleZone = (k) => setZones(s => ({ ...s, [k]: !s[k] }));

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div style={{ padding: '6px 20px 12px', display: 'flex', alignItems: 'center', gap: 12 }}>
        <button onClick={onClose} style={{ width: 36, height: 36, borderRadius: 12, background: t.card, border: `0.5px solid ${t.border}`,
          display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', color: t.textDim }}>
          <RCIcon name="close" size={16} />
        </button>
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 22, fontWeight: 700, color: t.text, letterSpacing: -0.4 }}>피드백 입력</div>
          <div style={{ fontSize: 12, color: t.textFaint }}>오늘 컨디션 · 수요일 저녁</div>
        </div>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '0 20px 120px' }}>
        <div style={{ marginTop: 4 }}>
          <RCSlider label="피로도"   value={fatigue}    onChange={setFatigue}    zone="threshold" theme={theme}/>
          <RCSlider label="근육통"   value={soreness}   onChange={setSoreness}   zone="interval"  theme={theme}/>
          <RCSlider label="의욕"     value={motivation} onChange={setMotivation} zone="base"      theme={theme}/>
          <RCSlider label="수면 질"  value={sleep}      onChange={setSleep}      zone="recovery"  theme={theme}/>
        </div>

        {/* Body silhouette */}
        <div style={{ marginTop: 24 }}>
          <div style={{ fontSize: 14, fontWeight: 600, color: t.text, marginBottom: 6 }}>통증 부위</div>
          <div style={{ fontSize: 12, color: t.textDim, marginBottom: 12 }}>
            통증이 있는 부위를 탭하세요 · 해당 없음 그대로 두기
          </div>
          <div style={{ background: t.card, borderRadius: 18, border: `0.5px solid ${t.border}`, padding: 16 }}>
            <BodySilhouette zones={zones} toggleZone={toggleZone} theme={theme} />
          </div>
        </div>

        {/* Notes */}
        <div style={{ marginTop: 20 }}>
          <div style={{ fontSize: 14, fontWeight: 600, color: t.text, marginBottom: 8 }}>메모 <span style={{ fontSize: 11, color: t.textFaint, fontWeight: 500, marginLeft: 4 }}>선택</span></div>
          <textarea value={notes} onChange={e => setNotes(e.target.value)} placeholder="오늘 특별히 전달할 내용이 있다면…"
            style={{ width: '100%', minHeight: 96, padding: 14, borderRadius: 14,
              background: t.card, border: `0.5px solid ${t.border}`, color: t.text,
              fontFamily: RC_FONT_UI, fontSize: 13.5, lineHeight: 1.5, resize: 'none', outline: 'none', boxSizing: 'border-box' }}/>
        </div>
      </div>

      <div style={{ position: 'absolute', bottom: 28, left: 20, right: 20, zIndex: 30 }}>
        <RCButton theme={theme} full size="md" onClick={onClose}>피드백 전송</RCButton>
      </div>
    </div>
  );
}

Object.assign(window, { RCFeedbackScreen });
