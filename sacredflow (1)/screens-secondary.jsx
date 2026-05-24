// SF Secondary screens — Practice sheet, Reminder, Settings, Paywall, Help, Crisis

// ── Practice (bottom sheet) ────────────────────────────────────
// Shows the parent screen darkened with a half-height sheet over it.
function ScreenPractice() {
  return (
    <div style={{
      flex: 1, position: 'relative', display: 'flex', flexDirection: 'column',
      background: 'rgba(20,17,12,0.55)',
    }}>
      {/* dim background hint of detail screen */}
      <div style={{
        position: 'absolute', inset: 0,
        background: 'linear-gradient(180deg, var(--sf-bg) 0%, var(--sf-bg) 30%, transparent 50%)',
        opacity: 0.4,
      }} />

      {/* gives visual context of what's behind */}
      <div style={{
        position: 'relative', zIndex: 1, padding: '30px 28px 0',
        opacity: 0.4,
      }}>
        <RecipientBadge style={{ display: 'flex', justifyContent: 'center' }}>TO UNIVERSE</RecipientBadge>
        <Asterism size={9} style={{ margin: '20px 0' }} />
        <div className="sf-prayer-body" style={{
          fontSize: 18, lineHeight: 1.55, color: 'var(--sf-primary-ink)',
          textAlign: 'center',
        }}>
          the morning meets you<br/>as it always has —
        </div>
      </div>

      <div style={{ flex: 1 }} />

      {/* sheet */}
      <div style={{
        position: 'relative', zIndex: 2,
        background: 'var(--sf-surface)',
        borderTopLeftRadius: 28, borderTopRightRadius: 28,
        boxShadow: '0 -12px 40px rgba(30,20,5,0.18)',
        padding: '12px 24px 28px',
        display: 'flex', flexDirection: 'column',
        minHeight: '58%',
      }}>
        {/* handle */}
        <div style={{
          width: 36, height: 4, borderRadius: 2,
          background: 'var(--sf-outline)',
          opacity: 0.5,
          margin: '0 auto 18px',
        }} />

        {/* header row */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 28 }}>
          <div>
            <div className="sf-overline">repeat after me</div>
            <div style={{
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 22, color: 'var(--sf-primary-ink)', marginTop: 4,
            }}>Practice</div>
          </div>
          <div style={{
            padding: '6px 14px',
            background: 'var(--sf-primary-soft)',
            borderRadius: 'var(--sf-r-pill)',
            fontFamily: 'var(--sf-sans)', fontSize: 13, fontWeight: 600,
            color: 'var(--sf-primary)', fontVariantNumeric: 'tabular-nums',
            letterSpacing: 0.5,
          }}>3 / 8</div>
        </div>

        {/* phase */}
        <div className="sf-overline" style={{
          color: 'var(--sf-tertiary)', textAlign: 'center',
          letterSpacing: '0.32em', marginBottom: 24,
        }}>
          • listen •
        </div>

        {/* current chunk */}
        <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <div style={{
            fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
            fontSize: 28, lineHeight: 1.4, color: 'var(--sf-primary-ink)',
            textAlign: 'center', letterSpacing: '-0.005em',
          }}>
            let the small light<br/>
            through the window<br/>
            be enough for now.
          </div>
        </div>

        {/* progress dots */}
        <div style={{
          display: 'flex', justifyContent: 'center', gap: 6,
          marginTop: 32, marginBottom: 28,
        }}>
          {Array.from({ length: 8 }).map((_, i) => (
            <div key={i} style={{
              width: i === 2 ? 18 : 5, height: 5, borderRadius: 3,
              background: i <= 2 ? 'var(--sf-primary-ink)' : 'var(--sf-outline-soft)',
            }} />
          ))}
        </div>

        {/* controls */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 28 }}>
          <button style={{
            width: 48, height: 48, borderRadius: '50%',
            background: 'transparent', border: '1px solid var(--sf-outline-soft)',
            cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'var(--sf-ink-2)',
          }}>
            <SFIcon name="restart" size={18} />
          </button>
          <button style={{
            width: 72, height: 72, borderRadius: '50%',
            background: 'var(--sf-primary-ink)', border: 'none',
            cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'var(--sf-bg)',
            boxShadow: 'var(--sf-shadow-2)',
          }}>
            <SFIcon name="pause" size={26} />
          </button>
          <button style={{
            width: 48, height: 48, borderRadius: '50%',
            background: 'transparent', border: '1px solid var(--sf-outline-soft)',
            cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'var(--sf-ink-2)',
          }}>
            <SFIcon name="skip-next" size={18} />
          </button>
        </div>

        <div style={{ marginTop: 18, textAlign: 'center' }}>
          <TextButton style={{ color: 'var(--sf-ink-3)' }}>Close</TextButton>
        </div>
      </div>
    </div>
  );
}

