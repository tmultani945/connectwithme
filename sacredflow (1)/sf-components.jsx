// SF Components — shared building blocks for all Sacred Flow screens.
// Depends on tokens.css and SFIcon (window global).

const sfPad = (top, right = top, bottom = top, left = right) =>
  ({ paddingTop: top, paddingRight: right, paddingBottom: bottom, paddingLeft: left });

// ── Recipient badge (small caps, tracked) ───────────────────────
function RecipientBadge({ children, color, style }) {
  return (
    <div className="sf-recipient-badge" style={{
      color: color || 'var(--sf-primary)',
      display: 'inline-flex', alignItems: 'center', gap: 8,
      ...style,
    }}>
      <span style={{
        display: 'inline-block', width: 14, height: 1,
        background: 'currentColor', opacity: 0.5,
      }} />
      <span>{children}</span>
      <span style={{
        display: 'inline-block', width: 14, height: 1,
        background: 'currentColor', opacity: 0.5,
      }} />
    </div>
  );
}

// ── Asterism mark (single visual punctuation) ──────────────────
function Asterism({ size = 12, color, style }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      gap: size * 0.6, opacity: 0.5, color: color || 'var(--sf-primary)',
      ...style,
    }}>
      <span style={{ width: size * 0.32, height: size * 0.32, borderRadius: '50%', background: 'currentColor' }} />
      <span style={{ width: size * 0.32, height: size * 0.32, borderRadius: '50%', background: 'currentColor' }} />
      <span style={{ width: size * 0.32, height: size * 0.32, borderRadius: '50%', background: 'currentColor' }} />
    </div>
  );
}

// ── Primary button ─────────────────────────────────────────────
function PrimaryButton({ children, disabled, onClick, full = true, style, icon }) {
  return (
    <button onClick={onClick} disabled={disabled} style={{
      width: full ? '100%' : 'auto',
      height: 56, borderRadius: 'var(--sf-r-pill)',
      background: disabled ? 'var(--sf-outline-soft)' : 'var(--sf-primary-ink)',
      color: disabled ? 'var(--sf-ink-3)' : 'var(--sf-bg)',
      fontFamily: 'var(--sf-sans)', fontWeight: 500, fontSize: 15,
      letterSpacing: 0.3, border: 'none', cursor: disabled ? 'not-allowed' : 'pointer',
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 10,
      ...style,
    }}>
      <span>{children}</span>
      {icon && <SFIcon name={icon} size={18} />}
    </button>
  );
}

// ── Ghost / text button ────────────────────────────────────────
function TextButton({ children, onClick, style, color }) {
  return (
    <button onClick={onClick} style={{
      background: 'transparent', border: 'none', cursor: 'pointer',
      padding: '12px 16px', fontFamily: 'var(--sf-sans)', fontWeight: 500,
      fontSize: 14, color: color || 'var(--sf-primary-ink)',
      letterSpacing: 0.2, ...style,
    }}>{children}</button>
  );
}

// ── Filter / select chip ───────────────────────────────────────
function Chip({ children, selected, onClick, disabled, locked, dense, style }) {
  return (
    <button onClick={onClick} disabled={disabled} style={{
      display: 'inline-flex', alignItems: 'center', gap: 6,
      padding: dense ? '7px 14px' : '10px 16px',
      borderRadius: 'var(--sf-r-pill)',
      background: selected ? 'var(--sf-primary-ink)' : 'transparent',
      color: selected ? 'var(--sf-bg)' : 'var(--sf-ink)',
      border: `1px solid ${selected ? 'var(--sf-primary-ink)' : 'var(--sf-outline-soft)'}`,
      fontFamily: 'var(--sf-sans)', fontWeight: 500,
      fontSize: dense ? 13 : 14, letterSpacing: 0.1,
      cursor: disabled ? 'not-allowed' : 'pointer',
      opacity: disabled ? 0.5 : 1,
      whiteSpace: 'nowrap',
      ...style,
    }}>
      {selected && <SFIcon name="check-soft" size={14} />}
      <span>{children}</span>
      {locked && <SFIcon name="lock" size={12} style={{ opacity: 0.7 }} />}
    </button>
  );
}

// ── Section header (overline) ──────────────────────────────────
function SectionHeader({ children, style }) {
  return <div className="sf-overline" style={style}>{children}</div>;
}

// ── Top bar (back + title + trailing) ──────────────────────────
function BackTopBar({ title, onBack, trailing, transparent = true, sub }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center',
      padding: '14px 20px 14px 14px', minHeight: 56,
      background: transparent ? 'transparent' : 'var(--sf-surface)',
      gap: 8, flexShrink: 0,
    }}>
      <button onClick={onBack} style={{
        width: 40, height: 40, borderRadius: '50%',
        background: 'transparent', border: 'none', cursor: 'pointer',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        color: 'var(--sf-ink)',
      }}>
        <SFIcon name="back" size={22} />
      </button>
      <div style={{ flex: 1, minWidth: 0 }}>
        {title && <div style={{
          fontFamily: 'var(--sf-sans)', fontSize: 15, fontWeight: 500,
          color: 'var(--sf-ink)',
        }}>{title}</div>}
        {sub && <div className="sf-overline" style={{ marginTop: 2 }}>{sub}</div>}
      </div>
      {trailing}
    </div>
  );
}

