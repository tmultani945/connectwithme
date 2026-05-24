// SF Main screens — Home, Create, Generation Loading, Result, Library, Prayer Detail

// ── Home ───────────────────────────────────────────────────────
function ScreenHome() {
  const recent = [
    { recipient: 'TO UNIVERSE',      preview: 'May the work of my hands today rise from a settled place. Let me move with care and without hurry, knowing that what is mine to do will meet me.', date: 'TODAY',     fav: true,  accent: 'var(--sf-primary)' },
    { recipient: 'TO HIGHER SELF',   preview: 'I return to what is already steady within me. The breath beneath the noise, the quiet beneath the wanting.', date: 'YESTERDAY', fav: false, accent: 'var(--sf-secondary)' },
    { recipient: 'TO THE SACRED',    preview: 'For the ones I love who are not here this morning — let them be held in some larger arms.', date: '3 DAYS AGO', fav: true,  accent: 'var(--sf-tertiary)' },
  ];
  return (
    <div className="sf-paper" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      position: 'relative', overflow: 'hidden',
    }}>
      <TimeOfDayBackdrop hour={8} height={300} />
      <div style={{ position: 'relative', zIndex: 2, flex: 1, display: 'flex', flexDirection: 'column', overflow: 'auto' }}>
        {/* Top mini-bar */}
        <div style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          padding: '14px 24px 0',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Asterism size={8} />
            <span style={{
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 17, color: 'var(--sf-primary-ink)', letterSpacing: '-0.005em',
            }}>Sacred Flow</span>
          </div>
          <button style={{
            background: 'transparent', border: 'none', cursor: 'pointer',
            width: 36, height: 36, display: 'flex', alignItems: 'center',
            justifyContent: 'center', color: 'var(--sf-ink-2)',
          }}>
            <SFIcon name="sun" size={20} strokeWidth={1.3} />
          </button>
        </div>

        {/* Greeting */}
        <div style={{ padding: '28px 24px 8px' }}>
          <div className="sf-overline" style={{ marginBottom: 12 }}>
            Tuesday · May 23
          </div>
          <div className="sf-display" style={{
            fontSize: 44, lineHeight: 1.05,
            color: 'var(--sf-primary-ink)', marginBottom: 8,
          }}>
            Good morning,<br/>Anya.
          </div>
          <div className="sf-body" style={{
            fontSize: 15, color: 'var(--sf-ink-2)', maxWidth: 280,
          }}>
            What would you like to bring<br/>into today?
          </div>
        </div>

        {/* Primary CTA card */}
        <div style={{ padding: '20px 24px 0' }}>
          <div style={{
            padding: '20px 22px',
            borderRadius: 'var(--sf-r-lg)',
            background: 'var(--sf-primary-ink)',
            color: 'var(--sf-bg)',
            display: 'flex', alignItems: 'center', gap: 16,
            boxShadow: 'var(--sf-shadow-2)',
          }}>
            <div style={{
              width: 44, height: 44, borderRadius: '50%',
              background: 'rgba(255,253,248,0.12)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              flexShrink: 0,
            }}>
              <SFIcon name="plus" size={20} color="#FAF7F1" strokeWidth={1.5} />
            </div>
            <div style={{ flex: 1 }}>
              <div style={{
                fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
                fontSize: 20, fontWeight: 400, marginBottom: 2,
              }}>Create a reflection</div>
              <div style={{
                fontFamily: 'var(--sf-sans)', fontSize: 12,
                opacity: 0.7, letterSpacing: 0.15,
              }}>3 of 5 free reflections today</div>
            </div>
            <SFIcon name="arrow-right" size={20} color="#FAF7F1" />
          </div>
        </div>

        {/* Recent */}
        <div style={{ padding: '34px 0 16px' }}>
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '0 24px', marginBottom: 14,
          }}>
            <SectionHeader>Recent reflections</SectionHeader>
            <span className="sf-overline" style={{ color: 'var(--sf-primary)' }}>See all</span>
          </div>
          <div style={{
            display: 'flex', gap: 12, overflowX: 'auto',
            padding: '0 24px 8px',
          }}>
            {recent.map((r, i) => (
              <PrayerCard key={i} {...r} />
            ))}
          </div>
        </div>

        {/* Quiet quote footer */}
        <div style={{ padding: '8px 28px 24px', textAlign: 'center', marginTop: 'auto' }}>
          <Rule ornament style={{ marginBottom: 14 }} />
          <div style={{
            fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
            fontSize: 14, color: 'var(--sf-ink-3)', lineHeight: 1.5,
          }}>
            "Begin again, and again, and again."
          </div>
        </div>
      </div>
      <BottomNav active="home" />
    </div>
  );
}

