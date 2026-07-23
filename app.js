(function(){

  var STORAGE_KEY = 'farming-control-state-v1';
  var state = { games: [], selectedGameId: null, tab: 'chars', view: 'home' };
  var root = document.getElementById('root');
  var WEEKDAYS = ['일','월','화','수','목','금','토'];
  var saveStatusTimer = null;
  var showSaveStatus = false;

  function uid(){ return Date.now().toString(36) + Math.random().toString(36).slice(2,8); }

  function weekKeyFor(resetDay){
    var d = new Date();
    d.setHours(0,0,0,0);
    var diff = (d.getDay() - resetDay + 7) % 7;
    d.setDate(d.getDate() - diff);
    return d.toISOString().slice(0,10);
  }

  function daysUntilReset(resetDay){
    var todayDow = new Date().getDay();
    var diff = (resetDay - todayDow + 7) % 7;
    return diff;
  }

  // ---------- storage (localStorage: 브라우저에 영구 저장, 페이지를 닫아도 유지됨) ----------
  function loadData(){
    try{
      var raw = localStorage.getItem(STORAGE_KEY);
      if(raw){
        var parsed = JSON.parse(raw);
        state.games = parsed.games || [];
        state.selectedGameId = parsed.selectedGameId || (state.games[0] ? state.games[0].id : null);
      }
    }catch(e){
      console.error('저장된 데이터를 불러오지 못했습니다', e);
    }
    render();
  }

  function saveData(){
    try{
      localStorage.setItem(STORAGE_KEY, JSON.stringify({
        games: state.games, selectedGameId: state.selectedGameId
      }));
      flashSaveStatus();
    }catch(e){
      console.error('저장 실패', e);
      alert('데이터 저장에 실패했어요. 브라우저 저장 공간이 가득 찼을 수 있어요.');
    }
  }

  function flashSaveStatus(){
    showSaveStatus = true;
    var el = document.getElementById('save-status');
    if(el) el.classList.add('show');
    clearTimeout(saveStatusTimer);
    saveStatusTimer = setTimeout(function(){
      showSaveStatus = false;
      var el2 = document.getElementById('save-status');
      if(el2) el2.classList.remove('show');
    }, 1200);
  }

  function exportBackup(){
    var payload = JSON.stringify({ games: state.games, selectedGameId: state.selectedGameId }, null, 2);
    var blob = new Blob([payload], { type: 'application/json' });
    var url = URL.createObjectURL(blob);
    var a = document.createElement('a');
    var today = new Date().toISOString().slice(0,10);
    a.href = url;
    a.download = 'farming-control-backup-' + today + '.json';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  function importBackup(file){
    var reader = new FileReader();
    reader.onload = function(){
      try{
        var parsed = JSON.parse(reader.result);
        if(!parsed || !Array.isArray(parsed.games)) throw new Error('형식이 올바르지 않습니다');
        if(!confirm('백업 파일을 불러오면 현재 데이터를 덮어씁니다. 계속할까요?')) return;
        state.games = parsed.games;
        state.selectedGameId = parsed.selectedGameId || (state.games[0] ? state.games[0].id : null);
        saveData();
        render();
      }catch(e){
        alert('백업 파일을 읽는 데 실패했어요. 이 앱에서 내보낸 JSON 파일인지 확인해 주세요.');
      }
    };
    reader.readAsText(file);
  }

  function resetAll(){
    if(!confirm('모든 게임과 데이터를 삭제할까요? 이 작업은 되돌릴 수 없어요. 걱정되면 먼저 백업을 다운로드하세요.')) return;
    state.games = [];
    state.selectedGameId = null;
    saveData();
    render();
  }

  function getGame(){
    return state.games.find(function(g){ return g.id === state.selectedGameId; }) || null;
  }

  function esc(s){
    var d = document.createElement('div');
    d.textContent = s;
    return d.innerHTML;
  }

  // ---------- render ----------
  function render(){
    var game = (state.view === 'game') ? getGame() : null;
    root.className = '';
    root.innerHTML =
      renderDataBar() +
      '<div class="app">' +
        renderSidebar() +
        '<div class="main">' +
          (state.view === 'game' ? renderSummary(game) + renderTabs(game) : '') +
          '<div class="content">' + (state.view === 'home' ? renderHome() : renderContent(game)) + '</div>' +
        '</div>' +
      '</div>';
    bindEvents();
  }

  function renderDataBar(){
    return (
      '<div class="data-bar">' +
        '<span>이 브라우저에 자동 저장됨</span>' +
        '<span id="save-status" class="save-status' + (showSaveStatus?' show':'') + '">✓ 저장됨</span>' +
        '<div class="spacer"></div>' +
        '<button class="btn" id="btn-export">백업 다운로드</button>' +
        '<label class="btn" style="margin:0;">백업 불러오기<input type="file" id="btn-import" accept="application/json" style="display:none;"></label>' +
        '<button class="btn" id="btn-reset-all">전체 초기화</button>' +
      '</div>'
    );
  }

  function renderSidebar(){
    var items = state.games.map(function(g){
      var active = state.view === 'game' && g.id === state.selectedGameId;
      return (
        '<div class="game-item' + (active?' active':'') + '">' +
          '<button class="game-name-btn" data-select-game="' + g.id + '">' +
            '<span class="game-tick"></span><span>' + esc(g.name) + '</span>' +
          '</button>' +
          '<button class="game-del" data-del-game="' + g.id + '" title="게임 삭제">✕</button>' +
        '</div>'
      );
    }).join('');
    return (
      '<div class="sidebar">' +
        '<div class="brand"><span class="dot"></span>파밍 관제소</div>' +
        '<button class="home-nav-btn' + (state.view==='home'?' active':'') + '" id="btn-go-home">🏠 오늘 할 일</button>' +
        '<div class="game-list">' + items + '</div>' +
        '<button class="add-game" id="btn-add-game">+ 게임 추가</button>' +
      '</div>'
    );
  }

  function renderSummary(game){
    if(!game) return '<div class="summary"></div>';
    var total = game.characters.length;
    var pending = game.characters.filter(function(c){ return !c.completed; }).length;
    var wk = weekKeyFor(game.resetDay);
    var weeklyLeft = game.weekly.filter(function(w){ return w.doneWeekKey !== wk; }).length;
    return (
      '<div class="summary">' +
        '<div class="summary-item"><div class="summary-num mono">' + total + '</div><div class="summary-label">전체 캐릭터</div></div>' +
        '<div class="summary-item pending"><div class="summary-num mono">' + pending + '</div><div class="summary-label">육성 필요</div></div>' +
        '<div class="summary-item pending"><div class="summary-num mono">' + weeklyLeft + '</div><div class="summary-label">이번 주 남은 컨텐츠</div></div>' +
      '</div>'
    );
  }

  function renderTabs(game){
    if(!game) return '';
    var tabs = [
      ['chars','캐릭터 육성'],
      ['weekly','주간 컨텐츠'],
      ['party','파티 구성']
    ];
    return '<div class="tabs">' + tabs.map(function(t){
      return '<button class="tab-btn' + (state.tab===t[0]?' active':'') + '" data-tab="' + t[0] + '">' + t[1] + '</button>';
    }).join('') + '</div>';
  }

  function renderContent(game){
    if(!game){
      return (
        '<div class="empty">' +
          '<h3>아직 등록된 게임이 없어요</h3>' +
          '<p>먼저 왼쪽에서 게임을 추가하면 캐릭터와 주간 컨텐츠를 관리할 수 있어요.</p>' +
          '<button class="btn primary" id="btn-add-game-empty">게임 추가하기</button>' +
        '</div>'
      );
    }
    if(state.tab === 'chars') return renderCharsTab(game);
    if(state.tab === 'weekly') return renderWeeklyTab(game);
    if(state.tab === 'party') return renderPartyTab(game);
    return '';
  }

  function renderHome(){
    if(state.games.length === 0){
      return (
        '<div class="empty">' +
          '<h3>아직 등록된 게임이 없어요</h3>' +
          '<p>게임을 추가하면 모든 게임의 할 일을 이 화면 하나에 모아볼 수 있어요.</p>' +
          '<button class="btn primary" id="btn-add-game-home">게임 추가하기</button>' +
        '</div>'
      );
    }

    var totalPendingChars = 0;
    var totalWeeklyLeft = 0;
    state.games.forEach(function(g){
      totalPendingChars += g.characters.filter(function(c){ return !c.completed; }).length;
      var wk = weekKeyFor(g.resetDay);
      totalWeeklyLeft += g.weekly.filter(function(w){ return w.doneWeekKey !== wk; }).length;
    });

    var stats =
      '<div class="home-stats">' +
        '<div class="home-stat-card"><div class="home-stat-num mono">' + state.games.length + '</div><div class="home-stat-label">등록된 게임</div></div>' +
        '<div class="home-stat-card pending"><div class="home-stat-num mono">' + totalPendingChars + '</div><div class="home-stat-label">육성 필요 캐릭터</div></div>' +
        '<div class="home-stat-card pending"><div class="home-stat-num mono">' + totalWeeklyLeft + '</div><div class="home-stat-label">이번 주 남은 컨텐츠</div></div>' +
      '</div>';

    var weeklyGroups = state.games.map(function(g){
      var wk = weekKeyFor(g.resetDay);
      var pending = g.weekly.filter(function(w){ return w.doneWeekKey !== wk; });
      if(pending.length === 0) return '';
      var rows = pending.map(function(w){
        return (
          '<div class="weekly-row">' +
            '<input type="checkbox" data-home-weekly="' + g.id + '|' + w.id + '">' +
            '<div class="w-name">' + esc(w.name) + '</div>' +
            '<span class="w-tag pending">미완료</span>' +
          '</div>'
        );
      }).join('');
      return (
        '<div class="home-group">' +
          '<div class="home-group-head">' +
            '<span class="home-group-title">' + esc(g.name) + '</span>' +
            '<button class="home-jump" data-home-jump="' + g.id + '|weekly">게임으로 이동 →</button>' +
          '</div>' +
          '<div class="weekly-list">' + rows + '</div>' +
        '</div>'
      );
    }).join('');

    var charGroups = state.games.map(function(g){
      var pending = g.characters.filter(function(c){ return !c.completed; });
      if(pending.length === 0) return '';
      var rows = pending.map(function(c){
        var openItems = c.items.filter(function(it){ return !it.done; }).length;
        var metaText = c.items.length ? (openItems + '/' + c.items.length + ' 항목 남음') : '체크리스트 없음';
        return (
          '<div class="home-char-row">' +
            '<div class="home-char-name">' + esc(c.name) + '</div>' +
            '<div class="home-char-meta mono">' + metaText + '</div>' +
            '<button class="toggle-done" data-home-toggle-char="' + g.id + '|' + c.id + '">완료로 표시</button>' +
          '</div>'
        );
      }).join('');
      return (
        '<div class="home-group">' +
          '<div class="home-group-head">' +
            '<span class="home-group-title">' + esc(g.name) + '</span>' +
            '<button class="home-jump" data-home-jump="' + g.id + '|chars">게임으로 이동 →</button>' +
          '</div>' +
          '<div class="home-char-list">' + rows + '</div>' +
        '</div>'
      );
    }).join('');

    return (
      stats +
      '<h3 class="home-section-title">이번 주 남은 컨텐츠</h3>' +
      (weeklyGroups || '<div class="empty small"><p>모든 게임의 주간 컨텐츠를 완료했어요 🎉</p></div>') +
      '<h3 class="home-section-title">육성이 필요한 캐릭터</h3>' +
      (charGroups || '<div class="empty small"><p>모든 캐릭터의 육성이 끝났어요 🎉</p></div>')
    );
  }

  function renderCharsTab(game){
    var filter = game.charFilter || 'all';
    var list = game.characters.filter(function(c){
      if(filter === 'pending') return !c.completed;
      if(filter === 'done') return c.completed;
      return true;
    });

    var addBar =
      '<div class="add-char-bar">' +
        '<input type="text" id="new-char-name" placeholder="캐릭터 이름 입력 후 Enter">' +
        '<button class="btn primary" id="btn-add-char">추가</button>' +
      '</div>';

    var filterRow =
      '<div class="filter-row">' +
        ['all','pending','done'].map(function(f){
          var label = f==='all'?'전체':f==='pending'?'진행중':'완료';
          return '<button class="filter-btn' + (filter===f?' active':'') + '" data-char-filter="' + f + '">' + label + '</button>';
        }).join('') +
        '<div class="filter-spacer"></div>' +
      '</div>';

    if(game.characters.length === 0){
      return addBar + filterRow +
        '<div class="empty"><h3>캐릭터가 없어요</h3><p>위에서 캐릭터 이름을 입력해 추가해 보세요.</p></div>';
    }
    if(list.length === 0){
      return addBar + filterRow +
        '<div class="empty"><h3>해당하는 캐릭터가 없어요</h3><p>다른 필터를 선택해 보세요.</p></div>';
    }

    var cards = list.map(function(c){
      var items = c.items.map(function(it){
        return (
          '<div class="item-row' + (it.done?' checked':'') + '">' +
            '<input type="checkbox" data-char="' + c.id + '" data-item="' + it.id + '"' + (it.done?' checked':'') + '>' +
            '<span>' + esc(it.text) + '</span>' +
            '<button class="item-del" data-del-item="' + c.id + '|' + it.id + '">✕</button>' +
          '</div>'
        );
      }).join('');

      return (
        '<div class="char-card' + (c.completed?' done':'') + '">' +
          '<div class="char-head">' +
            '<div class="char-name">' + esc(c.name) + '</div>' +
            '<div class="char-head-actions">' +
              (c.completed ? '<div class="stamp">완료</div>' : '') +
              '<button class="char-del" data-del-char="' + c.id + '" title="캐릭터 삭제">✕</button>' +
            '</div>' +
          '</div>' +
          '<div class="item-list">' + (items || '<div style="font-size:12px;color:var(--text-muted)">체크리스트 항목이 없어요</div>') + '</div>' +
          '<div class="add-item-row">' +
            '<input type="text" placeholder="육성 항목 추가 (예: 무기 레벨, 에코 세팅)" data-new-item="' + c.id + '">' +
            '<button data-add-item="' + c.id + '">추가</button>' +
          '</div>' +
          '<div class="char-foot">' +
            '<button class="toggle-done' + (c.completed?' is-done':'') + '" data-toggle-char="' + c.id + '">' +
              (c.completed ? '✓ 육성 완료' : '더 이상 파밍 안 해도 됨으로 표시') +
            '</button>' +
          '</div>' +
        '</div>'
      );
    }).join('');

    return addBar + filterRow + '<div class="char-grid">' + cards + '</div>';
  }

  function renderWeeklyTab(game){
    var wk = weekKeyFor(game.resetDay);
    var daysLeft = daysUntilReset(game.resetDay);
    var config =
      '<div class="weekly-config">' +
        '리셋 요일: ' +
        '<select id="reset-day">' +
          WEEKDAYS.map(function(w,i){
            return '<option value="' + i + '"' + (game.resetDay===i?' selected':'') + '>매주 ' + w + '요일</option>';
          }).join('') +
        '</select>' +
        '<span class="mono">· 다음 리셋까지 ' + daysLeft + '일</span>' +
      '</div>';

    var addBar =
      '<div class="add-weekly-bar">' +
        '<input type="text" id="new-weekly-name" placeholder="주간 컨텐츠 이름 입력 후 Enter">' +
        '<button class="btn primary" id="btn-add-weekly">추가</button>' +
      '</div>';

    if(game.weekly.length === 0){
      return config + addBar +
        '<div class="empty"><h3>등록된 주간 컨텐츠가 없어요</h3><p>보스, 던전, 이벤트 등 매주 반복되는 컨텐츠를 추가해 보세요.</p></div>';
    }

    var rows = game.weekly.map(function(w){
      var done = w.doneWeekKey === wk;
      return (
        '<div class="weekly-row' + (done?' done':'') + '">' +
          '<input type="checkbox" data-weekly="' + w.id + '"' + (done?' checked':'') + '>' +
          '<div class="w-name">' + esc(w.name) + '</div>' +
          '<span class="w-tag ' + (done?'done':'pending') + '">' + (done?'완료':'미완료') + '</span>' +
          '<button class="item-del" data-del-weekly="' + w.id + '">✕</button>' +
        '</div>'
      );
    }).join('');

    return config + addBar + '<div class="weekly-list">' + rows + '</div>';
  }

  function renderPartyTab(game){
    var parties = game.parties || [];
    var addBtn = '<button class="btn primary" id="btn-add-party" style="margin-bottom:16px;">+ 파티 추가</button>';

    if(game.characters.length === 0){
      return '<div class="empty"><h3>먼저 캐릭터를 등록해 주세요</h3><p>파티를 구성하려면 캐릭터 육성 탭에서 캐릭터를 추가해야 해요.</p></div>';
    }
    if(parties.length === 0){
      return addBtn + '<div class="empty"><h3>구성된 파티가 없어요</h3><p>여러 파티가 필요한 컨텐츠에 대비해 파티를 미리 짜볼 수 있어요.</p></div>';
    }

    var blocks = parties.map(function(p){
      var slotsHtml = p.slots.map(function(slot, idx){
        var options = '<option value="">비어있음</option>' + game.characters.map(function(c){
          return '<option value="' + c.id + '"' + (slot===c.id?' selected':'') + '>' + esc(c.name) + '</option>';
        }).join('');
        return (
          '<div class="slot">' +
            '<div class="slot-label">슬롯 ' + (idx+1) + '</div>' +
            '<select data-party="' + p.id + '" data-slot-idx="' + idx + '">' + options + '</select>' +
          '</div>'
        );
      }).join('');
      return (
        '<div class="party-block">' +
          '<div class="party-head">' +
            '<input type="text" value="' + esc(p.name) + '" data-rename-party="' + p.id + '">' +
            '<button class="party-del" data-del-party="' + p.id + '">파티 삭제</button>' +
          '</div>' +
          '<div class="party-slots">' + slotsHtml + '</div>' +
        '</div>'
      );
    }).join('');

    return addBtn + blocks;
  }

  // ---------- events ----------
  function bindEvents(){
    var exportBtn = document.getElementById('btn-export');
    if(exportBtn) exportBtn.addEventListener('click', exportBackup);
    var importInput = document.getElementById('btn-import');
    if(importInput) importInput.addEventListener('change', function(){
      if(importInput.files && importInput.files[0]) importBackup(importInput.files[0]);
      importInput.value = '';
    });
    var resetAllBtn = document.getElementById('btn-reset-all');
    if(resetAllBtn) resetAllBtn.addEventListener('click', resetAll);

    root.querySelectorAll('#btn-add-game, #btn-add-game-empty, #btn-add-game-home').forEach(function(el){
      el.addEventListener('click', addGame);
    });

    var goHomeBtn = document.getElementById('btn-go-home');
    if(goHomeBtn) goHomeBtn.addEventListener('click', function(){
      state.view = 'home';
      render();
    });

    root.querySelectorAll('[data-select-game]').forEach(function(el){
      el.addEventListener('click', function(){
        state.selectedGameId = el.getAttribute('data-select-game');
        state.view = 'game';
        state.tab = 'chars';
        render();
      });
    });
    root.querySelectorAll('[data-del-game]').forEach(function(el){
      el.addEventListener('click', function(e){
        e.stopPropagation();
        var id = el.getAttribute('data-del-game');
        var g = state.games.find(function(x){return x.id===id;});
        if(g && !confirm('"' + g.name + '" 게임과 그 안의 모든 데이터를 삭제할까요?')) return;
        state.games = state.games.filter(function(x){ return x.id !== id; });
        if(state.selectedGameId === id){
          state.selectedGameId = state.games[0] ? state.games[0].id : null;
          if(!state.selectedGameId) state.view = 'home';
        }
        saveData(); render();
      });
    });

    root.querySelectorAll('[data-tab]').forEach(function(el){
      el.addEventListener('click', function(){
        state.tab = el.getAttribute('data-tab');
        render();
      });
    });

    root.querySelectorAll('[data-home-weekly]').forEach(function(el){
      el.addEventListener('change', function(){
        var parts = el.getAttribute('data-home-weekly').split('|');
        var g = state.games.find(function(x){return x.id===parts[0];});
        if(!g) return;
        var w = g.weekly.find(function(x){return x.id===parts[1];});
        if(!w) return;
        w.doneWeekKey = el.checked ? weekKeyFor(g.resetDay) : null;
        saveData(); render();
      });
    });
    root.querySelectorAll('[data-home-toggle-char]').forEach(function(el){
      el.addEventListener('click', function(){
        var parts = el.getAttribute('data-home-toggle-char').split('|');
        var g = state.games.find(function(x){return x.id===parts[0];});
        if(!g) return;
        var c = g.characters.find(function(x){return x.id===parts[1];});
        if(c){ c.completed = true; saveData(); render(); }
      });
    });
    root.querySelectorAll('[data-home-jump]').forEach(function(el){
      el.addEventListener('click', function(){
        var parts = el.getAttribute('data-home-jump').split('|');
        state.selectedGameId = parts[0];
        state.view = 'game';
        state.tab = parts[1];
        render();
      });
    });

    var game = (state.view === 'game') ? getGame() : null;
    if(!game) return;

    // characters tab
    var addCharBtn = document.getElementById('btn-add-char');
    if(addCharBtn) addCharBtn.addEventListener('click', function(){ addCharacter(game); });
    var newCharInput = document.getElementById('new-char-name');
    if(newCharInput) newCharInput.addEventListener('keydown', function(e){
      if(e.key === 'Enter') addCharacter(game);
    });

    root.querySelectorAll('[data-char-filter]').forEach(function(el){
      el.addEventListener('click', function(){
        game.charFilter = el.getAttribute('data-char-filter');
        render();
      });
    });

    root.querySelectorAll('[data-toggle-char]').forEach(function(el){
      el.addEventListener('click', function(){
        var c = game.characters.find(function(x){return x.id===el.getAttribute('data-toggle-char');});
        if(c){ c.completed = !c.completed; saveData(); render(); }
      });
    });
    root.querySelectorAll('[data-del-char]').forEach(function(el){
      el.addEventListener('click', function(){
        game.characters = game.characters.filter(function(x){return x.id!==el.getAttribute('data-del-char');});
        saveData(); render();
      });
    });
    root.querySelectorAll('[data-char][data-item]').forEach(function(el){
      el.addEventListener('change', function(){
        var c = game.characters.find(function(x){return x.id===el.getAttribute('data-char');});
        if(!c) return;
        var it = c.items.find(function(x){return x.id===el.getAttribute('data-item');});
        if(it){ it.done = el.checked; saveData(); render(); }
      });
    });
    root.querySelectorAll('[data-del-item]').forEach(function(el){
      el.addEventListener('click', function(){
        var parts = el.getAttribute('data-del-item').split('|');
        var c = game.characters.find(function(x){return x.id===parts[0];});
        if(c){ c.items = c.items.filter(function(x){return x.id!==parts[1];}); saveData(); render(); }
      });
    });
    root.querySelectorAll('[data-add-item]').forEach(function(el){
      el.addEventListener('click', function(){ addItem(game, el.getAttribute('data-add-item')); });
    });
    root.querySelectorAll('[data-new-item]').forEach(function(el){
      el.addEventListener('keydown', function(e){
        if(e.key === 'Enter') addItem(game, el.getAttribute('data-new-item'));
      });
    });

    // weekly tab
    var resetDaySel = document.getElementById('reset-day');
    if(resetDaySel) resetDaySel.addEventListener('change', function(){
      game.resetDay = parseInt(resetDaySel.value, 10);
      saveData(); render();
    });
    var addWeeklyBtn = document.getElementById('btn-add-weekly');
    if(addWeeklyBtn) addWeeklyBtn.addEventListener('click', function(){ addWeekly(game); });
    var newWeeklyInput = document.getElementById('new-weekly-name');
    if(newWeeklyInput) newWeeklyInput.addEventListener('keydown', function(e){
      if(e.key === 'Enter') addWeekly(game);
    });
    root.querySelectorAll('[data-weekly]').forEach(function(el){
      el.addEventListener('change', function(){
        var w = game.weekly.find(function(x){return x.id===el.getAttribute('data-weekly');});
        if(!w) return;
        w.doneWeekKey = el.checked ? weekKeyFor(game.resetDay) : null;
        saveData(); render();
      });
    });
    root.querySelectorAll('[data-del-weekly]').forEach(function(el){
      el.addEventListener('click', function(){
        game.weekly = game.weekly.filter(function(x){return x.id!==el.getAttribute('data-del-weekly');});
        saveData(); render();
      });
    });

    // party tab
    var addPartyBtn = document.getElementById('btn-add-party');
    if(addPartyBtn) addPartyBtn.addEventListener('click', function(){
      game.parties = game.parties || [];
      game.parties.push({ id: uid(), name: '파티 ' + (game.parties.length+1), slots: [null,null,null,null] });
      saveData(); render();
    });
    root.querySelectorAll('[data-del-party]').forEach(function(el){
      el.addEventListener('click', function(){
        game.parties = game.parties.filter(function(x){return x.id!==el.getAttribute('data-del-party');});
        saveData(); render();
      });
    });
    root.querySelectorAll('[data-rename-party]').forEach(function(el){
      el.addEventListener('change', function(){
        var p = game.parties.find(function(x){return x.id===el.getAttribute('data-rename-party');});
        if(p){ p.name = el.value.trim() || p.name; saveData(); }
      });
    });
    root.querySelectorAll('[data-party][data-slot-idx]').forEach(function(el){
      el.addEventListener('change', function(){
        var p = game.parties.find(function(x){return x.id===el.getAttribute('data-party');});
        if(!p) return;
        var idx = parseInt(el.getAttribute('data-slot-idx'), 10);
        p.slots[idx] = el.value || null;
        saveData(); render();
      });
    });
  }

  function addGame(){
    var name = prompt('추가할 게임 이름을 입력하세요 (예: 명조, 젠존제)');
    if(!name || !name.trim()) return;
    var g = { id: uid(), name: name.trim(), resetDay: 1, characters: [], weekly: [], parties: [], charFilter: 'all' };
    state.games.push(g);
    state.selectedGameId = g.id;
    state.view = 'game';
    state.tab = 'chars';
    saveData(); render();
  }

  function addCharacter(game){
    var input = document.getElementById('new-char-name');
    var name = input.value.trim();
    if(!name) return;
    game.characters.push({ id: uid(), name: name, completed: false, items: [] });
    input.value = '';
    saveData(); render();
  }

  function addItem(game, charId){
    var input = document.querySelector('[data-new-item="' + charId + '"]');
    if(!input) return;
    var text = input.value.trim();
    if(!text) return;
    var c = game.characters.find(function(x){return x.id===charId;});
    if(!c) return;
    c.items.push({ id: uid(), text: text, done: false });
    saveData(); render();
  }

  function addWeekly(game){
    var input = document.getElementById('new-weekly-name');
    var name = input.value.trim();
    if(!name) return;
    game.weekly.push({ id: uid(), name: name, doneWeekKey: null });
    saveData(); render();
  }

  loadData();
})();
