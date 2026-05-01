// data.jsx — realistic mock data for an intermediate Korean runner
// Target: 10K PB 43–44 min, training for Seoul Half in ~8 weeks.

const RC_USER = {
  name: '민준',
  goalRace: { name: '서울 하프마라톤', date: '2026-06-14', distanceKm: 21.0975, targetPace: '4:42/km', weeksLeft: 8 },
  phase: 'build', // base → build → peak → taper
  phaseProgress: 0.45,
  vdot: 49.2,
};

// Pace zones (min/km) — per Jack Daniels VDOT 49
const RC_PACE_ZONES = {
  recovery:  { label: 'Recovery',  ko: '회복',   range: '5:50–6:30', target: '6:10' },
  easy:      { label: 'Easy',      ko: '편안',   range: '5:10–5:40', target: '5:25' },
  base:      { label: 'Base',      ko: '베이스', range: '4:55–5:20', target: '5:05' },
  marathon:  { label: 'Marathon',  ko: '마라톤', range: '4:40–4:55', target: '4:48' },
  threshold: { label: 'Threshold', ko: '역치',   range: '4:25–4:40', target: '4:32' },
  interval:  { label: 'Interval',  ko: '인터벌', range: '4:05–4:20', target: '4:12' },
  repetition:{ label: 'Rep',       ko: '반복',   range: '3:50–4:00', target: '3:55' },
};

// Today's session (cycled via Tweaks)
const RC_SESSIONS = {
  threshold: {
    zone: 'threshold', title: 'Threshold',
    titleKo: '템포 인터벌',
    durationMin: 40, distanceKm: 8.5,
    primaryTarget: '4:32/km',
    headline: '4:30/km × 3분 × 4세트',
    rationale: '10K PB 준비용 젖산 역치 자극이에요. 어제 회복이 좋아서 예정대로 진행할게요. 4세트 중 마지막 2세트는 폼 유지에 집중하세요.',
    steps: [
      { kind: 'warmup',    label: '워밍업',    durationMin: 10, pace: '5:40/km',  hr: 'Z2', note: '점진적 빌드업' },
      { kind: 'interval',  label: '메인 세트', durationMin: 20, pace: '4:30/km',  hr: 'Z4', note: '3분 × 4, 휴식 90초', reps: 4 },
      { kind: 'cooldown',  label: '쿨다운',    durationMin: 10, pace: '5:50/km',  hr: 'Z2', note: '호흡 조절' },
    ],
  },
  interval: {
    zone: 'interval', title: 'Interval',
    titleKo: 'VO2max 인터벌',
    durationMin: 45, distanceKm: 9.2,
    primaryTarget: '4:12/km',
    headline: '4:10/km × 400m × 8회',
    rationale: '속도 내구력을 끌어올리는 VO2max 자극이에요. 회복 호흡이 돌아올 때마다 다음 반복을 시작하세요.',
    steps: [
      { kind: 'warmup',    label: '워밍업',    durationMin: 15, pace: '5:30/km',  hr: 'Z2' },
      { kind: 'interval',  label: '메인 세트', durationMin: 20, pace: '4:10/km',  hr: 'Z5', note: '400m × 8, 휴식 60초', reps: 8 },
      { kind: 'cooldown',  label: '쿨다운',    durationMin: 10, pace: '5:50/km',  hr: 'Z2' },
    ],
  },
  base: {
    zone: 'base', title: 'Base',
    titleKo: '유산소 베이스',
    durationMin: 50, distanceKm: 10.2,
    primaryTarget: '5:05/km',
    headline: '5:05/km × 50분',
    rationale: '유산소 기반을 다지는 편한 러닝이에요. 대화 가능한 페이스를 유지하세요. HR Z2 상단 범위.',
    steps: [
      { kind: 'warmup',   label: '워밍업', durationMin: 5,  pace: '5:40/km', hr: 'Z1' },
      { kind: 'base',     label: '본 런',  durationMin: 40, pace: '5:05/km', hr: 'Z2' },
      { kind: 'cooldown', label: '쿨다운', durationMin: 5,  pace: '5:50/km', hr: 'Z1' },
    ],
  },
  recovery: {
    zone: 'recovery', title: 'Recovery',
    titleKo: '회복 러닝',
    durationMin: 30, distanceKm: 4.8,
    primaryTarget: '6:10/km',
    headline: '6:10/km × 30분',
    rationale: '어제 세션이 강도 높았어요. 오늘은 혈류 회복 목적의 아주 편한 러닝으로 진행합니다. HR Z1 유지.',
    steps: [
      { kind: 'recovery', label: '회복 런', durationMin: 30, pace: '6:10/km', hr: 'Z1' },
    ],
  },
  rest: {
    zone: 'rest', title: 'Rest',
    titleKo: '휴식일',
    durationMin: 0, distanceKm: 0,
    primaryTarget: '—',
    headline: '완전 휴식 또는 가벼운 스트레칭',
    rationale: '이번 주 누적 부하가 높아서 회복 우선이에요. 폼롤링 10–15분 권장. 내일 템포런 준비하세요.',
    steps: [],
  },
};

