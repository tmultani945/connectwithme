// SF Result variants — three alternative visual directions for the hero screen.
// Same prayer text + same content slots, fundamentally different visual concept each.
//
//   A · Bone & Ink     — hushed monochrome typographic restraint
//   B · The Window     — atmospheric composition with a horizon
//   C · Vellum         — illuminated-manuscript classical book

// ─────────────────────────────────────────────────────────────
// VARIANT A — Bone & Ink
// Strip almost all warmth. Single deep ink color on pale bone. No drop cap.
// Roman (not italic) prayer. Hairline rule instead of asterism. Lowercase
// recipient. Text-only actions. Feels like a poem in a fine letterpress book.
// ─────────────────────────────────────────────────────────────
function ResultVariantA() {
  const text = "the morning meets you as it always has — without asking what you have made of yourself, without counting what is undone.\n\nlet the small light through the window be enough for now. let your hands rest where they are. let the day arrive on its own slow feet, and find you already here, already breathing, already loved by something you do not have to name.";
  const ink = '#1F1A12';
  const bone = '#F2EFE6';

  return (
    <div className="sf-paper" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      background: bone, color: ink, position: 'relative',
    }}>
      {/* corner chrome — bare minimum */}
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '12px 16px',
      }}>
        <button style={{
          width: 36, height: 36, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          color: ink, opacity: 0.55,
        }}>
          <SFIcon name="close" size={18} color={ink} />
        </button>
        <span style={{
          fontFamily: 'var(--sf-serif)', fontSize: 12, fontStyle: 'italic',
          color: ink, opacity: 0.5, letterSpacing: 0.2,
        }}>to universe</span>
        <button style={{
          width: 36, height: 36, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          color: ink, opacity: 0.55,
        }}>
          <SFIcon name="menu" size={18} color={ink} />
        </button>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '32px 36px 0' }}>
        {/* tiny hairline rule, centered */}
        <div style={{
          width: 32, height: 1, background: ink, opacity: 0.35,
          margin: '0 auto 36px',
        }} />

        {/* prayer body — roman not italic, justified, tight */}
        <div style={{
          fontFamily: 'var(--sf-serif)', fontWeight: 400,
          fontSize: 19, lineHeight: 1.65, color: ink,
          textAlign: 'left', letterSpacing: '0.005em',
        }}>
          <div style={{ marginBottom: '0.65em' }}>
            <span style={{
              fontSize: 28, fontStyle: 'italic',
              letterSpacing: '-0.005em', color: ink,
            }}>The morning meets you</span> as it always has — without
            asking what you have made of yourself, without counting
            what is undone.
          </div>
          <div>
            Let the small light through the window be enough for now.
            Let your hands rest where they are. Let the day arrive on
            its own slow feet, and find you already here, already
            breathing, already loved by something you do not have to
            name.
          </div>
        </div>

        {/* end mark — a single tiny dot, like a printer's mark */}
        <div style={{
          marginTop: 40, textAlign: 'center',
          fontSize: 8, color: ink, opacity: 0.45,
        }}>·</div>

        {/* meta — tiny, italic, single line */}
        <div style={{
          marginTop: 32, textAlign: 'center',
          fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
          fontSize: 11.5, color: ink, opacity: 0.45, letterSpacing: 0.1,
        }}>
          gentle  ·  peace, clarity  ·  medium
        </div>

        <div style={{ height: 40 }} />
      </div>

      {/* actions — text labels with hairline separators, no buttons */}
      <div style={{
        padding: '20px 24px 8px',
        display: 'flex', justifyContent: 'center', alignItems: 'center',
      }}>
        {['Save', 'Practice', 'Again', 'Share', 'Copy'].map((a, i) => (
          <React.Fragment key={a}>
            {i > 0 && (
              <span style={{
                width: 1, height: 10, background: ink, opacity: 0.2,
                margin: '0 12px',
              }} />
            )}
            <span style={{
              fontFamily: 'var(--sf-sans)', fontSize: 12, fontWeight: 500,
              color: ink, opacity: 0.65, letterSpacing: 0.4,
              padding: '4px 0', cursor: 'pointer',
            }}>{a}</span>
          </React.Fragment>
        ))}
      </div>

      {/* done — just text */}
      <div style={{ padding: '6px 24px 18px', textAlign: 'center' }}>
        <span style={{
          fontFamily: 'var(--sf-sans)', fontSize: 13, fontWeight: 500,
          color: ink, letterSpacing: 0.5, padding: '10px 24px',
          cursor: 'pointer',
        }}>Done →</span>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// VARIANT B — The Window
