// SF Icons — hairline stroke set, custom drawn.
// All icons are 24x24 viewBox, currentColor strokes, 1.5px width.

function SFIcon({ name, size = 22, color, style, strokeWidth = 1.5 }) {
  const props = {
    width: size, height: size, viewBox: '0 0 24 24',
    fill: 'none', stroke: color || 'currentColor',
    strokeWidth, strokeLinecap: 'round', strokeLinejoin: 'round',
    style,
  };
  switch (name) {
    case 'back': return (
      <svg {...props}><path d="M15 5L8 12l7 7"/></svg>
    );
    case 'close': return (
      <svg {...props}><path d="M6 6l12 12M18 6L6 18"/></svg>
    );
    case 'search': return (
      <svg {...props}><circle cx="11" cy="11" r="6.5"/><path d="M16 16l4 4"/></svg>
    );
    case 'heart': return (
      <svg {...props}><path d="M12 20s-7-4.5-9-9c-1.3-3 .8-6 3.8-6 1.9 0 3.5 1 4.2 2.5C11.7 6 13.3 5 15.2 5c3 0 5.1 3 3.8 6-2 4.5-7 9-7 9z"/></svg>
    );
    case 'heart-fill': return (
      <svg {...props} fill={color || 'currentColor'} stroke="none"><path d="M12 20s-7-4.5-9-9c-1.3-3 .8-6 3.8-6 1.9 0 3.5 1 4.2 2.5C11.7 6 13.3 5 15.2 5c3 0 5.1 3 3.8 6-2 4.5-7 9-7 9z"/></svg>
    );
    case 'home': return (
      <svg {...props}><path d="M4 11l8-7 8 7v9a1 1 0 01-1 1h-4v-7h-6v7H5a1 1 0 01-1-1v-9z"/></svg>
    );
    case 'library': return (
      <svg {...props}><path d="M5 4h3v16H5zM10 4h3v16h-3zM15 5l4 .8L17 21l-4-.8z"/></svg>
    );
    case 'bell': return (
      <svg {...props}><path d="M6 16V11a6 6 0 1112 0v5l1.5 2h-15z"/><path d="M10 19a2 2 0 004 0"/></svg>
    );
    case 'settings': return (
      <svg {...props}><circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/></svg>
    );
    case 'plus': return (
      <svg {...props}><path d="M12 5v14M5 12h14"/></svg>
    );
    case 'check': return (
      <svg {...props}><path d="M5 12.5l4.5 4.5L19 7"/></svg>
    );
    case 'check-soft': return (
      <svg {...props} strokeWidth={1.2}><path d="M5 12.5l4.5 4.5L19 7"/></svg>
    );
    case 'arrow-right': return (
      <svg {...props}><path d="M5 12h14M13 6l6 6-6 6"/></svg>
    );
    case 'chevron-right': return (
      <svg {...props}><path d="M9 6l6 6-6 6"/></svg>
    );
    case 'chevron-down': return (
      <svg {...props}><path d="M6 9l6 6 6-6"/></svg>
    );
    case 'edit': return (
      <svg {...props}><path d="M14.5 5.5l4 4L9 19l-5 1 1-5 9.5-9.5z"/></svg>
    );
    case 'trash': return (
      <svg {...props}><path d="M4 7h16M9 7V5a1 1 0 011-1h4a1 1 0 011 1v2M6 7l1 12a1 1 0 001 1h8a1 1 0 001-1l1-12"/></svg>
    );
    case 'share': return (
      <svg {...props}><path d="M12 4v12M8 8l4-4 4 4M5 13v5a2 2 0 002 2h10a2 2 0 002-2v-5"/></svg>
    );
    case 'copy': return (
      <svg {...props}><rect x="8" y="8" width="12" height="12" rx="2"/><path d="M16 8V5a1 1 0 00-1-1H5a1 1 0 00-1 1v10a1 1 0 001 1h3"/></svg>
    );
    case 'play': return (
      <svg {...props}><path d="M7 5l12 7-12 7V5z" fill="currentColor"/></svg>
    );
    case 'pause': return (
      <svg {...props}><rect x="6.5" y="5" width="4" height="14" rx="1" fill="currentColor" stroke="none"/><rect x="13.5" y="5" width="4" height="14" rx="1" fill="currentColor" stroke="none"/></svg>
    );
    case 'skip-next': return (
      <svg {...props}><path d="M6 5l9 7-9 7V5z" fill="currentColor"/><path d="M18 5v14"/></svg>
    );
    case 'restart': return (
      <svg {...props}><path d="M4 12a8 8 0 108-8v3"/><path d="M12 7L9 4l3-3"/></svg>
    );
    case 'regenerate': return (
      <svg {...props}><path d="M3 12a9 9 0 0115.5-6L21 8"/><path d="M21 3v5h-5"/><path d="M21 12a9 9 0 01-15.5 6L3 16"/><path d="M3 21v-5h5"/></svg>
    );
    case 'bookmark': return (
      <svg {...props}><path d="M6 4h12v17l-6-4-6 4V4z"/></svg>
    );
    case 'bookmark-fill': return (
      <svg {...props} fill={color || 'currentColor'} stroke="currentColor"><path d="M6 4h12v17l-6-4-6 4V4z"/></svg>
    );
    case 'cloud-off': return (
      <svg {...props}><path d="M3 3l18 18"/><path d="M7 18a5 5 0 01-.5-9.9"/><path d="M10 6a5 5 0 019 4 4 4 0 011 7.7"/></svg>
    );
    case 'sparkle': return (
      <svg {...props}><path d="M12 3v6M12 15v6M3 12h6M15 12h6"/></svg>
    );
    case 'leaf': return (
      <svg {...props}><path d="M4 20c0-9 7-16 16-16 0 9-7 16-16 16z"/><path d="M4 20c4-4 8-8 16-16"/></svg>
    );
    case 'phone': return (
      <svg {...props}><path d="M5 5c0 8 6 14 14 14l1.5-3-4-2-2 2c-2-1-4-3-5-5l2-2-2-4L5 5z"/></svg>
    );
    case 'message': return (
      <svg {...props}><path d="M4 5h16v11H8l-4 4V5z"/></svg>
    );
    case 'external': return (
      <svg {...props}><path d="M14 4h6v6"/><path d="M20 4l-9 9"/><path d="M18 14v5a1 1 0 01-1 1H5a1 1 0 01-1-1V7a1 1 0 011-1h5"/></svg>
    );
    case 'menu': return (
      <svg {...props}><circle cx="6" cy="12" r="1.2" fill="currentColor"/><circle cx="12" cy="12" r="1.2" fill="currentColor"/><circle cx="18" cy="12" r="1.2" fill="currentColor"/></svg>
    );
    case 'lock': return (
      <svg {...props}><rect x="5" y="10" width="14" height="10" rx="2"/><path d="M8 10V7a4 4 0 018 0v3"/></svg>
    );
    case 'eye': return (
      <svg {...props}><path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/></svg>
    );
    case 'mic': return (
      <svg {...props}><rect x="9" y="3" width="6" height="12" rx="3"/><path d="M5 11a7 7 0 0014 0M12 18v3"/></svg>
    );
    case 'sun': return (
      <svg {...props}><circle cx="12" cy="12" r="4"/><path d="M12 3v2M12 19v2M3 12h2M19 12h2M5.6 5.6l1.4 1.4M17 17l1.4 1.4M5.6 18.4L7 17M17 7l1.4-1.4"/></svg>
    );
    case 'moon': return (
      <svg {...props}><path d="M20 14A8 8 0 119 3a7 7 0 0011 11z"/></svg>
    );
    case 'help': return (
      <svg {...props}><circle cx="12" cy="12" r="9"/><path d="M9.5 9.5a2.5 2.5 0 015 0c0 1.5-2.5 2-2.5 3.5"/><circle cx="12" cy="17" r="0.8" fill="currentColor"/></svg>
    );
    case 'shield': return (
      <svg {...props}><path d="M12 3l8 3v6c0 5-3.5 8.5-8 9-4.5-.5-8-4-8-9V6l8-3z"/></svg>
    );
    case 'asterism': return (
      <svg {...props} viewBox="0 0 24 24"><circle cx="12" cy="6" r="0.9" fill="currentColor" stroke="none"/><circle cx="7" cy="15" r="0.9" fill="currentColor" stroke="none"/><circle cx="17" cy="15" r="0.9" fill="currentColor" stroke="none"/></svg>
    );
    case 'note': return (
      <svg {...props}><path d="M5 4h10l4 4v12H5z"/><path d="M15 4v4h4"/></svg>
    );
    default: return null;
  }
}

Object.assign(window, { SFIcon });
