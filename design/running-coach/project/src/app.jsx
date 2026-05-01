// app.jsx — main Running Coach prototype app shell with navigation + tweaks

const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "theme": "light",
  "sessionKey": "threshold",
  "completed": false,
  "heroVariant": "bold",
  "weeklyVariant": "strip",
  "startTab": "home"
}/*EDITMODE-END*/;

function useEditModeBridge(defaults) {
  const [values, setValues] = React.useState(defaults);
  const [editMode, setEditMode] = React.useState(false);

  React.useEffect(() => {
    const onMsg = (e) => {
      const d = e.data;
      if (!d || typeof d !== 'object') return;
      if (d.type === '__activate_edit_mode')   setEditMode(true);
      if (d.type === '__deactivate_edit_mode') setEditMode(false);
    };
    window.addEventListener('message', onMsg);
    window.parent.postMessage({ type: '__edit_mode_available' }, '*');
    return () => window.removeEventListener('message', onMsg);
  }, []);

  const update = (patch) => {
    setValues(v => ({ ...v, ...patch }));
    window.parent.postMessage({ type: '__edit_mode_set_keys', edits: patch }, '*');
  };
  return { values, editMode, update };
}

function TweaksPanel({ values, update, theme }) {
  const t = rcTokens(theme);
  const [open, setOpen] = React.useState(false);
  return (
    <div style={{
      position: 'fixed', bottom: 20, right: 20, zIndex: 1000,
      width: open ? 260 : 44,
      background: '#0B1220', color: '#F5F3EE',
      borderRadius: 14, border: '0.5px solid rgba(255,255,255,0.12)',
      boxShadow: '0 20px 60px rgba(0,0,0,0.5)',
      fontFamily: RC_FONT_UI, overflow: 'hidden',
      transition: 'width 0.2s',
    }}>
      <div onClick={() => setOpen(o => !o)} style={{
        padding: open ? '10px 14px' : '10px', display: 'flex',
        alignItems: 'center', justifyContent: 'space-between',
        cursor: 'pointer', borderBottom: open ? '0.5px solid rgba(255,255,255,0.08)' : 'none',
      }}>
        {open && <span style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'rgba(245,243,238,0.5)', textTransform: 'uppercase' }}>Tweaks</span>}
        <svg width="14" height="14" viewBox="0 0 14 14"><path d="M2 4h10M2 7h10M2 10h6" stroke="#F5F3EE" strokeWidth="1.5" strokeLinecap="round"/></svg>
      </div>
      {open && (
        <div style={{ padding: 14, display: 'flex', flexDirection: 'column', gap: 14 }}>
          <TweakRow label="Theme">
            <SegBtn options={[['dark','Dark'],['light','Light']]} value={values.theme} onChange={v => update({ theme: v })} />
          </TweakRow>
          <TweakRow label="Today · Session">
            <SegBtn options={[
              ['recovery','Recv'],['base','Base'],['threshold','Thr'],['interval','Int'],['rest','Rest'],
            ]} value={values.sessionKey} onChange={v => update({ sessionKey: v })} small/>
          </TweakRow>
          <TweakRow label="Completion">
            <SegBtn options={[['before','Before'],['after','After']]}
              value={values.completed ? 'after' : 'before'}
              onChange={v => update({ completed: v === 'after' })} />
          </TweakRow>
          <TweakRow label="Home Hero">
            <SegBtn options={[['bold','Bold'],['split','Split'],['mono','Mono'],['zoned','Zoned']]}
              value={values.heroVariant} onChange={v => update({ heroVariant: v })} small/>
          </TweakRow>
          <TweakRow label="Weekly Layout">
            <SegBtn options={[['strip','Strip'],['calendar','Cal'],['timeline','Line']]}
              value={values.weeklyVariant} onChange={v => update({ weeklyVariant: v })} small/>
          </TweakRow>
        </div>
      )}
    </div>
  );
}

