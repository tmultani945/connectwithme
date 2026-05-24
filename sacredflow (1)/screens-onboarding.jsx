// SF Onboarding screens — Splash + 6 onboarding steps
// Each screen function returns the inside of an SFFrame.

// ── 1. Splash ──────────────────────────────────────────────────
function ScreenSplash() {
  return (
    <div className="sf-paper sf-vignette" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      alignItems: 'center', justifyContent: 'center',
      position: 'relative', overflow: 'hidden',
      background: 'linear-gradient(180deg, #F6EAD3 0%, var(--sf-bg) 80%)',
    }}>
      <div style={{ position: 'relative', zIndex: 2, textAlign: 'center' }}>
        <Asterism size={14} style={{ marginBottom: 32 }} />
        <div style={{
          fontFamily: 'var(--sf-serif)', fontSize: 48, fontWeight: 400,
          fontStyle: 'italic', color: 'var(--sf-primary-ink)',
          letterSpacing: '-0.01em', lineHeight: 1, marginBottom: 14,
        }}>Sacred Flow</div>
        <div className="sf-overline" style={{ fontSize: 10 }}>a quiet space</div>
      </div>
    </div>
  );
}

// ── 2. Welcome ─────────────────────────────────────────────────
function ScreenWelcome() {
  return (
    <div className="sf-paper sf-vignette" style={{
      flex: 1, display: 'flex', flexDirection: 'column',
      padding: '20px 28px 32px',
      position: 'relative', overflow: 'hidden',
      background: 'linear-gradient(180deg, #F4D9C0 0%, var(--sf-bg) 60%)',
    }}>
      <div style={{ position: 'relative', zIndex: 2, flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div style={{ height: 60 }} />
        <Asterism size={12} style={{ alignSelf: 'flex-start', marginBottom: 36 }} />
        <div className="sf-display" style={{
          fontSize: 44, marginBottom: 28, color: 'var(--sf-primary-ink)',
        }}>
          A quiet space<br/>
          for what matters<br/>
          to you.
        </div>
        <div className="sf-body" style={{
          fontSize: 16, lineHeight: 1.55, color: 'var(--sf-ink-2)',
          maxWidth: 300, marginBottom: 'auto',
        }}>
          Sacred Flow helps you put words to your prayers,
          intentions, and reflections — in whatever spiritual
          language is yours.
        </div>
        <PrimaryButton icon="arrow-right">Begin</PrimaryButton>
        <div style={{
          marginTop: 16, textAlign: 'center',
        }} className="sf-overline">no account · no ads · private</div>
      </div>
    </div>
  );
}

// ── Onboarding chrome (progress + back) ────────────────────────
function OnboardingHeader({ step, total = 6, onBack, label }) {
  return (
    <div style={{ padding: '14px 20px 8px', flexShrink: 0 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <button onClick={onBack} style={{
          width: 36, height: 36, borderRadius: '50%',
          background: 'transparent', border: 'none', cursor: 'pointer',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--sf-ink)', marginLeft: -8,
        }}>
          <SFIcon name="back" size={20} />
        </button>
        <div className="sf-overline" style={{ fontSize: 10 }}>
          {label} · {step} of {total}
        </div>
        <div style={{ width: 36 }} />
      </div>
      {/* progress hairline */}
      <div style={{ display: 'flex', gap: 4, marginTop: 14, paddingLeft: 4, paddingRight: 4 }}>
        {Array.from({ length: total }).map((_, i) => (
          <div key={i} style={{
            flex: 1, height: 2, borderRadius: 2,
            background: i < step ? 'var(--sf-primary-ink)' : 'var(--sf-outline-soft)',
          }} />
        ))}
      </div>
    </div>
  );
}

// ── 3. Use Case ────────────────────────────────────────────────
function ScreenUseCase() {
  const cases = [
    { name: 'Prayer',     desc: 'A reverent address' },
    { name: 'Intention',  desc: 'Setting a direction' },
    { name: 'Gratitude',  desc: 'Thanks for what is here' },
    { name: 'Healing',    desc: 'Words for what hurts' },
    { name: 'Reflection', desc: 'Pause and notice' },
    { name: 'Custom',     desc: 'Your own way' },
  ];
  const selected = 'Reflection';
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <OnboardingHeader step={1} label="Begin" />
      <div style={{ padding: '20px 28px 0', flex: 1, overflow: 'auto' }}>
        <div className="sf-headline" style={{ fontSize: 30, fontStyle: 'italic', marginBottom: 8 }}>
          What brings you<br/>here today?
        </div>
        <div className="sf-body" style={{ marginBottom: 28 }}>
          You can change this anytime.
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {cases.map(c => {
            const on = c.name === selected;
            return (
              <div key={c.name} style={{
                padding: '18px 16px', minHeight: 92,
                borderRadius: 'var(--sf-r-md)',
                background: on ? 'var(--sf-primary-soft)' : 'var(--sf-surface)',
                border: `1px solid ${on ? 'var(--sf-primary)' : 'var(--sf-outline-soft)'}`,
                boxShadow: on ? 'var(--sf-shadow-1)' : 'none',
                position: 'relative',
              }}>
                <div style={{
                  fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
                  fontSize: 22, color: 'var(--sf-ink)', marginBottom: 4,
                }}>{c.name}</div>
                <div className="sf-body" style={{ fontSize: 12.5 }}>{c.desc}</div>
                {on && (
                  <div style={{
                    position: 'absolute', top: 12, right: 12,
                    width: 18, height: 18, borderRadius: '50%',
                    background: 'var(--sf-primary)', color: '#fff',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    <SFIcon name="check" size={11} strokeWidth={2.2} />
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
      <div style={{ padding: '12px 28px 24px' }}>
        <PrimaryButton icon="arrow-right">Continue</PrimaryButton>
      </div>
    </div>
  );
}

// ── 4. Recipient ───────────────────────────────────────────────
function ScreenRecipient() {
  const opts = ['God', 'Universe', 'Nature', 'Higher Self', 'Divine Energy', 'Ancestors', 'The Sacred'];
  const selected = 'Universe';
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <OnboardingHeader step={2} label="Begin" />
      <div style={{ padding: '20px 28px 0', flex: 1, overflow: 'auto' }}>
        <div className="sf-headline" style={{ fontSize: 30, fontStyle: 'italic', marginBottom: 8 }}>
          Who or what do<br/>you address?
        </div>
        <div className="sf-body" style={{ marginBottom: 28 }}>
          This is just for you. There's no wrong answer.
        </div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 24 }}>
          {opts.map(o => (
            <Chip key={o} selected={o === selected}>{o}</Chip>
          ))}
          <Chip>
            <SFIcon name="plus" size={13} style={{ marginRight: 2 }} />
            Custom…
          </Chip>
        </div>
        <Rule ornament style={{ margin: '16px 0' }} />
        <div className="sf-body" style={{
          fontStyle: 'italic', fontSize: 13, textAlign: 'center',
          color: 'var(--sf-ink-3)', padding: '0 20px', fontFamily: 'var(--sf-serif)',
        }}>
          "The name does not change what is named."
        </div>
      </div>
      <div style={{ padding: '12px 28px 24px' }}>
        <PrimaryButton icon="arrow-right">Continue</PrimaryButton>
      </div>
    </div>
  );
}

// ── 5. Need ────────────────────────────────────────────────────
function ScreenNeed() {
  const needs = ['Peace', 'Healing', 'Clarity', 'Gratitude', 'Strength', 'Hope', 'Success', 'Abundance', 'Forgiveness', 'Protection', 'Joy', 'Courage'];
  const selected = ['Peace', 'Clarity'];
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <OnboardingHeader step={3} label="Begin" />
      <div style={{ padding: '20px 28px 0', flex: 1, overflow: 'auto' }}>
        <div className="sf-headline" style={{ fontSize: 30, fontStyle: 'italic', marginBottom: 8 }}>
          What would you<br/>like to bring in?
        </div>
        <div className="sf-body" style={{ marginBottom: 6 }}>
          Pick one or two.
        </div>
        <div className="sf-overline" style={{ marginBottom: 22, fontSize: 10 }}>
          {selected.length} of 2 selected
        </div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
          {needs.map(n => (
            <Chip key={n} selected={selected.includes(n)}
              disabled={!selected.includes(n) && selected.length >= 2}>
              {n}
            </Chip>
          ))}
        </div>
      </div>
      <div style={{ padding: '12px 28px 24px' }}>
        <PrimaryButton icon="arrow-right">Continue</PrimaryButton>
      </div>
    </div>
  );
}

// ── 6. Tone ────────────────────────────────────────────────────
function ScreenTone() {
  const tones = [
    { name: 'Gentle',       descriptor: 'soft and tender',     sample: 'May the morning meet you kindly.' },
    { name: 'Hopeful',      descriptor: 'open and forward',    sample: 'Something good is on its way.' },
    { name: 'Thankful',     descriptor: 'rooted in gratitude', sample: 'For this breath, thank you.' },
    { name: 'Grounded',     descriptor: 'steady and clear',    sample: 'My feet are on the floor. I am here.' },
    { name: 'Powerful',     descriptor: 'firm and rising',     sample: 'I will not be smaller than I am.' },
    { name: 'Surrendering', descriptor: 'releasing the hold',  sample: 'I let go of what is not mine to carry.' },
  ];
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <OnboardingHeader step={4} label="Begin" />
      <div style={{ padding: '20px 0 0' }}>
        <div style={{ padding: '0 28px' }}>
          <div className="sf-headline" style={{ fontSize: 30, fontStyle: 'italic', marginBottom: 8 }}>
            How would you<br/>like it to feel?
          </div>
          <div className="sf-body" style={{ marginBottom: 24 }}>
            The mood of your reflection.
          </div>
        </div>
        <div style={{
          display: 'flex', gap: 14, overflowX: 'auto',
          padding: '0 28px 8px', scrollSnapType: 'x mandatory',
        }}>
          {tones.map((t, i) => (
            <div key={t.name} style={{ scrollSnapAlign: 'start' }}>
              <ToneCard {...t} selected={i === 0} compact />
            </div>
          ))}
        </div>
        <div style={{
          marginTop: 16, display: 'flex', justifyContent: 'center', gap: 6,
        }}>
          {tones.map((_, i) => (
            <div key={i} style={{
              width: i === 0 ? 16 : 5, height: 5, borderRadius: 3,
              background: i === 0 ? 'var(--sf-primary-ink)' : 'var(--sf-outline-soft)',
              transition: 'all 240ms',
            }} />
          ))}
        </div>
      </div>
      <div style={{ flex: 1 }} />
      <div style={{ padding: '12px 28px 24px' }}>
        <PrimaryButton icon="arrow-right">Continue</PrimaryButton>
      </div>
    </div>
  );
}

// ── 7. Personal Context ────────────────────────────────────────
function ScreenContext() {
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
      <OnboardingHeader step={5} label="Begin" />
      <div style={{ padding: '20px 28px 0', flex: 1, overflow: 'auto' }}>
        <div className="sf-headline" style={{ fontSize: 30, fontStyle: 'italic', marginBottom: 8 }}>
          Anything specific<br/>on your mind?
        </div>
        <div className="sf-body" style={{ marginBottom: 22 }}>
          Optional. A few words help personalize what you receive.
        </div>
        <TextField
          multiline
          rows={5}
          value={"Starting a new job tomorrow. Grateful and a little anxious."}
          maxLength={300}
          charCounter
          helper="This stays on your device unless you choose to save it."
        />
        <Rule ornament style={{ margin: '28px 0 20px' }} />
        <div className="sf-overline" style={{ marginBottom: 14, textAlign: 'center' }}>
          or try one of these
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          {[
            'Missing my grandmother…',
            'Healing after surgery…',
            'A difficult conversation ahead…',
          ].map(t => (
            <div key={t} style={{
              padding: '12px 16px', borderRadius: 'var(--sf-r-md)',
              background: 'var(--sf-surface)',
              border: '1px dashed var(--sf-outline-soft)',
              fontFamily: 'var(--sf-serif)', fontStyle: 'italic',
              fontSize: 14, color: 'var(--sf-ink-2)',
            }}>{t}</div>
          ))}
        </div>
      </div>
      <div style={{ padding: '12px 28px 24px', display: 'flex', flexDirection: 'column', gap: 4 }}>
        <PrimaryButton>Create my first reflection</PrimaryButton>
        <TextButton style={{ alignSelf: 'center', color: 'var(--sf-ink-3)' }}>Skip for now</TextButton>
      </div>
    </div>
  );
}

Object.assign(window, {
  ScreenSplash, ScreenWelcome, ScreenUseCase, ScreenRecipient,
  ScreenNeed, ScreenTone, ScreenContext, OnboardingHeader,
});