// ── Reminder ───────────────────────────────────────────────────
function ScreenReminder() {
  const days = [
    { l: 'M', on: true }, { l: 'T', on: true }, { l: 'W', on: true },
    { l: 'T', on: true }, { l: 'F', on: true }, { l: 'S', on: false }, { l: 'S', on: true },
  ];
  return (
    <div className="sf-paper" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '18px 24px 4px', flexShrink: 0 }}>
        <div className="sf-overline">a gentle nudge</div>
        <div className="sf-display" style={{ fontSize: 32, marginTop: 6, color: 'var(--sf-primary-ink)' }}>
          Daily reflection
        </div>
        <div className="sf-body" style={{ marginTop: 8, maxWidth: 280 }}>
          A small daily nudge can help build the practice.
        </div>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '24px 24px 20px' }}>
        {/* enabled card */}
        <div style={{
          padding: '18px 20px',
          background: 'var(--sf-surface)',
          border: '1px solid var(--sf-outline-soft)',
          borderRadius: 'var(--sf-r-md)',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          marginBottom: 18,
        }}>
          <div>
            <div style={{ fontFamily: 'var(--sf-sans)', fontSize: 15, fontWeight: 600, color: 'var(--sf-ink)' }}>
              Enabled
            </div>
            <div className="sf-body" style={{ fontSize: 12.5, marginTop: 2 }}>
              You'll receive one notification per active day.
            </div>
          </div>
          {/* switch */}
          <div style={{
            width: 44, height: 26, borderRadius: 'var(--sf-r-pill)',
            background: 'var(--sf-primary-ink)', position: 'relative',
          }}>
            <div style={{
              position: 'absolute', top: 3, left: 21, width: 20, height: 20,
              borderRadius: '50%', background: 'var(--sf-bg)',
              boxShadow: '0 1px 3px rgba(0,0,0,0.15)',
            }} />
          </div>
        </div>

        {/* time picker card */}
        <SectionHeader style={{ marginBottom: 12 }}>Time</SectionHeader>
        <div style={{
          padding: '24px 20px',
          background: 'var(--sf-surface)',
          border: '1px solid var(--sf-outline-soft)',
          borderRadius: 'var(--sf-r-md)',
          marginBottom: 24, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10,
        }}>
          <span style={{
            fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
            fontSize: 54, color: 'var(--sf-primary-ink)', fontVariantNumeric: 'tabular-nums',
            lineHeight: 1,
          }}>7:15</span>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
            <span style={{
              padding: '4px 10px', borderRadius: 'var(--sf-r-pill)',
              background: 'var(--sf-primary-ink)', color: 'var(--sf-bg)',
              fontFamily: 'var(--sf-sans)', fontSize: 11, fontWeight: 600, letterSpacing: 0.5,
            }}>AM</span>
            <span style={{
              padding: '4px 10px', borderRadius: 'var(--sf-r-pill)',
              color: 'var(--sf-ink-3)',
              fontFamily: 'var(--sf-sans)', fontSize: 11, fontWeight: 600, letterSpacing: 0.5,
            }}>PM</span>
          </div>
        </div>

        {/* days */}
        <SectionHeader style={{ marginBottom: 12 }}>Days</SectionHeader>
        <div style={{ display: 'flex', gap: 6, marginBottom: 20 }}>
          {days.map((d, i) => (
            <div key={i} style={{
              flex: 1, aspectRatio: '1', borderRadius: '50%',
              background: d.on ? 'var(--sf-primary-ink)' : 'transparent',
              border: `1px solid ${d.on ? 'var(--sf-primary-ink)' : 'var(--sf-outline-soft)'}`,
              color: d.on ? 'var(--sf-bg)' : 'var(--sf-ink-2)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontFamily: 'var(--sf-sans)', fontSize: 13, fontWeight: 600,
            }}>{d.l}</div>
          ))}
        </div>
        <div className="sf-body" style={{
          fontSize: 12.5, color: 'var(--sf-ink-3)', textAlign: 'center',
          fontStyle: 'italic', fontFamily: 'var(--sf-serif)',
          padding: '4px 28px',
        }}>
          Weekdays at 7:15 in the morning, with Sunday quiet.
        </div>
      </div>

      <div style={{
        padding: '12px 24px 16px', display: 'flex', flexDirection: 'column', gap: 4,
        borderTop: '1px solid var(--sf-outline-soft)',
      }}>
        <PrimaryButton>Save changes</PrimaryButton>
        <TextButton style={{ alignSelf: 'center', color: 'var(--sf-error)' }}>Remove reminder</TextButton>
      </div>

      <BottomNav active="reminder" />
    </div>
  );
}