// ── Create ─────────────────────────────────────────────────────
function ScreenCreate() {
  const types = ['Prayer', 'Intention', 'Gratitude', 'Healing', 'Reflection', 'Custom'];
  const recipients = ['God', 'Universe', 'Nature', 'Higher Self', 'Divine Energy', 'Ancestors', 'The Sacred'];
  const needs = ['Peace', 'Healing', 'Clarity', 'Gratitude', 'Strength', 'Hope', 'Success', 'Abundance', 'Forgiveness', 'Protection', 'Joy', 'Courage'];
  const tones = ['Gentle', 'Hopeful', 'Thankful', 'Grounded', 'Powerful', 'Surrendering'];
  const lengths = [
    { name: 'Short',  range: '60–100' },
    { name: 'Medium', range: '120–180' },
    { name: 'Long',   range: '220–320', locked: true },
  ];
  return (
    <div className="sf-paper" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <BackTopBar title="New reflection" sub="Step into what's here" />
      <div style={{ flex: 1, overflow: 'auto', padding: '8px 24px 24px' }}>

        <SectionHeader style={{ marginBottom: 12 }}>Type</SectionHeader>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 28 }}>
          {types.map(t => <Chip key={t} dense selected={t === 'Reflection'}>{t}</Chip>)}
        </div>

        <SectionHeader style={{ marginBottom: 12 }}>Addressed to</SectionHeader>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 12 }}>
          {recipients.map(r => <Chip key={r} dense selected={r === 'Universe'}>{r}</Chip>)}
          <Chip dense>
            <SFIcon name="plus" size={12} style={{ marginRight: 2 }} />
            New custom…
          </Chip>
        </div>
        <div style={{
          marginTop: 4, padding: '8px 12px',
          background: 'var(--sf-surface-2)', borderRadius: 'var(--sf-r-sm)',
          display: 'flex', alignItems: 'center', gap: 8,
        }}>
          <SFIcon name="bookmark" size={13} color="var(--sf-ink-3)" />
          <span className="sf-overline" style={{ fontSize: 9.5 }}>Recent:</span>
          <span style={{ fontFamily: 'var(--sf-serif)', fontStyle: 'italic', fontSize: 14, color: 'var(--sf-ink-2)' }}>
            Source · Mother Earth
          </span>
        </div>

        <div style={{ height: 28 }} />

        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 12 }}>
          <SectionHeader>What you'd like to bring in</SectionHeader>
          <span className="sf-overline" style={{ fontSize: 9.5 }}>2 of 2</span>
        </div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 28 }}>
          {needs.map(n => {
            const on = n === 'Peace' || n === 'Clarity';
            return <Chip key={n} dense selected={on} disabled={!on}>{n}</Chip>;
          })}
        </div>

        <SectionHeader style={{ marginBottom: 12 }}>Tone</SectionHeader>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 28 }}>
          {tones.map(t => <Chip key={t} dense selected={t === 'Gentle'}>{t}</Chip>)}
        </div>

        <SectionHeader style={{ marginBottom: 12 }}>Length</SectionHeader>
        <div style={{
          display: 'flex', padding: 4,
          background: 'var(--sf-surface-2)', borderRadius: 'var(--sf-r-pill)',
          marginBottom: 28,
        }}>
          {lengths.map(l => {
            const on = l.name === 'Medium';
            return (
              <div key={l.name} style={{
                flex: 1, padding: '10px 8px',
                borderRadius: 'var(--sf-r-pill)',
                background: on ? 'var(--sf-surface)' : 'transparent',
                boxShadow: on ? 'var(--sf-shadow-1)' : 'none',
                textAlign: 'center', cursor: 'pointer', opacity: l.locked ? 0.55 : 1,
              }}>
                <div style={{
                  fontFamily: 'var(--sf-sans)', fontSize: 13, fontWeight: 600,
                  color: on ? 'var(--sf-primary-ink)' : 'var(--sf-ink-2)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 4,
                }}>
                  {l.name}
                  {l.locked && <SFIcon name="lock" size={11} />}
                </div>
                <div style={{
                  fontFamily: 'var(--sf-sans)', fontSize: 10, marginTop: 2,
                  color: 'var(--sf-ink-3)', letterSpacing: 0.2,
                }}>{l.range} words</div>
              </div>
            );
          })}
        </div>

        <SectionHeader style={{ marginBottom: 12 }}>Personal context · optional</SectionHeader>
        <TextField
          multiline rows={4}
          placeholder="A few words about what's on your mind…"
          maxLength={500} charCounter
          helper="Stays on your device."
        />

        <div style={{ textAlign: 'center', marginTop: 24 }}>
          <TextButton style={{ color: 'var(--sf-ink-3)' }}>↺  Reset to defaults</TextButton>
        </div>
      </div>

      {/* pinned bottom CTA */}
      <div style={{
        padding: '14px 24px 16px',
        background: 'linear-gradient(0deg, var(--sf-bg) 70%, rgba(250,247,241,0))',
        borderTop: '1px solid var(--sf-outline-soft)',
      }}>
        <PrimaryButton icon="sparkle">Generate</PrimaryButton>
      </div>
    </div>
  );
}

