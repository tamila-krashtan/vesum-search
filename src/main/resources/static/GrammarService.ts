class GrammarService {
  public error: string;
  public status: string;

  public initial: GrammarInitial;

  public inputOrder: string = "STANDARD"; // "STANDARD" | "REVERSE";

  public result: GrammarSearchResult;
  public details: LemmaParadigm;

  constructor() {
    grammarui.showStatus("Читаються початкові дані...");
    fetch('rest/grammar/initial')
      .then(r => {
        if (!r.ok) {
          grammarui.showError("Помилка запиту початкових даних: " + r.status + " " + r.statusText);
        } else {
          r.json().then(json => {
            this.initial = json;
            localization = this.initial.localization[document.documentElement.lang];
            grammarui.hideStatusError();
            this.afterInit();
          });
        }
      })
      .catch(err => grammarui.showError("Помилка: " + err));
  }
  afterInit() {
    let ps = window.location.hash;
    if (ps.charAt(0) == '#') {
      ps = ps.substring(1);
    }
    let data;
    try {
      data = JSON.parse(decodeURI(ps));
    } catch (error) {
      data = null;
    }
    grammarui.restoreToScreen(data);
    $('#grammarStat').html($.templates("#template-grammar-stat").render({
        initial: this.initial
    }));
  }

  search() {
    $('#desc').hide();
    $('#grammarStat').hide();
    let rq: GrammarRequest = grammarui.collectFromScreen();
    window.location.hash = '#' + stringify(rq);

    grammarui.showStatus("Шукаємо...");
    fetch('rest/grammar/search', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(rq)
    })
      .then(r => {
        if (!r.ok) {
          grammarui.showError("Помилка пошуку: " + r.status + " " + r.statusText);
        } else {
          r.json().then(json => {
            this.result = json;
            if (this.result.error) {
              grammarui.showError(this.result.error);
            } else {
              grammarui.showOutput(rq.grammar, rq.orderReverse && !rq.outputGrouping);
              if (this.result.output.length > 0) {
                grammarui.hideStatusError();
              } else if (this.result.hasMultiformResult) {
                grammarui.showError("Нічого не знайдено, але таке слово є серед форм. Спробуйте пошукати по всіх формах.");
              } else {
                grammarui.showError("Нічого не знайдено");
              }
            }
          });
        }
      })
      .catch(err => grammarui.showError("Помилка: " + err));
  }
  loadDetails(pdgId: number) {
    grammarui.showStatus("Шукаємо...");
    
    let url;
    let elemFull: HTMLInputElement = <HTMLInputElement>document.getElementById("grammarword-full");
    if (elemFull != null && elemFull.checked) {
        url = "rest/grammar/detailsFull/" + pdgId;
    } else {
        url = "rest/grammar/details/" + pdgId;
    }

    fetch(url)
      .then(r => {
        if (!r.ok) {
          grammarui.showError("Помилка пошуку: " + r.status + " " + r.statusText);
        } else {
          r.json().then(json => {
            grammarui.hideStatusError();
            dialogGrammarDB = new DialogGrammarDB(json);
          });
        }
      })
      .catch(err => grammarui.showError("Помилка: " + err));
  }
}

class GrammarRequest {
  public word: string; // тое што ўвёў карыстальнік
  public multiForm: boolean = false; // пошук па формах ?
  public grammar: string; // удакладненая граматыка
  public outputGrammar: string; // адмысловая форма
  public orderReverse: boolean; // адваротны парадак
  public outputGrouping: boolean; // групаваць
  public fullDatabase: boolean; // шукаць па ўсёй базе, не звяртаючы ўвагу на фільтры
}