// ── Settings ───────────────────────────────────────────────────
function ScreenSettings() {
  const row = (label, value, icon, opts = {}) => (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '14px 0', gap: 14,
      borderBottom: opts.last ? 'none' : '1px solid var(--sf-outline-soft)',
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12, minWidth: 0 }}>
        {icon && <div style={{
          width: 36, height: 36, borderRadius: 10,
          background: 'var(--sf-primary-soft)',
          color: 'var(--sf-primary)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
        }}>
          <SFIcon name={icon} size={17} />
        </div>}
        <div>
          <div style={{ fontFamily: 'var(--sf-sans)', fontSize: 14.5, fontWeight: 500, color: 'var(--sf-ink)' }}>
            {label}
          </div>
          {opts.sub && <div className="sf-body" style={{ fontSize: 12.5, marginTop: 2 }}>{opts.sub}</div>}
        </div>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexShrink: 0 }}>
        {value && <span style={{ fontFamily: 'var(--sf-sans)', fontSize: 13.5, color: 'var(--sf-ink-2)' }}>{value}</span>}
        {opts.toggle !== undefined ? (
          <div style={{
            width: 38, height: 22, borderRadius: 'var(--sf-r-pill)',
            background: opts.toggle ? 'var(--sf-primary-ink)' : 'var(--sf-outline-soft)',
            position: 'relative',
          }}>
            <div style={{
              position: 'absolute', top: 2, left: opts.toggle ? 18 : 2,
              width: 18, height: 18, borderRadius: '50%', background: 'var(--sf-bg)',
              boxShadow: '0 1px 3px rgba(0,0,0,0.15)',
            }} />
          </div>
        ) : <SFIcon name="chevron-right" size={16} color="var(--sf-ink-3)" />}
      </div>
    </div>
  );
  const section = (title, rows) => (
    <div style={{ marginBottom: 24 }}>
      <SectionHeader style={{ marginBottom: 10 }}>{title}</SectionHeader>
      <div style={{
        background: 'var(--sf-surface)',
        border: '1px solid var(--sf-outline-soft)',
        borderRadius: 'var(--sf-r-md)',
        padding: '0 18px',
      }}>
        {rows.map((r, i) => React.cloneElement(r, { key: i }))}
      </div>
    </div>
  );

  return (
    <div className="sf-paper" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '18px 24px 4px', flexShrink: 0 }}>
        <div className="sf-overline">preferences</div>
        <div className="sf-display" style={{ fontSize: 32, marginTop: 6, color: 'var(--sf-primary-ink)' }}>
          Settings
        </div>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '20px 24px 20px' }}>
        {/* Plus banner */}
        <div style={{
          padding: '18px 20px', marginBottom: 24,
          borderRadius: 'var(--sf-r-md)',
          background: 'linear-gradient(135deg, #3D3527 0%, #5C5240 100%)',
          color: 'var(--sf-bg)',
          display: 'flex', alignItems: 'center', gap: 14,
          position: 'relative', overflow: 'hidden',
        }}>
          <div style={{
            position: 'absolute', right: -20, top: -20, opacity: 0.18,
          }}>
            <Asterism size={28} color="#FAF7F1" />
          </div>
          <div style={{ flex: 1, position: 'relative' }}>
            <div className="sf-overline" style={{ color: 'rgba(250,247,241,0.7)' }}>free plan</div>
            <div style={{
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 19, marginTop: 4,
            }}>Keep the practice flowing.</div>
            <div className="sf-body" style={{
              color: 'rgba(250,247,241,0.75)', fontSize: 12.5, marginTop: 6,
            }}>Unlimited reflections, longer prayers, voice reading.</div>
          </div>
          <SFIcon name="chevron-right" size={18} color="#FAF7F1" />
        </div>

        {section('Practice', [
          row('Default addressed to', 'Universe', 'sparkle'),
          row('Default tone', 'Gentle', 'leaf'),
          row('Default type', 'Reflection', 'note', { last: true }),
        ])}

        {section('Appearance', [
          row('Theme', 'System', 'sun'),
          row('Use system color', null, 'eye', { toggle: false, sub: 'Android 12+ Material You', last: true }),
        ])}

        {section('Library', [
          row('Saved reflections', '24', 'library'),
          row('Backup & restore', null, 'cloud-off', { sub: 'On this device only', last: true }),
        ])}

        {section('About', [
          row('Help & disclaimer', null, 'help'),
          row('Privacy policy', null, 'shield'),
          row('Version', '1.0.2 (build 14)', null, { last: true }),
        ])}

        <div style={{ textAlign: 'center', padding: '8px 0 16px' }}>
          <Asterism size={10} />
          <div className="sf-overline" style={{ marginTop: 10, fontSize: 9.5 }}>
            made slowly · with care
          </div>
        </div>
      </div>

      <BottomNav active="settings" />
    </div>
  );
}

