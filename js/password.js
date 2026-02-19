// ================================
//  CV Password Gate (Hashed) – macOS Terminal popup
//  - Hides the real password by comparing SHA-256 hash
//  - Opens CV only if hash matches
// ================================

// SHA-256 hash for the password "henriquedevops123"
const PASSWORD_HASH = "a54d47e565097dae1fa1377fb4df3815f8fc318ba70638b22e377209e31cce9e";
// Path to your sanitized CV PDF (adjust if you keep a different name)
const CV_PATH = "assets/CV_henrique_2025.pdf";

// Hash helper using Web Crypto API
async function sha256(message) {
  const enc = new TextEncoder();
  const data = enc.encode(message);
  const hashBuffer = await crypto.subtle.digest('SHA-256', data);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

// Elements
const cvLink = document.getElementById('cvLink');
const overlay = document.getElementById('terminalOverlay');
const pwdInput = document.getElementById('cvPwdInput');
const bCancel = document.getElementById('termCancel');
const bUnlock = document.getElementById('termUnlock');
const feedback = document.getElementById('termFeedback');

// Show the macOS-like terminal popup
cvLink.addEventListener('click', (e) => {
  e.preventDefault();
  feedback.textContent = '';
  pwdInput.value = '';
  overlay.style.display = 'flex';
  setTimeout(() => pwdInput.focus(), 30);
});

// Close popup
bCancel.addEventListener('click', () => {
  overlay.style.display = 'none';
});

// Validate password
async function tryUnlock() {
  const typed = (pwdInput.value || '').trim();
  const hash = await sha256(typed);
  if (hash === PASSWORD_HASH) {
    const token = Math.random().toString(36).slice(2);
    window.open(`${CV_PATH}?t=${token}`, '_blank', 'noopener,noreferrer');
    overlay.style.display = 'none';
  } else {
    feedback.textContent = 'Incorrect password';
    pwdInput.value = '';
  }
}

bUnlock.addEventListener('click', tryUnlock);
pwdInput.addEventListener('keydown', (ev) => { if (ev.key === 'Enter') tryUnlock(); });