// ── Bottom nav (4 tabs) ────────────────────────────────────────
function BottomNav({ active = 'home' }) {
  const items = [
    { id: 'home',     label: 'Home',      icon: 'home' },
    { id: 'library',  label: 'Library',   icon: 'library' },
    { id: 'reminder', label: 'Reminders', icon: 'bell' },
    { id: 'settings', label: 'Settings',  icon: 'settings' },
  ];
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-around',
      padding: '8px 8px 14px',
      borderTop: '1px solid var(--sf-outline-soft)',
      background: 'var(--sf-bg)',
      flexShrink: 0,
    }}>
      {items.map(it => {
        const on = it.id === active;
        return (
          <div key={it.id} style={{
            display: 'flex', flexDirection: 'column', alignItems: 'center',
            gap: 4, padding: '6px 14px',
            cursor: 'pointer',
            color: on ? 'var(--sf-primary-ink)' : 'var(--sf-ink-3)',
          }}>
            <div style={{
              width: 44, height: 28, borderRadius: 'var(--sf-r-pill)',
              background: on ? 'var(--sf-primary-soft)' : 'transparent',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <SFIcon name={it.icon} size={20} strokeWidth={on ? 1.8 : 1.4} />
            </div>
            <span style={{
              fontFamily: 'var(--sf-sans)', fontSize: 11,
              fontWeight: on ? 600 : 500, letterSpacing: 0.2,
            }}>{it.label}</span>
          </div>
        );
      })}
    </div>
  );
}

// ── Tone card (used in onboarding + create) ────────────────────
function ToneCard({ name, descriptor, sample, selected, onClick, compact }) {
  return (
    <div onClick={onClick} style={{
      width: compact ? 200 : 230,
      flexShrink: 0,
      padding: '22px 22px 24px',
      borderRadius: 'var(--sf-r-md)',
      background: selected ? 'var(--sf-primary-soft)' : 'var(--sf-surface)',
      border: `1px solid ${selected ? 'var(--sf-primary)' : 'var(--sf-outline-soft)'}`,
      cursor: 'pointer',
      boxShadow: selected ? 'var(--sf-shadow-2)' : 'var(--sf-shadow-1)',
      transition: 'all 300ms ease-out',
      display: 'flex', flexDirection: 'column', gap: 14, minHeight: 200,
    }}>
      <div className="sf-overline" style={{ color: selected ? 'var(--sf-primary)' : 'var(--sf-ink-3)' }}>
        {selected ? 'selected' : 'tone'}
      </div>
      <div style={{
        fontFamily: 'var(--sf-serif)', fontSize: 28, fontWeight: 400,
        letterSpacing: '-0.01em', color: 'var(--sf-ink)', lineHeight: 1,
      }}>{name}</div>
      <div className="sf-body" style={{ fontSize: 13, color: 'var(--sf-ink-2)' }}>{descriptor}</div>
      <div style={{ flex: 1 }} />
      <div style={{
        fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
        fontSize: 17, lineHeight: 1.4, color: 'var(--sf-primary-ink)',
        opacity: 0.85,
      }}>"{sample}"</div>
    </div>
  );
}

// ── Prayer card (saved-prayer summary) ─────────────────────────
function PrayerCard({ recipient, preview, date, fav, wide, onClick, accent }) {
  return (
    <div onClick={onClick} style={{
      width: wide ? '100%' : 270, flexShrink: 0,
      padding: '20px 22px 18px',
      borderRadius: 'var(--sf-r-md)',
      background: 'var(--sf-surface)',
      border: '1px solid var(--sf-outline-soft)',
      boxShadow: 'var(--sf-shadow-1)',
      display: 'flex', flexDirection: 'column', gap: 12,
      cursor: 'pointer', position: 'relative',
      minHeight: wide ? 'auto' : 180,
    }}>
      {/* hairline accent on left */}
      {accent && <div style={{
        position: 'absolute', left: 0, top: 14, bottom: 14, width: 2,
        background: accent, borderRadius: 2, opacity: 0.55,
      }} />}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div className="sf-recipient-badge">{recipient}</div>
        <SFIcon name={fav ? 'heart-fill' : 'heart'} size={16}
          color={fav ? 'var(--sf-tertiary)' : 'var(--sf-ink-3)'} />
      </div>
      <div style={{
        fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
        fontSize: 16, lineHeight: 1.5, color: 'var(--sf-ink)',
        overflow: 'hidden',
        display: '-webkit-box',
        WebkitLineClamp: wide ? 2 : 4,
        WebkitBoxOrient: 'vertical',
      }}>{preview}</div>
      <div style={{ flex: 1 }} />
      <div className="sf-overline" style={{ fontSize: 10, letterSpacing: '0.18em' }}>{date}</div>
    </div>
  );
}

