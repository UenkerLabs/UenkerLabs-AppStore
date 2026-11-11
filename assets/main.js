'use strict';
const year=document.getElementById('year');
if(year)year.textContent=new Date().getFullYear();
const copyButton=document.getElementById('copy-repo');
if(copyButton){
  copyButton.addEventListener('click',async()=>{
    const target=copyButton.dataset.copy;
    const feedback=document.getElementById('copy-feedback');
    try {
      if(!navigator.clipboard?.writeText)throw new Error('Clipboard API nicht verfügbar');
      await navigator.clipboard.writeText(target);
      if(feedback)feedback.textContent='Adresse kopiert.';
    }catch(_error){
      if(feedback)feedback.textContent='Kopieren nicht möglich. Bitte die Adresse oben markieren und manuell kopieren.';
      document.getElementById('repo-url')?.setAttribute('tabindex','0');
      document.getElementById('repo-url')?.focus();
    }
  });
}