// ── Paywall ────────────────────────────────────────────────────
function ScreenPaywall() {
  const features = [
    { icon: 'sparkle',    label: 'Unlimited reflections' },
    { icon: 'note',       label: 'Long-form prayers' },
    { icon: 'mic',        label: 'Gentle voice reading', soon: true },
    { icon: 'bell',       label: 'Multiple reminders' },
    { icon: 'share',      label: 'Beautiful share cards' },
    { icon: 'leaf',       label: 'Priority generation' },
  ];
  return (
    <div className="sf-paper sf-vignette" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      position: 'relative', overflow: 'hidden',
      background: 'linear-gradient(180deg, #F4D9C0 0%, var(--sf-bg) 55%)',
    }}>
      {/* close */}
      <div style={{ display: 'flex', justifyContent: 'flex-end', padding: '12px 14px 0' }}>
        <button style={{
          width: 40, height: 40, borderRadius: '50%',
          background: 'rgba(255,253,248,0.7)',
          border: '1px solid var(--sf-outline-soft)',
          cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-ink-2)',
        }}>
          <SFIcon name="close" size={18} />
        </button>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '0 28px 24px', position: 'relative', zIndex: 2 }}>
        <Asterism size={11} style={{ margin: '12px 0 22px' }} />
        <div className="sf-display" style={{
          fontSize: 38, marginBottom: 14, color: 'var(--sf-primary-ink)',
        }}>
          Keep the practice<br/>flowing.
        </div>
        <div className="sf-body" style={{ fontSize: 15, marginBottom: 28, maxWidth: 320 }}>
          Unlimited reflections, long-form prayers, gentle voice reading,
          and more daily reminders.
        </div>

        {/* features */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginBottom: 28 }}>
          {features.map(f => (
            <div key={f.label} style={{
              display: 'flex', alignItems: 'center', gap: 14,
              padding: '12px 14px',
              background: 'var(--sf-surface)',
              border: '1px solid var(--sf-outline-soft)',
              borderRadius: 'var(--sf-r-md)',
            }}>
              <div style={{
                width: 32, height: 32, borderRadius: 10,
                background: 'var(--sf-primary-soft)',
                color: 'var(--sf-primary)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <SFIcon name={f.icon} size={16} />
              </div>
              <span style={{ flex: 1, fontFamily: 'var(--sf-sans)', fontSize: 14, fontWeight: 500, color: 'var(--sf-ink)' }}>
                {f.label}
              </span>
              {f.soon ? (
                <span className="sf-overline" style={{ fontSize: 9, color: 'var(--sf-ink-3)' }}>soon</span>
              ) : (
                <SFIcon name="check" size={16} color="var(--sf-secondary)" strokeWidth={2} />
              )}
            </div>
          ))}
        </div>

        {/* plan cards */}
        <div style={{ display: 'flex', gap: 10, marginBottom: 16 }}>
          {/* yearly */}
          <div style={{
            flex: 1, padding: '18px 14px',
            borderRadius: 'var(--sf-r-md)',
            background: 'var(--sf-primary-ink)', color: 'var(--sf-bg)',
            position: 'relative',
          }}>
            <div style={{
              position: 'absolute', top: -8, right: 10,
              padding: '3px 8px', borderRadius: 'var(--sf-r-pill)',
              background: 'var(--sf-tertiary)', color: 'var(--sf-bg)',
              fontFamily: 'var(--sf-sans)', fontSize: 10, fontWeight: 700, letterSpacing: 0.5,
            }}>SAVE 50%</div>
            <div className="sf-overline" style={{ color: 'rgba(250,247,241,0.7)' }}>yearly</div>
            <div style={{
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 30, marginTop: 6, letterSpacing: '-0.01em',
            }}>$29.99</div>
            <div style={{
              fontFamily: 'var(--sf-sans)', fontSize: 11, opacity: 0.7, marginTop: 4,
            }}>$2.50 / month</div>
          </div>
          {/* monthly */}
          <div style={{
            flex: 1, padding: '18px 14px',
            borderRadius: 'var(--sf-r-md)',
            background: 'var(--sf-surface)',
            border: '1px solid var(--sf-outline-soft)',
          }}>
            <div className="sf-overline">monthly</div>
            <div style={{
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 30, marginTop: 6, color: 'var(--sf-primary-ink)', letterSpacing: '-0.01em',
            }}>$4.99</div>
            <div style={{
              fontFamily: 'var(--sf-sans)', fontSize: 11, color: 'var(--sf-ink-3)', marginTop: 4,
            }}>billed monthly</div>
          </div>
        </div>
      </div>

      <div style={{
        padding: '12px 24px 16px', position: 'relative', zIndex: 2,
        display: 'flex', flexDirection: 'column', gap: 4,
        background: 'linear-gradient(0deg, var(--sf-bg) 75%, transparent)',
      }}>
        <PrimaryButton>Start Sacred Flow Plus</PrimaryButton>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '4px 8px 0' }}>
          <TextButton style={{ color: 'var(--sf-ink-3)', fontSize: 12 }}>Restore purchases</TextButton>
          <span className="sf-overline" style={{ fontSize: 9.5 }}>cancel anytime</span>
        </div>
      </div>
    </div>
  );
}