// A horizon composition. Upper 55% is a soft sky gradient (the open). Lower
// 45% is the ground (solid linen). The prayer crosses the horizon. Recipient
// badge sits up in the sky like a label on a postcard. The asterism becomes
// a single small filled circle — the sun. The screen reads as a scene.
// ─────────────────────────────────────────────────────────────
function ResultVariantB() {
  return (
    <div style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      position: 'relative', overflow: 'hidden',
      color: 'var(--sf-primary-ink)',
    }}>
      {/* sky */}
      <div style={{
        position: 'absolute', top: 0, left: 0, right: 0, height: '58%',
        background: 'linear-gradient(180deg, #F4C9A5 0%, #F3DCC0 55%, #EDDFC4 100%)',
      }} />
      {/* ground */}
      <div style={{
        position: 'absolute', bottom: 0, left: 0, right: 0, height: '42%',
        background: 'var(--sf-bg)',
      }} />
      {/* horizon hairline */}
      <div style={{
        position: 'absolute', top: '58%', left: 0, right: 0, height: 1,
        background: 'rgba(60,45,20,0.18)',
      }} />
      {/* sun mark */}
      <div style={{
        position: 'absolute', top: 84, right: 56,
        width: 18, height: 18, borderRadius: '50%',
        background: '#E89968', opacity: 0.85,
        boxShadow: '0 0 24px 6px rgba(232,153,104,0.35)',
      }} />

      {/* top chrome */}
      <div style={{
        position: 'relative', zIndex: 2,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '10px 14px',
      }}>
        <button style={{
          width: 38, height: 38, borderRadius: '50%',
          background: 'rgba(255,253,248,0.55)', border: 'none', cursor: 'pointer',
          backdropFilter: 'blur(8px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-primary-ink)',
        }}>
          <SFIcon name="close" size={18} />
        </button>
        {/* postcard-style label */}
        <div style={{
          padding: '5px 12px', borderRadius: 2,
          background: 'rgba(255,253,248,0.65)', backdropFilter: 'blur(6px)',
          border: '1px solid rgba(60,45,20,0.15)',
          fontFamily: 'var(--sf-sans)', fontSize: 10, fontWeight: 600,
          letterSpacing: '0.24em', textTransform: 'uppercase',
          color: 'var(--sf-primary-ink)',
        }}>to · universe</div>
        <button style={{
          width: 38, height: 38, borderRadius: '50%',
          background: 'rgba(255,253,248,0.55)', border: 'none', cursor: 'pointer',
          backdropFilter: 'blur(8px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-primary-ink)',
        }}>
          <SFIcon name="menu" size={18} />
        </button>
      </div>

      {/* prayer crossing the horizon */}
      <div style={{
        position: 'relative', zIndex: 2,
        flex: 1, padding: '24px 32px 0', overflow: 'auto',
      }}>
        {/* first 4 lines in the sky */}
        <div style={{
          fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
          fontSize: 24, lineHeight: 1.5, fontWeight: 400,
          color: 'var(--sf-primary-ink)', letterSpacing: '-0.005em',
          marginBottom: 16,
        }}>
          the morning meets you<br/>
          as it always has —<br/>
          without asking what<br/>
          you have made of yourself,
        </div>

        {/* lower body — on the ground */}
        <div style={{
          fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
          fontSize: 16.5, lineHeight: 1.65, fontWeight: 400,
          color: 'var(--sf-ink-2)',
        }}>
          without counting what is undone.<br/>
          let the small light through the window<br/>
          be enough for now. let the day arrive<br/>
          on its own slow feet, and find you<br/>
          already here, already breathing,<br/>
          already loved by something<br/>
          you do not have to name.
        </div>

        {/* meta — anchored at the bottom of the column */}
        <div style={{
          marginTop: 32, paddingTop: 14,
          borderTop: '1px solid rgba(60,45,20,0.12)',
          fontFamily: 'var(--sf-sans)', fontSize: 11, fontWeight: 500,
          color: 'var(--sf-ink-3)', letterSpacing: '0.18em',
          textTransform: 'uppercase',
        }}>
          GENTLE / PEACE / CLARITY
        </div>
      </div>

      {/* actions — small hairline icons sitting on the ground */}
      <div style={{
        position: 'relative', zIndex: 2,
        padding: '14px 24px 12px',
        display: 'flex', justifyContent: 'space-around',
      }}>
        {[
          { icon: 'heart',      label: 'Save' },
          { icon: 'mic',        label: 'Practice' },
          { icon: 'regenerate', label: 'Again' },
          { icon: 'share',      label: 'Share' },
          { icon: 'copy',       label: 'Copy' },
        ].map(a => (
          <div key={a.label} style={{
            display: 'flex', flexDirection: 'column', alignItems: 'center',
            gap: 5, cursor: 'pointer',
            color: 'var(--sf-primary-ink)',
          }}>
            <SFIcon name={a.icon} size={20} strokeWidth={1.3} />
            <span style={{
              fontFamily: 'var(--sf-sans)', fontSize: 9.5,
              fontWeight: 500, color: 'var(--sf-ink-2)',
              letterSpacing: 0.3,
            }}>{a.label}</span>
          </div>
        ))}
      </div>

      <div style={{ position: 'relative', zIndex: 2, padding: '4px 24px 18px' }}>
        <div style={{
          padding: 14, borderRadius: 'var(--sf-r-pill)',
          background: 'transparent', border: '1px solid rgba(60,45,20,0.25)',
          textAlign: 'center', cursor: 'pointer',
          fontFamily: 'var(--sf-sans)', fontSize: 14, fontWeight: 500,
          color: 'var(--sf-primary-ink)', letterSpacing: 0.3,
        }}>Done</div>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// VARIANT C — Vellum / Manuscript
