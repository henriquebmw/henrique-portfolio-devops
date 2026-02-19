
const PASSWORD_HASH = '52659753d12ea7209687fe5cfeb8c4fa496af871acf621e708c1aa6782bfa5ce';
const CV_PATH = 'assets/CV_henrique_2025.pdf';
async function sha256(msg){const m=new TextEncoder().encode(msg);const h=await crypto.subtle.digest('SHA-256',m);return Array.from(new Uint8Array(h)).map(b=>b.toString(16).padStart(2,'0')).join('');}
const link=document.getElementById('cvLink');const ov=document.getElementById('terminalOverlay');const inp=document.getElementById('cvPwdInput');const fb=document.getElementById('termFeedback');document.getElementById('termCancel').onclick=()=>ov.style.display='none';
document.getElementById('termUnlock').onclick=unlock;link.onclick=e=>{e.preventDefault();fb.textContent='';inp.value='';ov.style.display='flex';setTimeout(()=>inp.focus(),50);};
async function unlock(){const typed=inp.value.trim();if(await sha256(typed)===PASSWORD_HASH){window.open(CV_PATH,'_blank');ov.style.display='none';}else{fb.textContent='Incorrect password';inp.value='';}}
