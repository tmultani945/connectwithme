// SFFrame.jsx — Sacred Flow minimal Android phone frame
// Clean dark bezel, no Material chrome. Wraps each screen in a phone shell.

const SF_FRAME_W = 380;
const SF_FRAME_H = 820;

function SFStatusBar({ tone = 'dark' }) {
  const ink = tone === 'light' ? '#FAF7F1' : '#2A2419';
  return (
    <div style={{
      height: 36, display: 'flex', alignItems: 'center',
      justifyContent: 'space-between', padding: '0 20px',
      position: 'relative', flexShrink: 0,
      fontFamily: 'Inter, system-ui, sans-serif',
      color: ink, fontSize: 13, fontWeight: 600, letterSpacing: 0.2,
    }}>
      <span>9:24</span>
      {/* camera dot */}
      <div style={{
        position: 'absolute', left: '50%', top: 10, transform: 'translateX(-50%)',
        width: 9, height: 9, borderRadius: 100, background: '#111',
        boxShadow: '0 0 0 2px rgba(0,0,0,0.35)',
      }} />
      <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
        {/* signal */}
        <svg width="15" height="11" viewBox="0 0 15 11" fill={ink}>
          <rect x="0"  y="7" width="2.4" height="4" rx="0.5"/>
          <rect x="3.5" y="5" width="2.4" height="6" rx="0.5"/>
          <rect x="7"  y="3" width="2.4" height="8" rx="0.5"/>
          <rect x="10.5" y="0" width="2.4" height="11" rx="0.5"/>
        </svg>
        {/* wifi */}
        <svg width="14" height="11" viewBox="0 0 14 11" fill={ink}>
          <path d="M7 11 L9.5 8 C8.1 6.8 5.9 6.8 4.5 8 Z"/>
          <path d="M7 5.2 C9.2 5.2 11.2 6.2 12.6 7.7 L11.3 9 C10.3 8 8.7 7.4 7 7.4 C5.3 7.4 3.7 8 2.7 9 L1.4 7.7 C2.8 6.2 4.8 5.2 7 5.2 Z" opacity="0.85"/>
          <path d="M7 1.5 C10.4 1.5 13.4 2.9 14 4 L12.6 5.4 C11.3 4.3 9.3 3.5 7 3.5 C4.7 3.5 2.7 4.3 1.4 5.4 L0 4 C0.6 2.9 3.6 1.5 7 1.5 Z" opacity="0.6"/>
        </svg>
        {/* battery */}
        <svg width="22" height="11" viewBox="0 0 22 11">
          <rect x="0.5" y="0.5" width="19" height="10" rx="2.5" fill="none" stroke={ink} strokeOpacity="0.65"/>
          <rect x="2"   y="2"   width="14" height="7"  rx="1.4" fill={ink}/>
          <rect x="20"  y="3.5" width="1.5" height="4" rx="0.5" fill={ink} fillOpacity="0.65"/>
        </svg>
      </div>
    </div>
  );
}

function SFGestureBar({ tone = 'dark' }) {
  const ink = tone === 'light' ? '#FAF7F1' : '#2A2419';
  return (
    <div style={{
      height: 22, display: 'flex', alignItems: 'center', justifyContent: 'center',
      flexShrink: 0,
    }}>
      <div style={{
        width: 120, height: 4, borderRadius: 2,
        background: ink, opacity: 0.5,
      }} />
    </div>
  );
}

function SFFrame({
  children,
  width = SF_FRAME_W,
  height = SF_FRAME_H,
  dark = false,
  bg,                // override background
  statusTone,        // 'dark' (icons dark) or 'light' (icons light)
}) {
  const themeClass = dark ? 'sf-dark' : '';
  const bezel = dark ? '#0d0b08' : '#1c1815';
  const surfaceBg = bg || (dark ? 'var(--sf-bg)' : 'var(--sf-bg)');
  const tone = statusTone || (dark ? 'light' : 'dark');
  return (
    <div className={themeClass} style={{
      width, height, borderRadius: 44,
      padding: 6, background: bezel,
      boxShadow: '0 1px 0 rgba(255,255,255,0.04) inset, 0 24px 48px rgba(30,20,5,0.18), 0 6px 16px rgba(30,20,5,0.12)',
      boxSizing: 'border-box',
      position: 'relative',
    }}>
      {/* inner ring highlight */}
      <div style={{
        position: 'absolute', inset: 5, borderRadius: 39,
        boxShadow: 'inset 0 0 0 1px rgba(255,255,255,0.04)',
        pointerEvents: 'none',
      }} />
      <div style={{
        width: '100%', height: '100%', borderRadius: 38,
        background: surfaceBg, color: 'var(--sf-ink)',
        overflow: 'hidden', position: 'relative',
        display: 'flex', flexDirection: 'column',
      }}>
        <SFStatusBar tone={tone} />
        <div style={{ flex: 1, minHeight: 0, position: 'relative', display: 'flex', flexDirection: 'column' }}>
          {children}
        </div>
        <SFGestureBar tone={tone} />
      </div>
    </div>
  );
}

Object.assign(window, { SFFrame, SFStatusBar, SFGestureBar });