function TweakRow({ label, children }) {
  return (
    <div>
      <div style={{ fontSize: 10, fontWeight: 700, color: 'rgba(245,243,238,0.55)', letterSpacing: 0.6, textTransform: 'uppercase', marginBottom: 6 }}>{label}</div>
      {children}
    </div>
  );
}

function SegBtn({ options, value, onChange, small }) {
  return (
    <div style={{ display: 'flex', gap: 2, padding: 2, borderRadius: 8, background: 'rgba(255,255,255,0.06)' }}>
      {options.map(([v, label]) => (
        <button key={v} onClick={() => onChange(v)} style={{
          flex: 1, padding: small ? '5px 2px' : '6px 4px', borderRadius: 6,
          background: v === value ? '#F5F3EE' : 'transparent',
          color: v === value ? '#0B1220' : 'rgba(245,243,238,0.7)',
          border: 'none', cursor: 'pointer',
          fontSize: small ? 10 : 11, fontWeight: 600, letterSpacing: 0.2,
        }}>{label}</button>
      ))}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// App shell with tab + modal navigation
// ─────────────────────────────────────────────────────────────
function RCApp({ initialTab = 'home', theme = 'dark', sessionKey = 'threshold', completed = false,
                heroVariant = 'bold', weeklyVariant = 'strip',
                onboardingMode = false, onboardingStep = 0, onboardingSetStep }) {
  const [tab, setTab] = React.useState(initialTab);
  const [modal, setModal] = React.useState(null); // 'workout' | 'feedback' | null
  const [modalCompleted, setModalCompleted] = React.useState(completed);

  React.useEffect(() => { setTab(initialTab); }, [initialTab]);
  React.useEffect(() => { setModalCompleted(completed); }, [completed]);

  if (onboardingMode) {
    return (
      <RCOnboardingScreen theme={theme} step={onboardingStep}
        onStepChange={onboardingSetStep}
        onComplete={() => onboardingSetStep && onboardingSetStep(0)}/>
    );
  }

  const screens = {
    home: <RCHomeScreen theme={theme} sessionKey={sessionKey} heroVariant={heroVariant}
            onNav={(to) => {
              if (to === 'workout') { setModalCompleted(false); setModal('workout'); }
              else if (to === 'workout-done') { setModalCompleted(true); setModal('workout'); }
              else if (to === 'feedback') setModal('feedback');
            }}/>,
    weekly:   <RCWeeklyScreen theme={theme} variant={weeklyVariant} onNav={(to) => { if (to === 'workout') { setModalCompleted(false); setModal('workout'); } }}/>,
    trends:   <RCTrendsScreen theme={theme}/>,
    goals:    <RCGoalsScreen theme={theme}/>,
    settings: <RCSettingsScreen theme={theme}/>,
  };

  return (
    <div style={{ height: '100%', position: 'relative' }}>
      {/* screen fills full area; floating tab bar overlays */}
      <div style={{ position: 'absolute', inset: 0 }}>
        {screens[tab]}
      </div>
      <RCTabBar theme={theme} active={tab} onChange={setTab} />

      {/* modal sheets */}
      {modal && (
        <div style={{
          position: 'absolute', inset: 0, zIndex: 100,
          animation: 'rc-slide-up 0.22s cubic-bezier(0.2,0.8,0.2,1)',
        }}>
          <div style={{ position: 'absolute', inset: 0, background: rcTokens(theme).bg }}>
            {modal === 'workout' && (
              <RCWorkoutScreen theme={theme} sessionKey={sessionKey} completed={modalCompleted} onBack={() => setModal(null)} />
            )}
            {modal === 'feedback' && (
              <RCFeedbackScreen theme={theme} onClose={() => setModal(null)} />
            )}
          </div>
        </div>
      )}
    </div>
  );
}

Object.assign(window, { RCApp, TweaksPanel, TWEAK_DEFAULTS, useEditModeBridge });