// ── Generation Loading ─────────────────────────────────────────
function ScreenLoading() {
  return (
    <div className="sf-paper sf-vignette" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      alignItems: 'center', justifyContent: 'center',
      padding: '40px 32px', position: 'relative', overflow: 'hidden',
      background: 'radial-gradient(circle at 50% 40%, #F4E5CC, var(--sf-bg) 70%)',
    }}>
      <div style={{ position: 'relative', zIndex: 2, textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <BreathingCircle size={220} />
        <div className="sf-overline" style={{ marginTop: 56, color: 'var(--sf-primary)' }}>
          listening
        </div>
        <div style={{
          fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
          fontSize: 26, color: 'var(--sf-primary-ink)',
          marginTop: 18, lineHeight: 1.3, letterSpacing: '-0.005em',
        }}>
          choosing words<br/>for what you brought.
        </div>
        <div style={{
          marginTop: 36, display: 'flex', gap: 6, alignItems: 'center',
          color: 'var(--sf-ink-3)',
        }}>
          <span style={{ width: 6, height: 6, borderRadius: '50%', background: 'currentColor', opacity: 0.6, animation: 'sf-breathe 1.4s ease-in-out infinite' }} />
          <span style={{ width: 6, height: 6, borderRadius: '50%', background: 'currentColor', opacity: 0.6, animation: 'sf-breathe 1.4s ease-in-out infinite', animationDelay: '0.18s' }} />
          <span style={{ width: 6, height: 6, borderRadius: '50%', background: 'currentColor', opacity: 0.6, animation: 'sf-breathe 1.4s ease-in-out infinite', animationDelay: '0.36s' }} />
        </div>
        <div style={{ marginTop: 'auto' }} />
      </div>
      <div style={{
        position: 'absolute', bottom: 36, left: 0, right: 0,
        textAlign: 'center', zIndex: 2,
      }} className="sf-overline">~6 seconds</div>
    </div>
  );
}

