// macOS Terminal CV unlock (SHA-256 hash only)
const PASSWORD_HASH = "52659753d12ea7209687fe5cfeb8c4fa496af871acf621e708c1aa6782bfa5ce"; // not reversible
const CV_PATH = "assets/Henrique.Teixeira_CV_2025.pdf";             // your real CV path

async function sha256(message) {
  const encoder = new TextEncoder();
  const data = encoder.encode(message);
  const hashBuffer = await crypto.subtle.digest('SHA-256', data);
  return Array.from(new Uint8Array(hashBuffer)).map(b => b.toString(16).padStart(2, '0')).join('');
}

const cvLink   = document.getElementById('cvLink');
const dockCv   = document.getElementById('dockCv');
const overlay  = document.getElementById('terminalOverlay');
const pwdInput = document.getElementById('cvPwdInput');
const btnCancel= document.getElementById('termCancel');
const btnUnlock= document.getElementById('termUnlock');
const feedback = document.getElementById('termFeedback');

function openTerminal() { feedback.textContent=''; pwdInput.value=''; overlay.style.display='flex'; setTimeout(()=>pwdInput.focus(), 40); }
function closeTerminal() { overlay.style.display='none'; }

cvLink.addEventListener('click', function(e){ e.preventDefault(); openTerminal(); });
if(dockCv) dockCv.addEventListener('click', function(e){ e.preventDefault(); openTerminal(); });
btnCancel.addEventListener('click', closeTerminal);

async function tryUnlock() {
  const typed = (pwdInput.value||'').trim();
  const digest = await sha256(typed);
  if (digest === PASSWORD_HASH) {
    const token = Math.random().toString(36).slice(2);
    window.open(CV_PATH + '?t=' + token, '_blank', 'noopener,noreferrer');
    closeTerminal();
  } else {
    feedback.textContent = 'Incorrect password';
    pwdInput.value = '';
  }
}

btnUnlock.addEventListener('click', tryUnlock);
pwdInput.addEventListener('keydown', function(ev){ if(ev.key==='Enter') tryUnlock(); });