// Leans into illuminated-manuscript references (without religious iconography).
// Warm vellum background. Narrow text column with generous margins. Drop cap
// inside a hairline-bordered box. First word in small caps. Justified text.
// Leaf-dot asterism. Hairline circle action buttons. Feels like a page from
// a private book.
// ─────────────────────────────────────────────────────────────
function ResultVariantC() {
  const vellum = '#F4EAD3';
  const ochre = '#A85A2E';
  return (
    <div className="sf-paper" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      position: 'relative', overflow: 'hidden',
      background: vellum,
    }}>
      {/* extra paper grain layer for vellum feel */}
      <div style={{
        position: 'absolute', inset: 0, pointerEvents: 'none',
        background: 'radial-gradient(120% 80% at 50% 30%, rgba(255,250,230,0.25), transparent 60%), radial-gradient(80% 60% at 80% 100%, rgba(120,80,30,0.07), transparent 70%)',
      }} />

      {/* top chrome */}
      <div style={{
        position: 'relative', zIndex: 2,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '10px 18px',
      }}>
        <button style={{
          width: 36, height: 36, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          color: 'var(--sf-primary-ink)', opacity: 0.7,
        }}>
          <SFIcon name="back" size={20} />
        </button>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          color: ochre,
        }}>
          {/* tiny leaf */}
          <svg width="9" height="9" viewBox="0 0 9 9" fill={ochre}>
            <path d="M0.5 8.5 C2 3 5.5 1 8.5 0.5 C7 5 4.5 7.5 0.5 8.5z"/>
          </svg>
          <span style={{
            fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
            fontSize: 13, color: ochre, letterSpacing: 0.5,
          }}>to Universe</span>
          <svg width="9" height="9" viewBox="0 0 9 9" fill={ochre}>
            <path d="M8.5 8.5 C7 3 3.5 1 0.5 0.5 C2 5 4.5 7.5 8.5 8.5z"/>
          </svg>
        </div>
        <button style={{
          width: 36, height: 36, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          color: 'var(--sf-primary-ink)', opacity: 0.7,
        }}>
          <SFIcon name="menu" size={20} />
        </button>
      </div>

      {/* main column */}
      <div style={{
        position: 'relative', zIndex: 2, flex: 1, overflow: 'auto',
        padding: '24px 48px 0',
      }}>
        {/* top ornament — leaf cluster */}
        <div style={{
          display: 'flex', justifyContent: 'center', gap: 6, marginBottom: 28,
          color: ochre, opacity: 0.85,
        }}>
          {[0, 1, 2].map(i => (
            <svg key={i} width="10" height="10" viewBox="0 0 10 10" fill="currentColor"
              style={{ transform: `rotate(${i === 1 ? 0 : i === 0 ? -25 : 25}deg)` }}>
              <ellipse cx="5" cy="5" rx="1.5" ry="3.6"/>
            </svg>
          ))}
        </div>

        {/* prayer column with drop cap in box */}
        <div style={{ display: 'flex', gap: 14, marginBottom: 14 }}>
          <div style={{
            width: 64, height: 76, flexShrink: 0,
            border: `1px solid ${ochre}`,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            background: 'rgba(168,90,46,0.04)',
            position: 'relative',
          }}>
            <span style={{
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 66, fontWeight: 500,
              color: ochre, lineHeight: 0.85,
              letterSpacing: '-0.04em',
            }}>T</span>
            {/* corner ticks */}
            {[[2, 2], [58, 2], [2, 70], [58, 70]].map(([x, y], i) => (
              <svg key={i} width="6" height="6" viewBox="0 0 6 6"
                style={{ position: 'absolute', left: x, top: y }}>
                <path d="M0 3 L3 3 L3 0" stroke={ochre} strokeWidth="0.8" fill="none"
                  transform={`rotate(${i * 90} 3 3)`}/>
              </svg>
            ))}
          </div>
          <div style={{
            flex: 1, paddingTop: 6,
            fontFamily: 'var(--sf-serif)', fontWeight: 500,
            fontSize: 16, lineHeight: 1.55, color: 'var(--sf-primary-ink)',
            letterSpacing: '0.01em',
          }}>
            <span style={{
              fontVariant: 'small-caps', fontWeight: 600, letterSpacing: '0.06em',
              color: ochre,
            }}>he morning</span> meets you as it always has — without
            asking what you have made of yourself.
          </div>
        </div>

        {/* continued prayer body, justified */}
        <div style={{
          fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
          fontSize: 16, lineHeight: 1.65, color: 'var(--sf-primary-ink)',
          textAlign: 'justify', letterSpacing: '0.005em',
        }}>
          Let the small light through the window be enough for now.
          Let your hands rest where they are. Let the day arrive on
          its own slow feet, and find you already here, already
          breathing, already loved by something you do not have
          to name.
        </div>

        {/* leaf trefoil */}
        <div style={{
          display: 'flex', justifyContent: 'center', marginTop: 32, color: ochre,
        }}>
          <svg width="36" height="20" viewBox="0 0 36 20" fill="currentColor">
            <ellipse cx="6" cy="10" rx="1.6" ry="4" transform="rotate(-30 6 10)"/>
            <ellipse cx="18" cy="10" rx="1.6" ry="5"/>
            <ellipse cx="30" cy="10" rx="1.6" ry="4" transform="rotate(30 30 10)"/>
          </svg>
        </div>

        {/* meta in script-y italic */}
        <div style={{
          marginTop: 24, textAlign: 'center',
          fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
          fontSize: 12, color: 'var(--sf-ink-3)', letterSpacing: 0.2,
        }}>
          gentle · peace · clarity · medium
        </div>

        <div style={{ height: 32 }} />
      </div>

      {/* hairline circle actions */}
      <div style={{
        position: 'relative', zIndex: 2,
        padding: '14px 28px 8px',
        display: 'flex', justifyContent: 'space-around',
      }}>
        {[
          { icon: 'heart',      label: 'Save' },
          { icon: 'mic',        label: 'Practice' },
          { icon: 'regenerate', label: 'Again' },
          { icon: 'share',      label: 'Share' },
          { icon: 'copy',       label: 'Copy' },
        ].map(a => (
          <div key={a.label} style={{
            display: 'flex', flexDirection: 'column', alignItems: 'center',
            gap: 5, cursor: 'pointer',
          }}>
            <div style={{
              width: 38, height: 38, borderRadius: '50%',
              border: `1px solid ${ochre}`,
              background: 'transparent',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: ochre,
            }}>
              <SFIcon name={a.icon} size={16} strokeWidth={1.2} />
            </div>
            <span style={{
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 10.5, color: 'var(--sf-ink-2)',
            }}>{a.label}</span>
          </div>
        ))}
      </div>

      <div style={{ position: 'relative', zIndex: 2, padding: '8px 28px 18px', textAlign: 'center' }}>
        <span style={{
          fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
          fontSize: 14, color: ochre, letterSpacing: 0.4,
          padding: '10px 20px', cursor: 'pointer',
          borderBottom: `1px solid ${ochre}`, paddingBottom: 2,
        }}>finis</span>
      </div>
    </div>
  );
}

Object.assign(window, { ResultVariantA, ResultVariantB, ResultVariantC });
