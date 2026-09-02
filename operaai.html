 
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Human CLI — Orchestrator Workspace</title>
  <meta name="description" content="Human CLI — dashboard orchestrator AI untuk repository, development, deployment, VPS, security audit, dan workspace agent swarm." />
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;700&family=IBM+Plex+Mono:wght@400;500;600&display=swap" rel="stylesheet">
  <style>
    :root{
      --bg:#081018;
      --bg-2:#0d1722;
      --panel:#0f1c29cc;
      --panel-solid:#0f1c29;
      --panel-2:#152638;
      --line:#22364d;
      --line-2:#2f4a68;
      --text:#ecf6ff;
      --muted:#8da6be;
      --soft:#b9c9d8;
      --accent:#88f3c8;
      --accent-2:#7dd3ff;
      --warn:#ffbd73;
      --danger:#ff8d94;
      --violet:#bca8ff;
      --ok:#7fffb0;
      --shadow:0 30px 80px rgba(0,0,0,.45);
      --radius:24px;
      --radius-sm:14px;
      --radius-xs:10px;
      --sidebar-w: 310px;
      --topbar-h: 72px;
      --glass: blur(16px) saturate(150%);
      --grid: radial-gradient(circle at 1px 1px, rgba(125,211,255,.12) 1px, transparent 0);
    }

    *{box-sizing:border-box}
    html,body{height:100%}
    html{scroll-behavior:smooth}
    body{
      margin:0;
      font-family:"Space Grotesk", sans-serif;
      color:var(--text);
      background:
        radial-gradient(1200px 600px at 10% -10%, rgba(125,211,255,.13), transparent 60%),
        radial-gradient(900px 500px at 100% 0%, rgba(136,243,200,.10), transparent 55%),
        linear-gradient(180deg, #071019 0%, #09131e 35%, #071018 100%);
      overflow:hidden;
    }

    body::before{
      content:"";
      position:fixed;
      inset:0;
      background-image:var(--grid);
      background-size:28px 28px;
      opacity:.35;
      pointer-events:none;
      mask-image:linear-gradient(to bottom, rgba(0,0,0,.8), rgba(0,0,0,.15));
    }

    .app{
      display:grid;
      grid-template-columns: var(--sidebar-w) 1fr;
      height:100vh;
      gap:0;
    }

    .sidebar{
      position:relative;
      display:flex;
      flex-direction:column;
      min-height:100vh;
      background:linear-gradient(180deg, rgba(8,16,24,.95), rgba(10,18,30,.96));
      border-right:1px solid rgba(125,211,255,.10);
      backdrop-filter:var(--glass);
      z-index:20;
    }

    .brand{
      display:flex;
      align-items:center;
      justify-content:space-between;
      padding:1rem 1rem .9rem 1rem;
      border-bottom:1px solid rgba(125,211,255,.08);
    }

    .brand-left{
      display:flex;
      gap:.85rem;
      align-items:center;
    }

    .logo{
      width:44px;
      height:44px;
      border-radius:14px;
      position:relative;
      background:
        linear-gradient(135deg, rgba(136,243,200,.2), rgba(125,211,255,.18)),
        linear-gradient(180deg, #102133, #0b1724);
      border:1px solid rgba(136,243,200,.22);
      box-shadow:inset 0 1px 0 rgba(255,255,255,.06), 0 12px 30px rgba(0,0,0,.32);
      overflow:hidden;
    }

    .logo::before,.logo::after{
      content:"";
      position:absolute;
      inset:8px;
      border-radius:10px;
      border:1px dashed rgba(136,243,200,.25);
    }
    .logo::after{
      inset:auto 10px 10px 10px;
      height:10px;
      border:none;
      background:linear-gradient(90deg, var(--accent), var(--accent-2));
      border-radius:99px;
      box-shadow:0 0 18px rgba(125,211,255,.4);
    }

    .brand h1{
      margin:0;
      font-size:1.04rem;
      line-height:1;
      letter-spacing:.02em;
    }

    .brand p{
      margin:.2rem 0 0;
      color:var(--muted);
      font-size:.76rem;
      letter-spacing:.08em;
      text-transform:uppercase;
    }

    .collapse-btn,.ghost-btn,.icon-btn{
      border:none;
      background:rgba(255,255,255,.04);
      color:var(--text);
      border:1px solid rgba(255,255,255,.08);
      border-radius:12px;
      cursor:pointer;
      transition:.24s ease;
      font-family:inherit;
    }

    .collapse-btn{
      width:40px;height:40px;
      font-size:1rem;
    }

    .collapse-btn:hover,.ghost-btn:hover,.icon-btn:hover{
      transform:translateY(-1px);
      border-color:rgba(136,243,200,.32);
      box-shadow:0 12px 26px rgba(0,0,0,.25);
      background:rgba(136,243,200,.08);
    }

    .sidebar-scroll{
      overflow:auto;
      padding:1rem;
      display:flex;
      flex-direction:column;
      gap:1rem;
    }

    .panel{
      background:linear-gradient(180deg, rgba(18,31,46,.76), rgba(12,23,36,.78));
      border:1px solid rgba(125,211,255,.10);
      border-radius:20px;
      box-shadow:var(--shadow);
      backdrop-filter:var(--glass);
    }

    .panel-header{
      padding:.9rem 1rem;
      display:flex;
      justify-content:space-between;
      align-items:center;
      border-bottom:1px solid rgba(255,255,255,.05);
    }

    .panel-header h2{
      font-size:.9rem;
      margin:0;
      letter-spacing:.06em;
      text-transform:uppercase;
      color:var(--soft);
    }

    .panel-body{padding:1rem}

    .nav-list{
      display:grid;
      gap:.55rem;
    }

    .nav-item{
      display:flex;
      align-items:center;
      gap:.8rem;
      padding:.82rem .9rem;
      border-radius:14px;
      color:var(--soft);
      cursor:pointer;
      border:1px solid transparent;
      transition:.22s ease;
      text-decoration:none;
    }
    .nav-item:hover,.nav-item.active{
      background:linear-gradient(90deg, rgba(125,211,255,.12), rgba(136,243,200,.08));
      color:var(--text);
      border-color:rgba(125,211,255,.16);
      transform:translateX(3px);
    }
    .nav-badge{
      margin-left:auto;
      font-size:.7rem;
      padding:.22rem .45rem;
      border-radius:999px;
      color:#041018;
      background:linear-gradient(90deg, var(--accent), var(--accent-2));
      font-weight:700;
    }

    .mini-stack{display:grid; gap:.7rem}
    .chip-row{display:flex; flex-wrap:wrap; gap:.5rem}
    .chip{
      display:inline-flex;
      align-items:center;
      gap:.4rem;
      padding:.45rem .7rem;
      border-radius:999px;
      font-size:.76rem;
      background:rgba(255,255,255,.04);
      border:1px solid rgba(255,255,255,.06);
      color:var(--soft);
    }
    .chip.active{
      background:linear-gradient(90deg, rgba(136,243,200,.16), rgba(125,211,255,.14));
      color:var(--text);
      border-color:rgba(136,243,200,.22);
    }

    .session-list,.bookmark-list,.provider-list{display:grid; gap:.65rem}
    .session-item,.bookmark-item,.provider-item{
      border:1px solid rgba(255,255,255,.06);
      background:rgba(255,255,255,.025);
      border-radius:16px;
      padding:.8rem;
    }
    .session-item h3,.bookmark-item h3,.provider-item h3{
      margin:0 0 .35rem;
      font-size:.88rem;
      font-weight:600;
    }
    .session-item p,.bookmark-item p,.provider-item p{
      margin:0;
      color:var(--muted);
      font-size:.76rem;
      line-height:1.45;
    }

    .provider-status{
      display:inline-flex;
      align-items:center;
      gap:.35rem;
      margin-top:.55rem;
      color:var(--ok);
      font-size:.73rem;
      font-family:"IBM Plex Mono", monospace;
    }
    .provider-status::before{
      content:"";
      width:7px;height:7px;border-radius:50%;
      background:currentColor;
      box-shadow:0 0 10px currentColor;
    }

    .main{
      display:flex;
      flex-direction:column;
      min-width:0;
      min-height:100vh;
      overflow:hidden;
    }

    .topbar{
      min-height:var(--topbar-h);
      display:flex;
      align-items:center;
      justify-content:space-between;
      gap:1rem;
      padding:1rem 1.2rem;
      border-bottom:1px solid rgba(125,211,255,.08);
      background:linear-gradient(180deg, rgba(9,18,29,.7), rgba(9,18,29,.32));
      backdrop-filter:blur(12px);
    }

    .topbar-left{
      display:flex;
      align-items:center;
      gap:.8rem;
      min-width:0;
    }

    .hamburger{
      display:none;
      width:42px;height:42px;
      border-radius:12px;
      align-items:center;
      justify-content:center;
      font-size:1.1rem;
    }

    .workspace-title{
      display:flex;
      flex-direction:column;
      min-width:0;
    }
    .workspace-title h2{
      margin:0;
      font-size:1rem;
      letter-spacing:.03em;
    }
    .workspace-title p{
      margin:.2rem 0 0;
      color:var(--muted);
      font-size:.78rem;
      white-space:nowrap;
      overflow:hidden;
      text-overflow:ellipsis;
    }

    .topbar-right{
      display:flex;
      align-items:center;
      gap:.65rem;
      flex-wrap:wrap;
      justify-content:flex-end;
    }

    .ghost-btn{
      padding:.72rem .9rem;
      font-size:.84rem;
    }

    .cta{
      border:none;
      cursor:pointer;
      border-radius:14px;
      padding:.8rem 1rem;
      font-family:inherit;
      font-weight:700;
      color:#041018;
      background:linear-gradient(90deg, var(--accent), var(--accent-2));
      box-shadow:0 14px 36px rgba(96,200,255,.16);
      transition:.22s ease;
    }
    .cta:hover{transform:translateY(-2px) scale(1.01)}
    .cta:active{transform:translateY(0) scale(.985)}

    .workspace{
      flex:1;
      overflow:auto;
      padding:1rem 1rem 1.15rem;
    }

    .grid{
      display:grid;
      grid-template-columns: 1.25fr .82fr;
      gap:1rem;
      min-height:100%;
    }

    .column{
      display:grid;
      gap:1rem;
      align-content:start;
      min-width:0;
    }

    .hero{
      padding:1.15rem;
      border-radius:28px;
      position:relative;
      overflow:hidden;
      background:
        radial-gradient(circle at 82% 18%, rgba(125,211,255,.18), transparent 22%),
        linear-gradient(135deg, rgba(18,31,46,.96), rgba(9,17,27,.92));
      border:1px solid rgba(125,211,255,.1);
      box-shadow:var(--shadow);
    }

    .hero::after{
      content:"";
      position:absolute;
      inset:auto -18% -35% 40%;
      height:70%;
      background:radial-gradient(circle, rgba(136,243,200,.16), transparent 60%);
      pointer-events:none;
      filter:blur(12px);
    }

    .eyebrow{
      display:inline-flex;
      align-items:center;
      gap:.5rem;
      font-size:.75rem;
      letter-spacing:.14em;
      text-transform:uppercase;
      color:var(--accent);
      margin-bottom:.8rem;
    }
    .eyebrow::before{
      content:"";
      width:26px;height:1px;background:currentColor;
    }

    .hero h3{
      margin:0;
      font-size:clamp(1.8rem, 4vw, 3.4rem);
      line-height:.95;
      max-width:11ch;
      letter-spacing:-.05em;
    }

    .hero p{
      max-width:64ch;
      color:var(--soft);
      line-height:1.6;
      margin:1rem 0 1.2rem;
      font-size:.98rem;
    }

    .hero-actions{
      display:flex;
      gap:.75rem;
      flex-wrap:wrap;
      align-items:center;
    }

    .inline-metrics{
      display:grid;
      grid-template-columns: repeat(4, 1fr);
      gap:.75rem;
      margin-top:1rem;
    }

    .metric{
      padding:.8rem;
      border-radius:18px;
      background:rgba(255,255,255,.03);
      border:1px solid rgba(255,255,255,.06);
    }

    .metric .k{
      display:block;
      font-family:"IBM Plex Mono", monospace;
      color:var(--accent);
      font-size:1.15rem;
      margin-bottom:.18rem;
    }
    .metric .l{
      color:var(--muted);
      font-size:.74rem;
      line-height:1.4;
    }

    .section-card{
      border-radius:24px;
      padding:1rem;
      background:linear-gradient(180deg, rgba(16,28,43,.85), rgba(10,19,31,.86));
      border:1px solid rgba(125,211,255,.09);
      box-shadow:var(--shadow);
      min-width:0;
    }

    .section-head{
      display:flex;
      justify-content:space-between;
      align-items:flex-start;
      gap:1rem;
      margin-bottom:.9rem;
    }
    .section-head h4{
      margin:0;
      font-size:1rem;
      letter-spacing:.02em;
    }
    .section-head p{
      margin:.25rem 0 0;
      color:var(--muted);
      font-size:.8rem;
    }

    .modes{
      display:grid;
      grid-template-columns: repeat(4, 1fr);
      gap:.75rem;
    }

    .mode{
      padding:.85rem;
      border-radius:18px;
      border:1px solid rgba(255,255,255,.06);
      background:
        linear-gradient(180deg, rgba(255,255,255,.03), rgba(255,255,255,.015));
      transition:.25s ease;
      cursor:pointer;
    }
    .mode:hover{
      transform:translateY(-3px);
      border-color:rgba(136,243,200,.2);
      background:linear-gradient(180deg, rgba(136,243,200,.08), rgba(125,211,255,.05));
    }
    .mode strong{
      display:block;
      margin-bottom:.35rem;
      font-size:.9rem;
    }
    .mode span{
      display:block;
      color:var(--muted);
      font-size:.75rem;
      line-height:1.45;
    }

    .split-shell{
      display:grid;
      grid-template-rows: minmax(310px, 1fr) auto;
      gap:1rem;
      min-height:640px;
    }

    .terminal{
      border-radius:24px;
      overflow:hidden;
      border:1px solid rgba(125,211,255,.11);
      background:#08111a;
      box-shadow:var(--shadow);
      display:flex;
      flex-direction:column;
      min-height:0;
    }

    .terminal-top{
      display:flex;
      align-items:center;
      justify-content:space-between;
      padding:.75rem 1rem;
      border-bottom:1px solid rgba(255,255,255,.06);
      background:linear-gradient(180deg, rgba(255,255,255,.04), rgba(255,255,255,.015));
    }

    .terminal-dots{
      display:flex; gap:.45rem;
    }
    .terminal-dots i{
      width:10px;height:10px;border-radius:50%;
      display:block;
    }
    .terminal-dots i:nth-child(1){background:#ff8f8f}
    .terminal-dots i:nth-child(2){background:#ffd16f}
    .terminal-dots i:nth-child(3){background:#80f5bf}

    .terminal-title{
      font-size:.78rem;
      color:var(--muted);
      font-family:"IBM Plex Mono", monospace;
    }

    .terminal-body{
      padding:1rem;
      overflow:auto;
      display:grid;
      gap:1rem;
      background:
        linear-gradient(180deg, rgba(8,17,26,.98), rgba(10,20,31,.98));
      font-family:"IBM Plex Mono", monospace;
    }

    .output-block,.input-block{
      border-left:2px solid rgba(125,211,255,.22);
      padding-left:.9rem;
    }

    .stream-label{
      display:flex;
      align-items:center;
      gap:.55rem;
      margin-bottom:.45rem;
      color:var(--muted);
      font-size:.74rem;
      text-transform:uppercase;
      letter-spacing:.12em;
    }

    .stream-label .pill{
      padding:.15rem .5rem;
      border-radius:999px;
      background:rgba(136,243,200,.08);
      color:var(--accent);
      border:1px solid rgba(136,243,200,.18);
      font-size:.68rem;
    }

    .terminal pre{
      margin:0;
      white-space:pre-wrap;
      line-height:1.62;
      color:#d9ecff;
      font-size:.84rem;
    }

    .line-ok{color:var(--ok)}
    .line-warn{color:var(--warn)}
    .line-danger{color:var(--danger)}
    .line-accent{color:var(--accent-2)}

    .action-row{
      display:flex;
      flex-wrap:wrap;
      gap:.55rem;
      margin-top:.8rem;
    }

    .tiny{
      border:none;
      border-radius:999px;
      padding:.5rem .72rem;
      font-size:.72rem;
      cursor:pointer;
      color:var(--soft);
      background:rgba(255,255,255,.05);
      border:1px solid rgba(255,255,255,.06);
      transition:.2s ease;
      font-family:inherit;
    }
    .tiny:hover{
      transform:translateY(-1px);
      background:rgba(136,243,200,.08);
      border-color:rgba(136,243,200,.2);
      color:var(--text);
    }

    .composer{
      border-radius:24px;
      background:linear-gradient(180deg, rgba(15,28,41,.96), rgba(10,18,28,.96));
      border:1px solid rgba(125,211,255,.10);
      box-shadow:var(--shadow);
      padding:1rem;
      position:sticky;
      bottom:0;
    }

    .composer-top{
      display:flex;
      align-items:center;
      justify-content:space-between;
      gap:1rem;
      margin-bottom:.8rem;
      flex-wrap:wrap;
    }

    .composer-top .left,.composer-top .right{
      display:flex;
      gap:.55rem;
      align-items:center;
      flex-wrap:wrap;
    }

    .badge{
      display:inline-flex;
      align-items:center;
      gap:.45rem;
      padding:.5rem .7rem;
      border-radius:999px;
      background:rgba(255,255,255,.04);
      border:1px solid rgba(255,255,255,.06);
      color:var(--soft);
      font-size:.74rem;
    }
    .badge strong{color:var(--text)}

    .input-shell{
      display:grid;
      grid-template-columns: 1fr auto;
      gap:.85rem;
      align-items:end;
    }

    .input-wrap{
      position:relative;
      display:flex;
      flex-direction:column;
      gap:.65rem;
    }

    textarea{
      width:100%;
      min-height:130px;
      max-height:260px;
      resize:vertical;
      border:none;
      outline:none;
      border-radius:18px;
      padding:1rem 1rem 3.25rem;
      color:var(--text);
      background:
        linear-gradient(180deg, rgba(255,255,255,.04), rgba(255,255,255,.02));
      border:1px solid rgba(255,255,255,.08);
      font: 500 .95rem/1.6 "Space Grotesk", sans-serif;
      box-shadow:inset 0 1px 0 rgba(255,255,255,.03);
    }
    textarea::placeholder{color:#7d95ab}

    .input-toolbar{
      position:absolute;
      left:.8rem;
      right:.8rem;
      bottom:.8rem;
      display:flex;
      justify-content:space-between;
      align-items:center;
      gap:.65rem;
      flex-wrap:wrap;
    }

    .toolbar-left,.toolbar-right{
      display:flex;
      gap:.45rem;
      flex-wrap:wrap;
      align-items:center;
    }

    .send-stack{
      display:flex;
      flex-direction:column;
      gap:.65rem;
      min-width:190px;
    }

    .send-stack .cta{width:100%}

    .mini-note{
      color:var(--muted);
      font-size:.72rem;
      text-align:right;
      line-height:1.45;
    }

    .right-stack{
      display:grid;
      gap:1rem;
      align-content:start;
    }

    .swarm-grid{
      display:grid;
      grid-template-columns: repeat(2, 1fr);
      gap:.75rem;
    }

    .swarm-node{
      border-radius:18px;
      padding:.85rem;
      border:1px solid rgba(255,255,255,.06);
      background:rgba(255,255,255,.025);
      position:relative;
      overflow:hidden;
    }
    .swarm-node::before{
      content:"";
      position:absolute;
      inset:-40% auto auto 65%;
      width:120px;height:120px;border-radius:50%;
      background:radial-gradient(circle, rgba(136,243,200,.12), transparent 65%);
      pointer-events:none;
    }
    .swarm-node h5{
      margin:0 0 .3rem;
      font-size:.9rem;
    }
    .swarm-node p{
      margin:0;
      color:var(--muted);
      font-size:.74rem;
      line-height:1.45;
    }
    .swarm-node .status{
      margin-top:.7rem;
      display:inline-flex;
      align-items:center;
      gap:.35rem;
      font-family:"IBM Plex Mono", monospace;
      font-size:.7rem;
      color:var(--accent);
    }
    .swarm-node .status::before{
      content:"";
      width:7px;height:7px;border-radius:50%;
      background:currentColor;
      box-shadow:0 0 12px currentColor;
    }

    .provider-ticker{
      display:grid;
      gap:.55rem;
    }

    .provider-row{
      display:grid;
      grid-template-columns: 1fr auto auto;
      gap:.6rem;
      align-items:center;
      padding:.72rem .8rem;
      border-radius:16px;
      background:rgba(255,255,255,.025);
      border:1px solid rgba(255,255,255,.06);
    }
    .provider-row strong{font-size:.84rem}
    .provider-row span{
      font-size:.72rem;
      color:var(--muted);
      font-family:"IBM Plex Mono", monospace;
    }

    .status-tag{
      display:inline-flex;
      align-items:center;
      padding:.22rem .5rem;
      border-radius:999px;
      font-size:.68rem;
      font-weight:700;
      letter-spacing:.06em;
      text-transform:uppercase;
    }
    .status-online{background:rgba(127,255,176,.12); color:var(--ok)}
    .status-free{background:rgba(125,211,255,.12); color:var(--accent-2)}
    .status-mixed{background:rgba(188,168,255,.12); color:var(--violet)}

    .settings-list{
      display:grid;
      gap:.7rem;
    }

    .setting{
      display:flex;
      align-items:center;
      justify-content:space-between;
      gap:.8rem;
      padding:.8rem .85rem;
      border-radius:16px;
      background:rgba(255,255,255,.025);
      border:1px solid rgba(255,255,255,.06);
    }

    .setting h6{
      margin:0 0 .18rem;
      font-size:.84rem;
    }
    .setting p{
      margin:0;
      color:var(--muted);
      font-size:.72rem;
    }

    .switch{
      width:48px; height:28px;
      border-radius:999px;
      background:rgba(255,255,255,.12);
      position:relative;
      flex:0 0 auto;
      cursor:pointer;
      transition:.24s ease;
      border:1px solid rgba(255,255,255,.08);
    }
    .switch::after{
      content:"";
      position:absolute;
      top:3px; left:3px;
      width:20px;height:20px;
      border-radius:50%;
      background:#fff;
      transition:.24s ease;
      box-shadow:0 4px 10px rgba(0,0,0,.25);
    }
    .switch.active{
      background:linear-gradient(90deg, rgba(136,243,200,.65), rgba(125,211,255,.65));
      border-color:rgba(136,243,200,.25);
    }
    .switch.active::after{left:23px}

    .footer-note{
      margin-top:auto;
      padding:1rem;
      color:var(--muted);
      font-size:.74rem;
      line-height:1.55;
      border-top:1px solid rgba(255,255,255,.06);
    }
    .footer-note strong{color:var(--soft)}

    .reveal{
      opacity:0;
      transform:translateY(18px);
      animation:rise .7s ease forwards;
    }
    .delay-1{animation-delay:.06s}
    .delay-2{animation-delay:.12s}
    .delay-3{animation-delay:.18s}
    .delay-4{animation-delay:.24s}
    .delay-5{animation-delay:.3s}
    .delay-6{animation-delay:.36s}

    @keyframes rise{
      to{opacity:1; transform:translateY(0)}
    }

    .sr-only{
      position:absolute;
      width:1px;height:1px;padding:0;margin:-1px;
      overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;border:0;
    }

    @media (max-width: 1180px){
      .grid{grid-template-columns:1fr}
      .right-stack{grid-template-columns:repeat(2,1fr)}
      .modes{grid-template-columns:repeat(3,1fr)}
    }

    @media (max-width: 980px){
      .app{grid-template-columns:1fr}
      .sidebar{
        position:fixed;
        top:0; left:0; bottom:0;
        width:min(90vw, 340px);
        transform:translateX(-102%);
        transition:.28s ease;
        box-shadow:0 30px 60px rgba(0,0,0,.42);
      }
      body.sidebar-open .sidebar{transform:translateX(0)}
      .hamburger{display:inline-flex}
      .topbar{padding:.9rem 1rem}
      .workspace{padding:.9rem}
      .right-stack{grid-template-columns:1fr}
      .inline-metrics{grid-template-columns:repeat(2, 1fr)}
      .modes{grid-template-columns:repeat(2,1fr)}
    }

    @media (max-width: 680px){
      .hero h3{max-width:14ch}
      .input-shell{grid-template-columns:1fr}
      .send-stack{min-width:0}
      .composer{padding:.85rem}
      .split-shell{min-height:auto}
      .terminal{min-height:380px}
      .swarm-grid{grid-template-columns:1fr}
      .inline-metrics{grid-template-columns:1fr 1fr}
      .modes{grid-template-columns:1fr}
      .topbar-right{width:100%; justify-content:flex-start}
      .hero-actions{align-items:stretch}
      .hero-actions .cta,.hero-actions .ghost-btn{width:100%}
    }
  </style>
</head>
<body>
  <div class="app">
    <aside class="sidebar" aria-label="Sidebar navigasi">
      <div class="brand">
        <div class="brand-left">
          <div class="logo" aria-hidden="true"></div>
          <div>
            <h1>Human CLI</h1>
            <p>Agent Orchestrator Workspace</p>
          </div>
        </div>
        <button class="collapse-btn" id="closeSidebar" aria-label="Tutup sidebar">✕</button>
      </div>

      <div class="sidebar-scroll">
        <section class="panel reveal delay-1">
          <div class="panel-header">
            <h2>Tab Chat</h2>
            <button class="ghost-btn" data-toast="Sesi baru dibuat di sidebar.">+ New</button>
          </div>
          <div class="panel-body">
            <nav class="nav-list" aria-label="Navigasi utama">
              <a class="nav-item active" href="#workspace-chat"><span>⌘</span> Workspace Chat <span class="nav-badge">Live</span></a>
              <a class="nav-item" href="#modes"><span>◫</span> Mode Router</a>
              <a class="nav-item" href="#providers"><span>◎</span> AI Providers</a>
              <a class="nav-item" href="#profiles"><span>◌</span> Profile Swarm</a>
              <a class="nav-item" href="#settings"><span>⚙</span> Settings</a>
            </nav>
          </div>
        </section>

        <section class="panel reveal delay-2">
          <div class="panel-header">
            <h2>Session</h2>
            <button class="ghost-btn" data-toast="Riwayat sesi dibuka.">History</button>
          </div>
          <div class="panel-body session-list">
            <article class="session-item">
              <h3>Deploy VPS + Audit Env</h3>
              <p>Repository, Docker compose, secret variables, dan fallback model routing.</p>
            </article>
            <article class="session-item">
              <h3>README Auto Generate</h3>
              <p>Workflow GitHub Action + skill extraction + ringkasan repo.</p>
            </article>
            <article class="session-item">
              <h3>Security Review Session</h3>
              <p>Dependency scan, PAT token review, permission matrix, webhooks.</p>
            </article>
          </div>
        </section>

        <section class="panel reveal delay-3">
          <div class="panel-header">
            <h2>Bookmarks</h2>
            <button class="ghost-btn" data-toast="Daftar bookmark chat ditampilkan.">List</button>
          </div>
          <div class="panel-body bookmark-list">
            <article class="bookmark-item">
              <h3>Python docs browser task</h3>
              <p>Mode: Browser · 10:44 WIB · Auto route berhasil.</p>
            </article>
            <article class="bookmark-item">
              <h3>Agent code workflow</h3>
              <p>Mode: Agent Code · 11:03 WIB · CI template siap ekspor.</p>
            </article>
          </div>
        </section>

        <section class="panel reveal delay-4">
          <div class="panel-header">
            <h2>Connected Accounts</h2>
            <button class="ghost-btn" data-toast="Halaman provider OAuth dibuka.">OAuth</button>
          </div>
          <div class="panel-body provider-list">
            <article class="provider-item">
              <h3>GitHub · Vercel · Notion</h3>
              <p>Repository, deployment target, knowledge sync, dan docs context.</p>
              <span class="provider-status">Connected</span>
            </article>
            <article class="provider-item">
              <h3>OpenRouter · Mistral · Gemini</h3>
              <p>BYOK, free/freemium routing, fallback chain, dan cloud agents.</p>
              <span class="provider-status">Hybrid Keys</span>
            </article>
          </div>
        </section>

        <section class="panel reveal delay-5">
          <div class="panel-header">
            <h2>Quick Filter</h2>
          </div>
          <div class="panel-body mini-stack">
            <div class="chip-row">
              <span class="chip active">Indonesia Default</span>
              <span class="chip">Free/Freemium</span>
              <span class="chip">Cloud Agent</span>
              <span class="chip">Ragmyai</span>
              <span class="chip">Audit</span>
              <span class="chip">VPS</span>
            </div>
          </div>
        </section>
      </div>

      <div class="footer-note">
        <strong>Founder Sign:</strong> DHANI YULIAWAN<br>
        Konsep UI dirancang untuk split workspace, auto mode routing, profile swarm, export/import, bookmark, dan multi-provider orchestration.
      </div>
    </aside>

    <main class="main">
      <header class="topbar">
        <div class="topbar-left">
          <button class="collapse-btn hamburger" id="openSidebar" aria-label="Buka sidebar">☰</button>
          <div class="workspace-title">
            <h2>Human CLI / Dashboard</h2>
            <p>Dashboard gaya orchestrator swarm dengan rasa CLI terminal, AI chat, dan workspace ala dev cloud.</p>
          </div>
        </div>
        <div class="topbar-right">
          <button class="ghost-btn" data-toast="Export/Import settings dibuka.">Export / Import</button>
          <button class="ghost-btn" data-toast="Configure profile agent swarm dibuka.">Profile Swarm</button>
          <button class="ghost-btn" data-toast="Provider manager dibuka.">Connected Account</button>
          <button class="cta" data-toast="Auto review & audit dijalankan.">Run Audit</button>
        </div>
      </header>

      <div class="workspace">
        <div class="grid">
          <section class="column">
            <article class="hero reveal delay-1" id="workspace-chat">
              <div class="eyebrow">Human CLI Workspace</div>
              <h3>Kelola repo, coding, deployment, cloud, VPS, dan swarm agent dari satu cockpit.</h3>
              <p>
                Konsep ini menggabungkan rasa dashboard coding agent, terminal observability, dan workspace AI modern.
                Fokus utamanya adalah <strong>auto mode selection</strong>, <strong>profile orchestrator</strong>, <strong>AI provider routing</strong>,
                <strong>security review</strong>, <strong>knowledge sandbox</strong>, dan <strong>exportable chat actions</strong>.
              </p>
              <div class="hero-actions">
                <button class="cta" data-fill="Buka dokumentasi Python resmi lalu rangkum bagian virtual environment dan packaging.">AI✨ Suggest</button>
                <button class="ghost-btn" data-fill="Buatkan GitHub Action workflow README auto generate dan audit secret usage.">Generate Task</button>
                <button class="ghost-btn" data-toast="Mikrofon aktif. Fitur STT demo.">Mic / STT</button>
                <button class="ghost-btn" data-toast="Clipper web aktif. Tambahkan URL sebagai source.">Clipper</button>
              </div>
              <div class="inline-metrics">
                <div class="metric">
                  <span class="k">08</span>
                  <span class="l">Mode aktif: CLI, SSH, SFTP, Browser, Chat, Code, Security, Multimodal</span>
                </div>
                <div class="metric">
                  <span class="k">05</span>
                  <span class="l">Fallback model chain maksimal sesuai brief profile</span>
                </div>
                <div class="metric">
                  <span class="k">24</span>
                  <span class="l">Provider cloud, BYOK, local AI, dan free/freemium ready</span>
                </div>
                <div class="metric">
                  <span class="k">∞</span>
                  <span class="l">Arsitektur dapat diperluas untuk connector, MCP, webhook, dan swarm</span>
                </div>
              </div>
            </article>

            <article class="section-card reveal delay-2" id="modes">
              <div class="section-head">
                <div>
                  <h4>Auto Mode Selection</h4>
                  <p>Agent menganalisis input user, konteks sesi, source, planning, aturan, lalu memilih mode eksekusi paling relevan.</p>
                </div>
                <button class="ghost-btn" data-toast="Semua mode dapat diedit, ditambah, atau dihapus.">Manage Modes</button>
              </div>
              <div class="modes">
                <div class="mode" data-fill="Buka dokumentasi Python dan cari bagian requests session usage.">
                  <strong>Browser</strong>
                  <span>Cocok untuk link, url, web source, scraping ringan, dan knowledge build.</span>
                </div>
                <div class="mode" data-fill="Masuk ke VPS production via SSH lalu cek service docker compose dan logs.">
                  <strong>SSH</strong>
                  <span>Remote shell, eksekusi command, observasi host, deployment ops.</span>
                </div>
                <div class="mode" data-fill="Upload file env.example dan backup logs via SFTP.">
                  <strong>SFTP</strong>
                  <span>Transfer file aman, sinkronisasi project, source import, artifacts.</span>
                </div>
                <div class="mode" data-fill="Buatkan workflow GitHub Action untuk generate README otomatis.">
                  <strong>Agent Code</strong>
                  <span>Generate, refactor, review, patch, dan workflow engineering.</span>
                </div>
                <div class="mode" data-fill="Audit dependency, PAT token, workflow permissions, dan .env exposure.">
                  <strong>Security</strong>
                  <span>Review, audit, scan, rules, compliance, dan hardening.</span>
                </div>
                <div class="mode" data-fill="Bantu brainstorming arsitektur orchestrator profile swarm untuk proyek pribadi.">
                  <strong>Chat Assistant</strong>
                  <span>Diskusi, planning, reasoning, dan ide produk atau dokumentasi.</span>
                </div>
                <div class="mode" data-fill="Generate dokumen ringkasan proyek dan diagram struktur repo.">
                  <strong>Multimodal</strong>
                  <span>Generate text, image placeholder, summary doc, dan export media.</span>
                </div>
                <div class="mode" data-fill="Uji endpoint staging dan cek permukaan attack surface dasar secara aman.">
                  <strong>Pentest</strong>
                  <span>Assessment terbatas, simulasi review, dan analisis risiko.</span>
                </div>
              </div>
            </article>

            <section class="split-shell reveal delay-3" aria-label="Split screen chat terminal">
              <div class="terminal" aria-live="polite">
                <div class="terminal-top">
                  <div class="terminal-dots" aria-hidden="true"><i></i><i></i><i></i></div>
                  <div class="terminal-title">human-cli://workspace/main-session — output stream</div>
                  <button class="ghost-btn" data-toast="Panel output dapat auto show/hide.">Auto Panel</button>
                </div>
                <div class="terminal-body" id="terminalBody">
                  <div class="input-block reveal delay-1">
                    <div class="stream-label">User Input <span class="pill">auto routed</span></div>
                    <pre>&gt; Buatkan code GitHub Action workflow README auto generate untuk repository ini, lalu audit secret usage dan berikan fallback plan deployment.</pre>
                    <div class="action-row">
                      <button class="tiny" data-toast="Input user masuk mode edit.">Edit</button>
                      <button class="tiny copy-btn" data-copy="Buatkan code GitHub Action workflow README auto generate untuk repository ini, lalu audit secret usage dan berikan fallback plan deployment.">Copy</button>
                      <button class="tiny" data-toast="Bookmark tersimpan untuk input + output + timestamp.">Bookmarks</button>
                      <button class="tiny" data-toast="Input user dihapus dari sesi demo.">Delete</button>
                    </div>
                  </div>

                  <div class="output-block reveal delay-2">
                    <div class="stream-label">Agent Output <span class="pill">agent code + security</span></div>
                    <pre>
<span class="line-accent">[router]</span> intent detected: repository automation + workflow generation + secret review
<span class="line-ok">[mode selected]</span> Agent Code → Security Review → Deployment Planner

<span class="line-accent">[generate]</span> created:
- .github/workflows/readme-auto.yml
- docs/README.template.md
- scripts/generate-readme.js

<span class="line-warn">[security review]</span>
- check workflow permission scope: contents: write only if required
- avoid plain PAT in repo secrets if GitHub token is enough
- validate no .env leak in artifact uploads
- use branch protection before auto commit

<span class="line-ok">[fallback]</span>
1. primary deploy: GitHub Action → VPS via SSH action
2. fallback #1: manual release tag pipeline
3. fallback #2: local build artifact + SFTP sync
4. fallback #3: container image deployment from registry mirror

<span class="line-accent">[next action]</span> open profile “Repo Security Maintainer” for auto review loop?
                    </pre>
                    <div class="action-row">
                      <button class="tiny" data-toast="Respons agent dikirim ulang.">Resent</button>
                      <button class="tiny copy-btn" data-copy="[router] intent detected: repository automation + workflow generation + secret review">Copy</button>
                      <button class="tiny" data-toast="TTS read aloud diaktifkan untuk output.">Speaker / TTS</button>
                      <button class="tiny" data-toast="Panel export dibuka.">Save / Export</button>
                      <button class="tiny" data-toast="Ekspor 1 input + 1 output.">Export Pair</button>
                      <button class="tiny" data-toast="Ekspor seluruh sesi chat.">Export Session</button>
                    </div>
                  </div>
                </div>
              </div>

              <form class="composer" id="composerForm">
                <div class="composer-top">
                  <div class="left">
                    <span class="badge"><strong>Default Agent</strong> Bahasa Indonesia</span>
                    <span class="badge"><strong>Provider</strong> Ragmyai / Cloud Agent</span>
                    <span class="badge"><strong>Fallback</strong> 5 chain ready</span>
                  </div>
                  <div class="right">
                    <button class="tiny" type="button" data-toast="Bookmark sesi dibuka.">Bookmarks</button>
                    <button class="tiny" type="button" data-toast="Upload source file: txt md doc pdf ppt canvas zip">Upload Source</button>
                    <button class="tiny" type="button" data-toast="Tambahkan URL source web.">Add URL</button>
                  </div>
                </div>

                <div class="input-shell">
                  <div class="input-wrap">
                    <label class="sr-only" for="userInput">Input User</label>
                    <textarea id="userInput" placeholder="Tulis perintah, planning, chat, task coding, review security, browser request, deployment, atau source extraction...&#10;&#10;Contoh: Coba buka web python dokumentasi dan ringkas bagian virtual environment."></textarea>
                    <div class="input-toolbar">
                      <div class="toolbar-left">
                        <button class="tiny" type="button" data-fill="Saran AI: analisis repo ini, pilih mode terbaik, lalu buat langkah kerja bertahap.">AI✨ Suggest</button>
                        <button class="tiny" type="button" data-toast="Mikrofon aktif.">Mic</button>
                        <button class="tiny" type="button" data-toast="Clipper aktif.">Clipper</button>
                        <button class="tiny" type="button" data-toast="Mode browser siap menerima link.">Browser</button>
                      </div>
                      <div class="toolbar-right">
                        <span class="badge">Always above keyboard</span>
                      </div>
                    </div>
                  </div>
                  <div class="send-stack">
                    <button class="cta" type="submit">Kirim ke Human CLI</button>
                    <button class="ghost-btn" type="button" data-toast="Autonomous workflow dijalankan berdasarkan profile aktif.">Run Autonomous</button>
                    <div class="mini-note">
                      Setiap text field dapat diberi tombol <strong>AI✨</strong>, bisa diedit, disalin, disimpan, dihapus, dan diekspor.
                    </div>
                  </div>
                </div>
              </form>
            </section>
          </section>

          <aside class="column right-stack">
            <article class="section-card reveal delay-2" id="profiles">
              <div class="section-head">
                <div>
                  <h4>Profile Orchestrator Swarm</h4>
                  <p>Profile menyimpan prompt, tools, MCP, connector, webhook, websocket, memory, secrets, env, PAT token, dan agent behavior.</p>
                </div>
                <button class="ghost-btn" data-toast="Tambah profile swarm baru.">Add Profile</button>
              </div>
              <div class="swarm-grid">
                <div class="swarm-node">
                  <h5>Repo Commander</h5>
                  <p>GitHub repos, workflow planning, branch strategy, README generation, issue triage.</p>
                  <span class="status">ACTIVE</span>
                </div>
                <div class="swarm-node">
                  <h5>Security Auditor</h5>
                  <p>Audit dependencies, secret exposure, PAT scope, config hardening, review gates.</p>
                  <span class="status">ACTIVE</span>
                </div>
                <div class="swarm-node">
                  <h5>Cloud Deployer</h5>
                  <p>VPS, Docker, release rollback, SFTP sync, health checks, release notes.</p>
                  <span class="status">READY</span>
                </div>
                <div class="swarm-node">
                  <h5>Knowledge Builder</h5>
                  <p>SKILL.md generation dari file, text, zip, URL, docs source, dan pasted context.</p>
                  <span class="status">READY</span>
                </div>
              </div>
            </article>

            <article class="section-card reveal delay-3" id="providers">
              <div class="section-head">
                <div>
                  <h4>AI Model Manager</h4>
                  <p>Provider free/freemium diutamakan, support BYOK, multiple API key, random auto routing, dan fallback model.</p>
                </div>
                <button class="ghost-btn" data-toast="Tambah provider eksternal dari URL/web endpoint.">Add External</button>
              </div>

              <div class="provider-ticker">
                <div class="provider-row">
                  <strong>Ragmyai Cloud</strong>
                  <span>default-agent:id</span>
                  <em class="status-tag status-online">online</em>
                </div>
                <div class="provider-row">
                  <strong>Mistral / OpenRouter</strong>
                  <span>fallback-01</span>
                  <em class="status-tag status-free">free+</em>
                </div>
                <div class="provider-row">
                  <strong>Gemini / OpenAI / Nvidia</strong>
                  <span>fallback-02</span>
                  <em class="status-tag status-mixed">hybrid</em>
                </div>
                <div class="provider-row">
                  <strong>Ollama Local</strong>
                  <span>fallback-03</span>
                  <em class="status-tag status-online">local</em>
                </div>
                <div class="provider-row">
                  <strong>HuggingFace / ZAI / AIHubMix</strong>
                  <span>fallback-04</span>
                  <em class="status-tag status-free">pool</em>
                </div>
              </div>
            </article>

            <article class="section-card reveal delay-4">
              <div class="section-head">
                <div>
                  <h4>Sandboxing & Library</h4>
                  <p>Source upload lokal dan URL collector untuk membangun skill, memory, dan konteks kerja.</p>
                </div>
                <button class="ghost-btn" data-toast="Import source file dan URL dibuka.">Import</button>
              </div>
              <div class="chip-row">
                <span class="chip active">.txt</span>
                <span class="chip active">.md</span>
                <span class="chip">.pdf</span>
                <span class="chip">.doc/.word</span>
                <span class="chip">.ppt</span>
                <span class="chip">.canvas</span>
                <span class="chip">.zip</span>
                <span class="chip">Web URL</span>
                <span class="chip">Paste max 100Kb</span>
              </div>
            </article>

            <article class="section-card reveal delay-5" id="settings">
              <div class="section-head">
                <div>
                  <h4>Settings & App Controls</h4>
                  <p>Export/import data, app settings, connected account providers, auto panel show/hide, session removal, dan audit status integrasi.</p>
                </div>
                <button class="ghost-btn" data-toast="Semua item yang dibuat bisa diedit atau dihapus.">Edit / Remove</button>
              </div>

              <div class="settings-list">
                <div class="setting">
                  <div>
                    <h6>Auto Show / Hide Panel</h6>
                    <p>Optimalkan view page agar panel tidak mempersempit area kerja.</p>
                  </div>
                  <button class="switch active" aria-label="Aktif"></button>
                </div>
                <div class="setting">
                  <div>
                    <h6>Default Agent Bahasa Indonesia</h6>
                    <p>Agent default menggunakan bahasa Indonesia untuk seluruh percakapan.</p>
                  </div>
                  <button class="switch active" aria-label="Aktif"></button>
                </div>
                <div class="setting">
                  <div>
                    <h6>Connected Accounts OAuth</h6>
                    <p>Google, GitHub, Facebook, Instagram, Vercel, Notion, n8n, HuggingFace, OpenRouter, Nvidia, Mistral, Gemini, OpenAI, Ollama, ZAI, dan lainnya.</p>
                  </div>
                  <button class="switch active" aria-label="Aktif"></button>
                </div>
                <div class="setting">
                  <div>
                    <h6>Session Delete & Export</h6>
                    <p>Hapus sesi chat, ekspor satu pasangan pesan, atau seluruh sesi ke berbagai format file.</p>
                  </div>
                  <button class="switch active" aria-label="Aktif"></button>
                </div>
              </div>
            </article>

            <article class="section-card reveal delay-6">
              <div class="section-head">
                <div>
                  <h4>Audit Ringkas Integrasi</h4>
                  <p>Review cepat terhadap permintaan fitur dan status implementasi pada prototype UI ini.</p>
                </div>
              </div>
              <div class="terminal-body" style="border-radius:18px; padding:.9rem;">
                <pre>
<span class="line-ok">[done]</span> split screen input/output
<span class="line-ok">[done]</span> auto mode selection visual
<span class="line-ok">[done]</span> bookmarks / session / history dalam tab chat sidebar
<span class="line-ok">[done]</span> ai suggest di input user
<span class="line-ok">[done]</span> export/import settings section
<span class="line-ok">[done]</span> connected account provider list
<span class="line-ok">[done]</span> profile swarm panel
<span class="line-ok">[done]</span> source library upload UI
<span class="line-warn">[pending backend]</span> real OAuth, real file parsing, real model routing, real SSH/SFTP
<span class="line-warn">[pending backend]</span> TTS/STT, browser automation, sandbox execution, persistence DB
<span class="line-accent">[next]</span> hubungkan ke API / local backend / websocket orchestrator
                </pre>
              </div>
            </article>
          </aside>
        </div>
      </div>
    </main>
  </div>

  <script>
    const body = document.body;
    const openSidebar = document.getElementById('openSidebar');
    const closeSidebar = document.getElementById('closeSidebar');
    const userInput = document.getElementById('userInput');
    const composerForm = document.getElementById('composerForm');
    const terminalBody = document.getElementById('terminalBody');

    openSidebar?.addEventListener('click', () => body.classList.add('sidebar-open'));
    closeSidebar?.addEventListener('click', () => body.classList.remove('sidebar-open'));

    document.addEventListener('click', (e) => {
      const fill = e.target.closest('[data-fill]');
      const toast = e.target.closest('[data-toast]');
      const copy = e.target.closest('.copy-btn');
      const sw = e.target.closest('.switch');
      const navItem = e.target.closest('.nav-item');
      const mode = e.target.closest('.mode');

      if (fill) {
        userInput.value = fill.dataset.fill;
        userInput.focus();
        showToast('Prompt dimasukkan ke kolom input.');
      }

      if (toast) {
        showToast(toast.dataset.toast);
      }

      if (copy) {
        navigator.clipboard.writeText(copy.dataset.copy || '').then(() => {
          showToast('Teks berhasil disalin.');
        }).catch(() => showToast('Gagal menyalin teks.'));
      }

      if (sw) {
        sw.classList.toggle('active');
      }

      if (navItem && window.innerWidth <= 980) {
        body.classList.remove('sidebar-open');
      }

      if (mode) {
        showToast('Mode preset dimasukkan ke input.');
      }
    });

    composerForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const value = userInput.value.trim();
      if (!value) {
        showToast('Tulis perintah terlebih dahulu.');
        return;
      }

      const route = detectMode(value);
      const pair = document.createElement('div');
      pair.className = 'reveal';
      pair.innerHTML = `
        <div class="input-block">
          <div class="stream-label">User Input <span class="pill">manual submit</span></div>
          <pre>&gt; ${escapeHtml(value)}</pre>
          <div class="action-row">
            <button class="tiny" type="button" data-toast="Input user masuk mode edit.">Edit</button>
            <button class="tiny copy-btn" type="button" data-copy="${escapeAttr(value)}">Copy</button>
            <button class="tiny" type="button" data-toast="Bookmark tersimpan untuk input + output + timestamp.">Bookmarks</button>
            <button class="tiny" type="button" data-toast="Input user dihapus dari sesi demo.">Delete</button>
          </div>
        </div>
        <div class="output-block">
          <div class="stream-label">Agent Output <span class="pill">${route.label}</span></div>
          <pre><span class="line-accent">[router]</span> analisis konteks selesai
<span class="line-ok">[mode selected]</span> ${route.label}

<span class="line-accent">[summary]</span> permintaan Anda siap dijalankan pada backend orchestrator.
<span class="line-warn">[demo ui]</span> prototype ini belum mengeksekusi SSH / browser / provider nyata.
<span class="line-ok">[next step]</span> hubungkan event ini ke engine ${route.engine} dan simpan ke session store.</pre>
          <div class="action-row">
            <button class="tiny" type="button" data-toast="Respons agent dikirim ulang.">Resent</button>
            <button class="tiny copy-btn" type="button" data-copy="[mode selected] ${route.label}">Copy</button>
            <button class="tiny" type="button" data-toast="TTS read aloud diaktifkan untuk output.">Speaker / TTS</button>
            <button class="tiny" type="button" data-toast="Panel export dibuka.">Save / Export</button>
            <button class="tiny" type="button" data-toast="Ekspor 1 input + 1 output.">Export Pair</button>
            <button class="tiny" type="button" data-toast="Ekspor seluruh sesi chat.">Export Session</button>
          </div>
        </div>
      `;
      terminalBody.appendChild(pair);
      terminalBody.scrollTop = terminalBody.scrollHeight;
      userInput.value = '';
      showToast('Pesan dikirim ke Human CLI.');
    });

    function detectMode(text){
      const t = text.toLowerCase();
      if (/(http|www\.|url|link|buka web|website|site|browser|dokumentasi)/.test(t)) {
        return { label:'Browser', engine:'browser-worker' };
      }
      if (/(ssh|vps|server|terminal|shell|docker|deploy|systemctl|logs)/.test(t)) {
        return { label:'SSH / CLI', engine:'remote-shell' };
      }
      if (/(sftp|upload|download file|sinkron|artifact)/.test(t)) {
        return { label:'SFTP', engine:'file-transfer' };
      }
      if (/(audit|security|scan|pentest|secret|token|permission|review)/.test(t)) {
        return { label:'Security', engine:'security-auditor' };
      }
      if (/(code|workflow|github action|repo|readme|refactor|bug|script)/.test(t)) {
        return { label:'Agent Code', engine:'coding-agent' };
      }
      if (/(image|gambar|dokumen|ppt|pdf|multimodal|generate)/.test(t)) {
        return { label:'Multimodal', engine:'multimodal-agent' };
      }
      return { label:'Chat Assistant', engine:'chat-orchestrator' };
    }

    function showToast(message){
      let toast = document.querySelector('.demo-toast');
      if (!toast) {
        toast = document.createElement('div');
        toast.className = 'demo-toast';
        Object.assign(toast.style, {
          position:'fixed',
          right:'16px',
          bottom:'16px',
          zIndex:'9999',
          padding:'12px 14px',
          maxWidth:'320px',
          borderRadius:'16px',
          background:'linear-gradient(180deg, rgba(15,28,41,.96), rgba(10,18,28,.96))',
          border:'1px solid rgba(136,243,200,.22)',
          color:'var(--text)',
          font:'500 14px/1.5 "Space Grotesk", sans-serif',
          boxShadow:'0 24px 48px rgba(0,0,0,.36)',
          opacity:'0',
          transform:'translateY(10px)',
          transition:'all .22s ease'
        });
        document.body.appendChild(toast);
      }
      toast.textContent = message;
      requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
      });
      clearTimeout(showToast._t);
      showToast._t = setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
      }, 2200);
    }

    function escapeHtml(str){
      return str.replace(/[&<>"']/g, m => ({
        '&':'&amp;',
        '<':'&lt;',
        '>':'&gt;',
        '"':'&quot;',
        "'":'&#39;'
      }[m]));
    }

    function escapeAttr(str){
      return str.replace(/"/g, '&quot;');
    }

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) entry.target.classList.add('in-view');
      });
    }, { threshold: .14 });

    document.querySelectorAll('.section-card, .hero, .panel').forEach(el => observer.observe(el));
  </script>
</body>
</html>
 