// ── Result (hero) ──────────────────────────────────────────────
function ScreenResult({ fadeIn = true }) {
  const text = `the morning meets you as it always has —\nwithout asking what you have made of yourself,\nwithout counting what is undone.\n\nlet the small light through the window\nbe enough for now. let your hands rest\nwhere they are. let the day arrive\non its own slow feet, and find you\nalready here, already breathing,\nalready loved by something\nyou do not have to name.`;
  return (
    <div className="sf-paper sf-vignette" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      position: 'relative', overflow: 'hidden',
      background: 'linear-gradient(180deg, #F6EBD6 0%, var(--sf-bg) 50%)',
    }}>
      {/* Top bar */}
      <div style={{
        position: 'relative', zIndex: 3,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '8px 14px',
      }}>
        <button style={{
          width: 40, height: 40, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-ink-2)',
        }}>
          <SFIcon name="close" size={20} />
        </button>
        <RecipientBadge>TO UNIVERSE</RecipientBadge>
        <button style={{
          width: 40, height: 40, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-ink-2)',
        }}>
          <SFIcon name="menu" size={20} />
        </button>
      </div>

      {/* Prayer body */}
      <div style={{
        position: 'relative', zIndex: 2,
        flex: 1, display: 'flex', flexDirection: 'column',
        padding: '12px 28px 0', overflow: 'auto',
      }}>
        <Asterism size={10} style={{ margin: '18px 0 24px' }} />

        {/* Drop cap + prayer */}
        <div style={{ position: 'relative' }}>
          <div style={{
            position: 'absolute', left: -4, top: -10,
            fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
            fontSize: 84, lineHeight: 0.85, fontWeight: 400,
            color: 'var(--sf-tertiary)',
            letterSpacing: '-0.04em',
          }}>t</div>
          <div className="sf-prayer-body" style={{
            paddingLeft: 56, fontSize: 21, lineHeight: 1.62,
            color: 'var(--sf-primary-ink)',
          }}>
            {fadeIn
              ? <FadeInProse text={`he morning meets you as it always has —\nwithout asking what you have made of yourself,\nwithout counting what is undone.\n\nlet the small light through the window\nbe enough for now. let your hands rest\nwhere they are. let the day arrive\non its own slow feet, and find you\nalready here, already breathing,\nalready loved by something\nyou do not have to name.`} perWord={55} />
              : <span>he morning meets you as it always has —{'\n'}without asking what you have made of yourself,{'\n'}without counting what is undone.</span>}
          </div>
        </div>

        <div style={{ height: 36 }} />
        <Asterism size={9} style={{ alignSelf: 'center' }} />

        <div style={{ height: 24 }} />

        {/* Metadata footer */}
        <div className="sf-overline" style={{
          textAlign: 'center', fontSize: 10, lineHeight: 1.8,
          color: 'var(--sf-ink-3)',
        }}>
          Tone · Gentle&nbsp;&nbsp;·&nbsp;&nbsp;Need · Peace, Clarity<br/>
          Length · Medium
        </div>

        <div style={{ flex: 1 }} />
      </div>

      {/* Action row */}
      <div style={{
        position: 'relative', zIndex: 3,
        padding: '12px 18px 6px',
        display: 'flex', justifyContent: 'space-around',
        opacity: 0.92,
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
            gap: 6, cursor: 'pointer', padding: '4px 8px',
          }}>
            <div style={{
              width: 44, height: 44, borderRadius: '50%',
              background: 'var(--sf-surface)',
              border: '1px solid var(--sf-outline-soft)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: 'var(--sf-primary-ink)',
            }}>
              <SFIcon name={a.icon} size={18} />
            </div>
            <span style={{
              fontFamily: 'var(--sf-sans)', fontSize: 10.5,
              fontWeight: 500, color: 'var(--sf-ink-2)',
              letterSpacing: 0.2,
            }}>{a.label}</span>
          </div>
        ))}
      </div>

      <div style={{ padding: '8px 24px 16px', position: 'relative', zIndex: 3 }}>
        <PrimaryButton style={{
          background: 'transparent', color: 'var(--sf-primary-ink)',
          border: '1px solid var(--sf-outline)', height: 50,
        }}>Done</PrimaryButton>
      </div>
    </div>
  );
}

// ── Library ────────────────────────────────────────────────────
function ScreenLibrary() {
  const filters = ['All', 'Favorites', 'Prayer', 'Intention', 'Gratitude', 'Healing', 'Reflection'];
  const items = [
    { recipient: 'TO UNIVERSE',     preview: 'May the work of my hands today rise from a settled place. Let me move with care and without hurry.', date: 'TODAY · MORNING',     fav: true,  accent: 'var(--sf-primary)' },
    { recipient: 'TO HIGHER SELF',  preview: 'I return to what is already steady within me. The breath beneath the noise, the quiet beneath the wanting.', date: 'YESTERDAY · EVENING', fav: false, accent: 'var(--sf-secondary)' },
    { recipient: 'TO THE SACRED',   preview: 'For the ones I love who are not here this morning — let them be held in some larger arms than mine.', date: '3 DAYS AGO',           fav: true,  accent: 'var(--sf-tertiary)' },
    { recipient: 'TO NATURE',       preview: 'The garden does not hurry. Neither does the river. Teach me, slowly, what the river already knows.', date: 'MAY 17',                fav: false, accent: 'var(--sf-secondary)' },
    { recipient: 'TO ANCESTORS',    preview: 'I light this small candle for the ones whose names I carry and whose faces I never knew.', date: 'MAY 14',                fav: true,  accent: 'var(--sf-tertiary)' },
  ];
  return (
    <div className="sf-paper" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '18px 24px 10px', flexShrink: 0,
      }}>
        <div>
          <div className="sf-overline">your collection</div>
          <div className="sf-display" style={{ fontSize: 34, lineHeight: 1, marginTop: 6, color: 'var(--sf-primary-ink)' }}>
            Library
          </div>
        </div>
        <button style={{
          width: 42, height: 42, borderRadius: '50%',
          background: 'var(--sf-surface)',
          border: '1px solid var(--sf-outline-soft)',
          cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-ink-2)',
        }}>
          <SFIcon name="search" size={18} />
        </button>
      </div>

      <div className="sf-overline" style={{ padding: '0 24px 8px', fontSize: 10 }}>
        24 reflections · 8 favorites
      </div>

      {/* filter chips */}
      <div style={{
        display: 'flex', gap: 8, overflowX: 'auto',
        padding: '10px 24px 16px', flexShrink: 0,
      }}>
        {filters.map((f, i) => (
          <Chip key={f} dense selected={i === 0}>{f}</Chip>
        ))}
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '0 24px 20px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        {items.map((it, i) => <PrayerCard key={i} wide {...it} />)}
      </div>

      <BottomNav active="library" />
    </div>
  );
}