// ── Help ───────────────────────────────────────────────────────
function ScreenHelp() {
  return (
    <div className="sf-paper" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <BackTopBar title="Help" />
      <div style={{ flex: 1, overflow: 'auto', padding: '4px 24px 24px' }}>
        <SectionHeader style={{ marginBottom: 10 }}>About</SectionHeader>
        <div style={{
          fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
          fontSize: 17, lineHeight: 1.5, color: 'var(--sf-primary-ink)',
          marginBottom: 28,
        }}>
          Sacred Flow is a quiet, single-player tool for putting words to
          your prayers, intentions, and reflections — in whatever spiritual
          language is yours.
        </div>

        {/* Disclaimer card */}
        <div style={{
          padding: '16px 18px', marginBottom: 24,
          background: 'var(--sf-tertiary-soft)',
          border: '1px solid var(--sf-tertiary)',
          borderRadius: 'var(--sf-r-md)',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
            <SFIcon name="shield" size={15} color="var(--sf-tertiary)" />
            <SectionHeader style={{ color: 'var(--sf-tertiary)' }}>Disclaimer</SectionHeader>
          </div>
          <div className="sf-body" style={{ fontSize: 13, lineHeight: 1.55, color: 'var(--sf-primary-ink)' }}>
            Sacred Flow generates text using AI based on your inputs. It is not a
            substitute for medical, mental health, religious, legal, or financial
            guidance. If you're in crisis, please reach out to a qualified professional.
          </div>
        </div>

        {/* support row */}
        <div style={{
          padding: '14px 18px', marginBottom: 28,
          background: 'var(--sf-surface)',
          border: '1px solid var(--sf-outline-soft)',
          borderRadius: 'var(--sf-r-md)',
          display: 'flex', alignItems: 'center', gap: 14,
        }}>
          <div style={{
            width: 36, height: 36, borderRadius: 10,
            background: 'var(--sf-secondary-soft)', color: 'var(--sf-secondary)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <SFIcon name="phone" size={16} />
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontFamily: 'var(--sf-sans)', fontSize: 14, fontWeight: 600, color: 'var(--sf-ink)' }}>
              If you need support
            </div>
            <div className="sf-body" style={{ fontSize: 12, marginTop: 2 }}>Crisis resources, available 24/7.</div>
          </div>
          <SFIcon name="chevron-right" size={16} color="var(--sf-ink-3)" />
        </div>

        <SectionHeader style={{ marginBottom: 10 }}>Frequently asked</SectionHeader>
        <div style={{
          background: 'var(--sf-surface)',
          border: '1px solid var(--sf-outline-soft)',
          borderRadius: 'var(--sf-r-md)',
          padding: '0 18px', marginBottom: 24,
        }}>
          {[
            { q: 'How is this different from other prayer apps?', open: true,
              a: 'Sacred Flow is belief-neutral. It writes in your spiritual language, whatever that may be — or none at all. There is no doctrine, no feed, no community.' },
            { q: 'Where is my data stored?' },
            { q: 'Can I use it offline?' },
            { q: 'How does the AI choose words?' },
            { q: 'Why no streaks or badges?' },
            { q: "Can I export my library?" },
            { q: 'Is there a website?' },
            { q: 'How do I cancel Plus?' },
          ].map((f, i) => (
            <div key={i} style={{
              padding: '14px 0',
              borderBottom: i < 7 ? '1px solid var(--sf-outline-soft)' : 'none',
            }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8 }}>
                <span style={{
                  fontFamily: 'var(--sf-sans)', fontSize: 14, fontWeight: 500,
                  color: 'var(--sf-ink)', flex: 1,
                }}>{f.q}</span>
                <SFIcon name={f.open ? 'chevron-down' : 'chevron-right'} size={16} color="var(--sf-ink-3)" />
              </div>
              {f.open && (
                <div className="sf-body" style={{ marginTop: 10, fontSize: 13.5, lineHeight: 1.6 }}>
                  {f.a}
                </div>
              )}
            </div>
          ))}
        </div>

        <div style={{ textAlign: 'center', padding: '4px 0' }}>
          <Asterism size={9} style={{ marginBottom: 10 }} />
          <div className="sf-overline" style={{ fontSize: 10 }}>contact · hello@sacredflow.app</div>
        </div>
      </div>
    </div>
  );
}

// ── Crisis Resources ───────────────────────────────────────────
function ScreenCrisis() {
  const resources = [
    {
      region: 'UNITED STATES',
      name: '988 Suicide & Crisis Lifeline',
      desc: '24/7 support, free, confidential.',
      actions: [
        { icon: 'phone', label: 'Call 988' },
        { icon: 'message', label: 'Text 988' },
        { icon: 'external', label: '988lifeline.org' },
      ],
    },
    {
      region: 'UNITED KINGDOM',
      name: 'Samaritans',
      desc: 'Free to call, any time, any reason.',
      actions: [
        { icon: 'phone', label: 'Call 116 123' },
        { icon: 'external', label: 'samaritans.org' },
      ],
    },
    {
      region: 'CANADA',
      name: 'Talk Suicide Canada',
      desc: 'Bilingual, 24/7.',
      actions: [
        { icon: 'phone', label: 'Call 1-833-456-4566' },
        { icon: 'message', label: 'Text 45645' },
      ],
    },
  ];
  return (
    <div className="sf-paper" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      background: 'var(--sf-secondary-soft)',
    }}>
      <BackTopBar />
      <div style={{ flex: 1, overflow: 'auto', padding: '4px 24px 16px' }}>
        <Asterism size={11} style={{ marginBottom: 22, color: 'var(--sf-secondary)' }} />
        <div className="sf-display" style={{
          fontSize: 32, color: 'var(--sf-primary-ink)', lineHeight: 1.1,
          marginBottom: 16,
        }}>
          Please take<br/>a moment.
        </div>
        <div className="sf-body" style={{
          fontSize: 15, lineHeight: 1.6, color: 'var(--sf-ink-2)',
          marginBottom: 32, maxWidth: 320,
        }}>
          What you shared sounds heavy. You don't have to carry it alone.
          If you're in crisis or thinking about hurting yourself, please
          talk to someone right now.
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {resources.map(r => (
            <div key={r.region} style={{
              padding: '18px 20px',
              background: 'var(--sf-surface)',
              borderRadius: 'var(--sf-r-md)',
              border: '1px solid var(--sf-outline-soft)',
            }}>
              <div className="sf-overline" style={{ color: 'var(--sf-secondary)', marginBottom: 8 }}>
                {r.region}
              </div>
              <div style={{
                fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
                fontSize: 21, color: 'var(--sf-primary-ink)', marginBottom: 4,
                letterSpacing: '-0.005em',
              }}>{r.name}</div>
              <div className="sf-body" style={{ fontSize: 13, marginBottom: 14 }}>{r.desc}</div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
                {r.actions.map(a => (
                  <button key={a.label} style={{
                    display: 'inline-flex', alignItems: 'center', gap: 8,
                    padding: '10px 16px', borderRadius: 'var(--sf-r-pill)',
                    background: 'var(--sf-primary-ink)', color: 'var(--sf-bg)',
                    border: 'none', cursor: 'pointer',
                    fontFamily: 'var(--sf-sans)', fontSize: 13.5, fontWeight: 500,
                  }}>
                    <SFIcon name={a.icon} size={15} />
                    {a.label}
                  </button>
                ))}
              </div>
            </div>
          ))}

          {/* global */}
          <div style={{
            padding: '18px 20px',
            background: 'transparent',
            borderRadius: 'var(--sf-r-md)',
            border: '1px dashed var(--sf-outline)',
          }}>
            <div className="sf-overline" style={{ color: 'var(--sf-ink-3)', marginBottom: 6 }}>
              ELSEWHERE IN THE WORLD
            </div>
            <div style={{
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 17, color: 'var(--sf-primary-ink)', marginBottom: 10,
            }}>Find a helpline near you.</div>
            <button style={{
              display: 'inline-flex', alignItems: 'center', gap: 8,
              padding: '10px 16px', borderRadius: 'var(--sf-r-pill)',
              background: 'transparent', color: 'var(--sf-primary-ink)',
              border: '1px solid var(--sf-outline)', cursor: 'pointer',
              fontFamily: 'var(--sf-sans)', fontSize: 13.5, fontWeight: 500,
            }}>
              <SFIcon name="external" size={15} />
              findahelpline.com
            </button>
          </div>
        </div>
      </div>

      <div style={{ padding: '12px 24px 16px', flexShrink: 0 }}>
        <PrimaryButton style={{
          background: 'var(--sf-surface)', color: 'var(--sf-primary-ink)',
          border: '1px solid var(--sf-outline)', height: 52,
        }}>When you're ready, come back</PrimaryButton>
      </div>
    </div>
  );
}

Object.assign(window, {
  ScreenPractice, ScreenReminder, ScreenSettings,
  ScreenPaywall, ScreenHelp, ScreenCrisis,
});