// Score chips
const RC_SCORES = {
  recovery: { key: 'recovery', label: '회복도', value: 78, max: 100, zone: 'recovery',
    tier: '좋음',
    explain: 'HRV 어제보다 +4ms, 수면 7시간 32분으로 정상 범위. 계획된 템포 진행 가능.' },
  fatigue:  { key: 'fatigue',  label: '피로도', value: 52, max: 100, zone: 'threshold',
    tier: '보통',
    explain: '지난 3일 누적 부하 TSS 218. 목요일 템포런 영향. 오늘 세션 후 회복 필요.' },
  injury:   { key: 'injury',   label: '부상 리스크', value: 30, max: 100, zone: 'base',
    tier: '낮음',
    explain: 'ACWR 1.08로 안전 구간. 발목·무릎 통증 보고 없음. 주간 +12% 증가, 상한 이내.' },
};

// Yesterday's completed session
const RC_YESTERDAY = {
  zone: 'recovery',
  title: 'Recovery Run',
  distanceKm: 4.2,
  durationMin: 27,
  avgPace: '6:29/km',
  plannedPace: '6:10/km',
  matchScore: 88,
  hr: 134,
  note: '계획보다 약간 느렸지만 회복 목적 달성.',
};

// Weekly plan
const RC_WEEK = [
  { day: 'mon', date: 15, zone: 'base',      name: '베이스 러닝',    mins: 50, rationale: '유산소 기반 유지 · 5:05/km × 50분', done: true,
    completed: { distanceKm: 10.1, pace: '5:07/km', match: 94 } },
  { day: 'tue', date: 16, zone: 'rest',      name: '휴식',          mins: 0,  rationale: '완전 휴식 · 가벼운 스트레칭 권장', done: true },
  { day: 'wed', date: 17, zone: 'threshold', name: '템포 인터벌',    mins: 40, rationale: '젖산 역치 자극 · 4:30/km × 3분 × 4세트', today: true },
  { day: 'thu', date: 18, zone: 'recovery',  name: '회복 러닝',      mins: 30, rationale: '혈류 회복 · 6:10/km × 30분' },
  { day: 'fri', date: 19, zone: 'rest',      name: '휴식',          mins: 0,  rationale: '주말 롱런 대비 완전 휴식' },
  { day: 'sat', date: 20, zone: 'interval',  name: 'VO2max 인터벌',  mins: 45, rationale: '속도 내구력 · 400m × 8회' },
  { day: 'sun', date: 21, zone: 'long',      name: '롱 런',          mins: 90, rationale: '18km 내외 · 마라톤 페이스 마지막 3km' },
];

// 12 weeks of weekly km + ACWR
const RC_TREND_WEEKS = [
  { w: 'W-11', km: 28, acwr: 0.92 }, { w: 'W-10', km: 32, acwr: 0.98 },
  { w: 'W-09', km: 35, acwr: 1.02 }, { w: 'W-08', km: 38, acwr: 1.05 },
  { w: 'W-07', km: 34, acwr: 0.96 }, { w: 'W-06', km: 42, acwr: 1.12 },
  { w: 'W-05', km: 45, acwr: 1.18 }, { w: 'W-04', km: 40, acwr: 1.04 },
  { w: 'W-03', km: 48, acwr: 1.22 }, { w: 'W-02', km: 52, acwr: 1.28 },
  { w: 'W-01', km: 46, acwr: 1.10 }, { w: '이번주', km: 43, acwr: 1.08 },
];

const RC_PRS = [
  { dist: '1K',       time: '3:32',    delta: '-4s',  deltaDir: 'up', when: '3주 전' },
  { dist: '5K',       time: '20:48',   delta: '-18s', deltaDir: 'up', when: '6주 전' },
  { dist: '10K',      time: '43:12',   delta: '-42s', deltaDir: 'up', when: '2주 전' },
  { dist: 'Half',     time: '1:38:20', delta: '+12s', deltaDir: 'down', when: '작년 가을' },
  { dist: 'Marathon', time: '—',       delta: '',     deltaDir: 'none', when: '기록 없음' },
];

Object.assign(window, {
  RC_USER, RC_PACE_ZONES, RC_SESSIONS, RC_SCORES, RC_YESTERDAY,
  RC_WEEK, RC_TREND_WEEKS, RC_PRS,
});