// ── Multi-line text field ──────────────────────────────────────
function TextField({ placeholder, value, maxLength, multiline, helper, charCounter, rows = 4 }) {
  const len = (value || '').length;
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
      <div style={{
        background: 'var(--sf-surface)',
        border: '1px solid var(--sf-outline-soft)',
        borderRadius: 'var(--sf-r-md)',
        padding: '14px 16px',
        minHeight: multiline ? rows * 22 + 28 : 48,
        fontFamily: 'var(--sf-sans)', fontSize: 15,
        color: value ? 'var(--sf-ink)' : 'var(--sf-ink-3)',
        lineHeight: 1.5,
        whiteSpace: multiline ? 'pre-wrap' : 'nowrap',
      }}>
        {value || placeholder}
      </div>
      {(helper || charCounter) && (
        <div style={{
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          padding: '0 4px',
        }}>
          <span className="sf-body" style={{ fontSize: 12, color: 'var(--sf-ink-3)' }}>
            {helper}
          </span>
          {charCounter && maxLength && (
            <span className="sf-body" style={{
              fontSize: 12, color: 'var(--sf-ink-3)',
              fontVariantNumeric: 'tabular-nums',
            }}>{len} / {maxLength}</span>
          )}
        </div>
      )}
    </div>
  );
}

// ── Breathing circle ───────────────────────────────────────────
function BreathingCircle({ size = 180 }) {
  return (
    <div style={{
      width: size, height: size, position: 'relative',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
    }}>
      {/* outer halo */}
      <div className="sf-breathe" style={{
        position: 'absolute', inset: 0,
        background: 'radial-gradient(circle, rgba(217,151,87,0.22), transparent 70%)',
      }} />
      {/* ring */}
      <div className="sf-breathe" style={{
        position: 'absolute', inset: size * 0.12,
        borderRadius: '50%',
        border: '1px solid var(--sf-primary)',
        opacity: 0.4,
      }} />
      <div className="sf-breathe" style={{
        position: 'absolute', inset: size * 0.22,
        borderRadius: '50%',
        background: 'radial-gradient(circle at 40% 40%, var(--sf-primary-soft), var(--sf-secondary-soft))',
        animationDelay: '-2s',
      }} />
      {/* center dot */}
      <div style={{
        width: 6, height: 6, borderRadius: '50%',
        background: 'var(--sf-primary)', opacity: 0.7,
        position: 'relative', zIndex: 2,
      }} />
    </div>
  );
}

// ── Hairline divider with optional center mark ─────────────────
function Rule({ ornament, style }) {
  if (ornament) {
    return (
      <div style={{
        display: 'flex', alignItems: 'center', gap: 14,
        color: 'var(--sf-outline)', ...style,
      }}>
        <div style={{ flex: 1, height: 1, background: 'currentColor', opacity: 0.4 }} />
        <Asterism size={10} />
        <div style={{ flex: 1, height: 1, background: 'currentColor', opacity: 0.4 }} />
      </div>
    );
  }
  return <div style={{ height: 1, background: 'var(--sf-outline-soft)', ...style }} />;
}

// ── Word-by-word fade-in for prayer text ───────────────────────
function FadeInProse({ text, delay = 0, perWord = 60, style }) {
  // split on whitespace, preserve line breaks
  const lines = text.split('\n');
  let wordIdx = 0;
  return (
    <div style={style}>
      {lines.map((line, li) => (
        <div key={li} style={{ marginBottom: line ? 0 : '0.6em' }}>
          {line.split(/(\s+)/).map((seg, si) => {
            if (/^\s+$/.test(seg)) return <span key={si}>{seg}</span>;
            const idx = wordIdx++;
            return (
              <span key={si} className="sf-fade-word" style={{
                animationDelay: `${delay + idx * perWord}ms`,
                display: 'inline-block',
              }}>{seg}</span>
            );
          })}
        </div>
      ))}
    </div>
  );
}

// ── Time-of-day gradient backdrop (Home top section) ───────────
function TimeOfDayBackdrop({ hour = 8, height = 220 }) {
  // 5-9 dawn (warm rose), 9-17 day (warm linen), 17-20 dusk (apricot), 20-5 night (deep linen)
  let stops;
  if (hour >= 5 && hour < 9) {
    stops = 'linear-gradient(180deg, #F4D9C0 0%, rgba(250,247,241,0) 100%)';
  } else if (hour >= 9 && hour < 17) {
    stops = 'linear-gradient(180deg, #F6EAD3 0%, rgba(250,247,241,0) 100%)';
  } else if (hour >= 17 && hour < 20) {
    stops = 'linear-gradient(180deg, #EAC5A8 0%, rgba(250,247,241,0) 100%)';
  } else {
    stops = 'linear-gradient(180deg, #DCD2BA 0%, rgba(250,247,241,0) 100%)';
  }
  return (
    <div style={{
      position: 'absolute', top: 0, left: 0, right: 0, height,
      background: stops, pointerEvents: 'none', opacity: 0.7,
    }} />
  );
}

Object.assign(window, {
  RecipientBadge, Asterism, PrimaryButton, TextButton, Chip,
  SectionHeader, BackTopBar, BottomNav, ToneCard, PrayerCard,
  TextField, BreathingCircle, Rule, FadeInProse, TimeOfDayBackdrop,
});