// ── Prayer Detail ──────────────────────────────────────────────
function ScreenDetail() {
  return (
    <div className="sf-paper sf-vignette" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      position: 'relative', overflow: 'hidden',
    }}>
      {/* top bar */}
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '8px 14px', flexShrink: 0,
      }}>
        <button style={{
          width: 40, height: 40, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-ink-2)',
        }}>
          <SFIcon name="back" size={22} />
        </button>
        <RecipientBadge>TO DIVINE ENERGY</RecipientBadge>
        <button style={{
          width: 40, height: 40, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-ink-2)',
        }}>
          <SFIcon name="trash" size={19} />
        </button>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '4px 28px 0' }}>
        <div className="sf-overline" style={{ textAlign: 'center', marginTop: 6, fontSize: 10 }}>
          May 17 · 7:42 am
        </div>

        <Asterism size={9} style={{ margin: '20px 0 22px' }} />

        {/* prayer with drop cap */}
        <div style={{ position: 'relative' }}>
          <div style={{
            position: 'absolute', left: -4, top: -10,
            fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
            fontSize: 76, lineHeight: 0.85, fontWeight: 400,
            color: 'var(--sf-tertiary)',
          }}>i</div>
          <div className="sf-prayer-body" style={{
            paddingLeft: 44, fontSize: 20, lineHeight: 1.6,
            color: 'var(--sf-primary-ink)',
          }}>
            return to what is already steady within me —{'\n'}
            the breath beneath the noise,{'\n'}
            the quiet beneath the wanting.{'\n'}{'\n'}
            i need not arrive anywhere{'\n'}
            i am not already standing.
          </div>
        </div>

        <Asterism size={9} style={{ margin: '28px 0 20px' }} />

        {/* metadata chips */}
        <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: 6, marginBottom: 28 }}>
          <Chip dense>Reflection</Chip>
          <Chip dense>Grounded</Chip>
          <Chip dense>Peace</Chip>
          <Chip dense>Clarity</Chip>
        </div>

        {/* private note */}
        <div style={{
          background: 'var(--sf-surface-2)',
          border: '1px solid var(--sf-outline-soft)',
          borderRadius: 'var(--sf-r-md)',
          padding: '16px 18px', marginBottom: 16,
        }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <SFIcon name="note" size={14} color="var(--sf-primary)" />
              <SectionHeader>Private note</SectionHeader>
            </div>
            <SFIcon name="edit" size={15} color="var(--sf-ink-3)" />
          </div>
          <div style={{
            fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
            fontSize: 15, lineHeight: 1.5, color: 'var(--sf-ink-2)',
          }}>
            Read this twice the morning before the surgery. It held.
          </div>
        </div>
      </div>

      {/* action row */}
      <div style={{
        padding: '12px 18px 16px',
        display: 'flex', justifyContent: 'space-around',
        borderTop: '1px solid var(--sf-outline-soft)',
        background: 'var(--sf-bg)',
        flexShrink: 0,
      }}>
        {[
          { icon: 'heart-fill', label: 'Favorited', color: 'var(--sf-tertiary)' },
          { icon: 'mic',        label: 'Practice' },
          { icon: 'share',      label: 'Share' },
          { icon: 'copy',       label: 'Copy' },
        ].map(a => (
          <div key={a.label} style={{
            display: 'flex', flexDirection: 'column', alignItems: 'center',
            gap: 5, cursor: 'pointer', padding: '6px 8px',
          }}>
            <SFIcon name={a.icon} size={22} color={a.color || 'var(--sf-primary-ink)'} />
            <span style={{
              fontFamily: 'var(--sf-sans)', fontSize: 10.5,
              fontWeight: 500, color: a.color || 'var(--sf-ink-2)',
              letterSpacing: 0.2,
            }}>{a.label}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

Object.assign(window, {
  ScreenHome, ScreenCreate, ScreenLoading, ScreenResult,
  ScreenLibrary, ScreenDetail,
});
