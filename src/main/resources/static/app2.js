// TODO: go through this file and remove parts not used in the dictionary search
var GrammarService = /** @class */ (function () {
    function GrammarService() {
        var _this = this;
        this.inputWord = new WordRequest();
        this.inputOrder = "STANDARD"; // "STANDARD" | "REVERSE";
        grammarui.showStatus("Читаються початкові дані...");
        fetch('grammar/initial')
            .then(function (r) {
            if (!r.ok) {
                grammarui.showError("Помилка запиту початкових даних: " + r.status + " " + r.statusText);
            }
            else {
                r.json().then(function (json) {
                    _this.initial = json;
                    localization = _this.initial.localization[document.documentElement.lang];
                    grammarui.hideStatusError();
                    _this.afterInit();
                });
            }
        })
            .catch(function (err) { return grammarui.showError("Помилка: " + err); });
    }
    GrammarService.prototype.afterInit = function () {
        var ps = window.location.hash;
        if (ps.charAt(0) == '#') {
            ps = ps.substring(1);
        }
        var data;
        try {
            data = JSON.parse(decodeURI(ps));
        }
        catch (error) {
            data = null;
        }
        grammarui.restoreToScreen(data);
        $('#grammarStat').html($.templates("#template-grammar-stat").render({
            initial: this.initial
        }));
    };
    GrammarService.prototype.search = function () {
        var _this = this;
        $('#desc').hide();
        $('#grammarStat').hide();
        var rq = grammarui.collectFromScreen();
        window.location.hash = '#' + stringify(rq);
        grammarui.showStatus("Шукаємо...");
        fetch('grammar/search', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(rq)
        })
            .then(function (r) {
            if (!r.ok) {
                grammarui.showError("Помилка пошуку: " + r.status + " " + r.statusText);
            }
            else {
                r.json().then(function (json) {
                    _this.result = json;
                    if (_this.result.error) {
                        grammarui.showError(_this.result.error);
                    }
                    else {
                        grammarui.showOutput(rq.grammar, rq.orderReverse && !rq.outputGrouping);
                        if (_this.result.output.length > 0) {
                            grammarui.hideStatusError();
                        }
                        else if (_this.result.hasMultiformResult) {
                            grammarui.showError("Нічого не знайдено, але таке слово є серед форм. Спробуйте пошукати по всіх формах.");
                        }
                        else {
                            grammarui.showError("Нічого не знайдено");
                        }
                    }
                });
            }
        })
            .catch(function (err) { return grammarui.showError("Помилка: " + err); });
    };
    GrammarService.prototype.loadDetails = function (pdgId) {
        grammarui.showStatus("Шукаємо...");
        var url;
        var elemFull = document.getElementById("grammarword-full");
        if (elemFull != null && elemFull.checked) {
            url = "grammar/detailsFull/" + pdgId;
        }
        else {
            url = "grammar/details/" + pdgId;
        }
        fetch(url)
            .then(function (r) {
            if (!r.ok) {
                grammarui.showError("Помилка пошуку: " + r.status + " " + r.statusText);
            }
            else {
                r.json().then(function (json) {
                    grammarui.hideStatusError();
                    dialogGrammarDB = new DialogGrammarDB(json);
                });
            }
        })
            .catch(function (err) { return grammarui.showError("Помилка: " + err); });
    };
    return GrammarService;
}());
var GrammarRequest = /** @class */ (function () {
    function GrammarRequest() {
        this.multiForm = false; // пошук по формах ?
    }
    return GrammarRequest;
}());
var GrammarUI = /** @class */ (function () {
    function GrammarUI() {
        this.hideStatusError();
    }
    GrammarUI.prototype.showError = function (err) {
        console.log("Error: " + err);
        $('#status').hide();
        document.getElementById("error").innerText = err;
        $('#error').show();
    };
    GrammarUI.prototype.showStatus = function (s) {
        $('#error').hide();
        document.getElementById("status").innerText = s;
        $('#status').show();
    };
    GrammarUI.prototype.collectFromScreen = function () {
        var r = new GrammarRequest();
        r.multiForm = document.getElementById('grammarword-multiform').checked;
        r.word = document.getElementById('grammarword-word').value.toLowerCase();
        if ($('#grammar-show-grammardetails').is(":visible")) {
            r.grammar = document.getElementById("grammarword-grammar").innerText;
        }
        if ($('#grammar-show-order').is(":visible")) {
            r.orderReverse = document.getElementById("inputOrderReverse").checked;
        }
        if ($('#grammar-show-forms').is(":visible")) {
            r.outputGrammar = document.getElementById("grammarwordshow-grammar").innerText;
        }
        if ($('#grammar-show-grouping').is(":visible")) {
            r.outputGrouping = document.getElementById("inputGrouping").checked;
        }
        r.fullDatabase = document.getElementById("grammarword-full").checked;
        return r;
    };
    GrammarUI.prototype.restoreToScreen = function (data) {
        if (data) {
            document.getElementById("grammarword-word").value = data.word ? data.word : "";
            document.getElementById("grammarword-multiform").checked = data.multiForm;
            if (data.grammar) {
                document.getElementById("grammarword-grammar").innerText = data.grammar;
                DialogWordGrammar.wordGrammarToText(data.grammar, document.getElementById("grammarword-grammarshow"));
                this.showExtended = true;
            }
            if (data.outputGrammar) {
                document.getElementById("grammarwordshow-grammar").innerText = data.outputGrammar;
                DialogWordGrammar.wordGrammarToText(data.outputGrammar, document.getElementById("grammarwordshow-grammarshow"));
                this.showExtended = true;
            }
            if (data.orderReverse) {
                $("#inputOrderReverse").prop('checked', true);
                this.showExtended = true;
            }
            if (data.fullDatabase) {
                $('#grammar-show-all').show();
                $("#grammarword-full").prop('checked', true);
            }
            this.visibilityChange();
        }
        var gr = this;
        new MutationObserver(function (mutationsList, observer) {
            document.getElementById('grammarwordshow-grammar').innerText = '';
            document.getElementById('grammarwordshow-grammarshow').innerText = '---';
            gr.visibilityChange();
        }).observe(document.getElementById('grammarword-grammar'), { childList: true });
        new MutationObserver(function (mutationsList, observer) {
            gr.visibilityChange();
        }).observe(document.getElementById('grammarwordshow-grammar'), { childList: true });
    };
    GrammarUI.prototype.showOutput = function (requestGrammar, reverse) {
        this.showExtended = true;
        this.responseHasDuplicateParadigms = grammarService.result.hasDuplicateParadigms;
        $('#output').html($.templates("#template-grammaroutput").render({
            lemmas: grammarService.result.output,
            reverse: reverse
        }));
        this.visibilityChange();
    };
    GrammarUI.prototype.hideStatusError = function () {
        $('#status').hide();
        $('#error').hide();
    };
    GrammarUI.prototype.resetControls = function () {
        this.showExtended = false;
        document.getElementById("grammarword-grammar").innerText = '';
        document.getElementById("grammarword-grammarshow").innerHTML = "---";
        $('#grammar-show-order').hide();
        $("#inputOrderStandard").prop('checked', true);
        $('#grammar-show-forms').hide();
        document.getElementById("grammarwordshow-grammar").innerText = '';
        document.getElementById("grammarword-grammarshow").innerHTML = "---";
        $('#grammar-show-grouping').hide();
        $('#grammar-show-noforms').hide();
        this.visibilityChange();
    };
    GrammarUI.prototype.visibilityChange = function () {
        if (this.showExtended) {
            $('#grammar-show-order').show();
            $('#grammar-show-reset').show();
        }
        if (this.responseHasDuplicateParadigms) {
            $('#grammar-show-grouping').show();
        }
        else {
            $('#grammar-show-grouping').hide();
        }
        if ($('#grammar-show-grouping').is(":visible") && document.getElementById("inputGrouping").checked) {
            $('#grammar-show-order').hide();
        }
        var grammar = document.getElementById('grammarword-grammar').innerText;
        if ($('#grammar-show-grammardetails').is(":visible") && grammar && DialogWordGrammar.hasFormTags(grammar)) {
            $('#grammar-show-forms').show();
            $('#grammar-show-noforms').hide();
        }
        else if (this.showExtended) {
            $('#grammar-show-forms').hide();
            $('#grammar-show-noforms').show();
        }
    };
    return GrammarUI;
}());
var OutGrammarParadigm = /** @class */ (function () {
    function OutGrammarParadigm() {
    }
    return OutGrammarParadigm;
}());
var OutGrammarVariant = /** @class */ (function () {
    function OutGrammarVariant() {
    }
    return OutGrammarVariant;
}());
var OutGrammarForm = /** @class */ (function () {
    function OutGrammarForm() {
    }
    return OutGrammarForm;
}());
var grammarui = null;
var grammarService = null;
var localization = null;
var dialogGrammarDB = null;
function initializeGrammarPage() {
    grammarui = new GrammarUI();
    grammarService = new GrammarService();
    document.onkeydown = function (e) {
        if (e.key == 'F2') {
            $('#grammar-show-all').show();
        }
    };
}
var SearchRequest = /** @class */ (function () {
    function SearchRequest() {
    }
    return SearchRequest;
}());
var SearchResult = /** @class */ (function () {
    function SearchResult() {
    }
    return SearchResult;
}());
var SearchTotalResult = /** @class */ (function () {
    function SearchTotalResult() {
    }
    return SearchTotalResult;
}());
var SentencesRequest = /** @class */ (function () {
    function SentencesRequest() {
    }
    return SentencesRequest;
}());
$.views.settings.allowCode(true);
$.views.converters("roundnum", roundnum);
$.views.converters("json", function (val) {
    return JSON.stringify(val);
});
$.views.converters("titleShort", function (str) {
    var s = str.toString();
    if (s.length == 0) {
        return "<...>";
    }
    else if (s.length > 20) {
        return s.substring(0, 17) + "...";
    }
    else {
        return s;
    }
});
$('body')
    .on('mousedown', '.popover', function (e) {
    e.preventDefault();
});
$.views.converters("wordtail", function (val) {
    if (val) {
        return val.replace('<', '&lt;').replace('>', '&gt;').replace('\n', '<br/>').replace('{', '<b style="background-color: #CCF">{').replace('}', '}</b>');
    }
    else {
        return "";
    }
});
$.views.converters("korpusname", function (val) {
    return val.replace(/\|\|.+/g, '');
});
$.views.converters("korpusdesc", function (val) {
    return val.replace(/.+\|\|/g, '');
});
$.views.converters("naciski", function (val) {
    return val != null ? val.replaceAll("+", "\u0301") : val;
});
var korpusui = null;
var korpusService = null;
var localization = null;
var dialogSources = null;
var dialogSubcorpuses = null;
var dialogText = null;
var dialogStyleGenres = null;
var dialogWordGrammar = null;
var spisy = {};
var PopoverPlaceVertical;
(function (PopoverPlaceVertical) {
    PopoverPlaceVertical[PopoverPlaceVertical["TOP"] = 0] = "TOP";
    PopoverPlaceVertical[PopoverPlaceVertical["BOTTOM"] = 1] = "BOTTOM";
})(PopoverPlaceVertical || (PopoverPlaceVertical = {}));
;
var PopoverPlaceHorizontal;
(function (PopoverPlaceHorizontal) {
    PopoverPlaceHorizontal[PopoverPlaceHorizontal["LEFT"] = 0] = "LEFT";
    PopoverPlaceHorizontal[PopoverPlaceHorizontal["CENTER"] = 1] = "CENTER";
    PopoverPlaceHorizontal[PopoverPlaceHorizontal["RIGHT"] = 2] = "RIGHT";
})(PopoverPlaceHorizontal || (PopoverPlaceHorizontal = {}));
;
var BasePopover = /** @class */ (function () {
    function BasePopover() {
    }
    BasePopover.prototype.showAt = function (elem, id, placeVertical, placeHorizontal) {
        var popover = document.getElementById(id);
        var spanPlace = this.getOffsetRect(elem);
        var popoverSize = popover.getBoundingClientRect();
        switch (placeVertical) {
            case PopoverPlaceVertical.TOP:
                popover.style.top = (spanPlace.top - popoverSize.height) + 'px';
                break;
            case PopoverPlaceVertical.BOTTOM:
                popover.style.top = spanPlace.top + 'px';
                break;
        }
        switch (placeHorizontal) {
            case PopoverPlaceHorizontal.LEFT:
                popover.style.left = (spanPlace.left - popoverSize.width) + 'px';
                break;
            case PopoverPlaceHorizontal.CENTER:
                popover.style.left = (spanPlace.left + spanPlace.width / 2 - popoverSize.width / 2) + 'px';
                break;
            case PopoverPlaceHorizontal.RIGHT:
                popover.style.left = (spanPlace.left) + 'px';
                break;
        }
        popover.style.visibility = 'visible';
        popover.focus();
    };
    BasePopover.prototype.getOffsetRect = function (elem) {
        var box = elem.getBoundingClientRect();
        var body = document.body;
        var docElem = document.documentElement;
        var scrollTop = window.pageYOffset || docElem.scrollTop || body.scrollTop;
        var scrollLeft = window.pageXOffset || docElem.scrollLeft || body.scrollLeft;
        var clientTop = docElem.clientTop || body.clientTop || 0;
        var clientLeft = docElem.clientLeft || body.clientLeft || 0;
        var top = box.top + scrollTop - clientTop;
        var left = box.left + scrollLeft - clientLeft;
        return { top: Math.round(top), left: Math.round(left), height: box.height, width: box.width };
    };
    return BasePopover;
}());
/*
 * Паказвае інфармацыю пра слова, разам з табліцай граматычных форм.
 */
var DialogGrammarDB = /** @class */ (function () {
    function DialogGrammarDB(data) {
        var _this = this;
        $('#dialog-grammardb-details').html($.templates("#template-grammardb").render({
            details: this.convert(data),
            getter: this.getter,
            log: this.log
        }));
        $('[data-toggle="tooltip"]').tooltip();
        this.hideBlock('tr.gr-p1');
        this.hideBlock('tr.gr-p2');
        this.hideBlock('tr.gr-p3');
        this.hideBlock('tr.gr-p4');
        this.hideBlock('tr.gr-p5');
        // merge the same cells vertically
        $('#dialog-grammardb table').each(function (i, e) { return _this.mergeCellsVertically(e); });
        $('#dialog-grammardb').modal('show');
    }
    DialogGrammarDB.prototype.hideBlock = function (cls) {
        var hide = true;
        $(cls).each(function (idx, e) {
            if ($(e).find('w').length) {
                hide = false;
            }
        });
        if (hide) {
            $(cls).remove();
        }
    };
    DialogGrammarDB.prototype.mergeCellsVertically = function (e) {
        for (var r = e.rows.length - 1; r > 0; r--) {
            for (var c = e.rows[r - 1].cells.length - 1; c >= 0; c--) {
                var v1 = e.rows[r - 1].cells[c];
                var v2 = e.rows[r].cells[c];
                if (v1 && v2 && v1.classList.contains('grtag') && v2.classList.contains('grtag') && v1.innerHTML == v2.innerHTML) {
                    e.rows[r - 1].cells[c].rowSpan += e.rows[r].cells[c].rowSpan;
                    e.rows[r].deleteCell(c);
                }
            }
        }
    };
    DialogGrammarDB.prototype.onClose = function () {
        $('#dialog-grammardb').modal('hide');
    };
    DialogGrammarDB.prototype.getter = function (spec, v) {
        var rq = Array();
        if (spec) {
            for (var _i = 0, _a = spec.split(';'); _i < _a.length; _i++) {
                var r = _a[_i];
                var m = r.match("(.+):(.)");
                if (m) {
                    var kv = new KeyValue();
                    kv.key = m[1];
                    kv.value = m[2];
                    rq.push(kv);
                }
                else {
                    throw "Wrong form specification: " + spec;
                }
            }
        }
        var result = Array();
        for (var fi = 0; fi < v.sourceForms.length; fi++) {
            var f = v.sourceForms[fi];
            var descr = Grammar.getCodes(grammarService.initial, v.tag + f.tag);
            var passed = true;
            for (var _b = 0, rq_1 = rq; _b < rq_1.length; _b++) {
                var kv = rq_1[_b];
                if (descr[kv.key] !== kv.value) {
                    passed = false;
                    break;
                }
            }
            if (passed) {
                v.sourceFormsUsageCount[fi]++;
                var suffix = '';
                if (f.options === 'ANIM') {
                    suffix += ' <span class="grtag">(іст.)</span>';
                }
                else if (f.options === 'INANIM') {
                    suffix += ' <span class="grtag">(неіст.)</span>';
                }
                if (f.type === 'NUMERAL') {
                    suffix += ' <span class="grtag">(2,3,4)</span>';
                }
                result.push(f.value + suffix);
            }
        }
        return result.length ? '<w>' + result.join('<br/>') + '</w>' : '–';
    };
    DialogGrammarDB.prototype.log = function (v) {
        var r = Array();
        for (var fi = 0; fi < v.sourceForms.length; fi++) {
            if (v.sourceFormsUsageCount[fi] == 0) {
                r.push(v.sourceForms[fi].value + ": не показано");
            }
            else if (v.sourceFormsUsageCount[fi] > 1) {
                r.push(v.sourceForms[fi].value + ": декілька разів");
            }
        }
        return r.join(", ");
    };
    DialogGrammarDB.prototype.convert = function (p) {
        var r = new OutGrammarParadigm();
        r.lemma = p.lemma;
        r.meaning = p.meaning;
        r.variants = [];
        var _loop_1 = function (v) {
            var rv = new OutGrammarVariant();
            rv.tag = (p.tag != null ? p.tag : "") + (v.tag != null ? v.tag : "");
            var skip = Grammar.getSkipParts(grammarService.initial, rv.tag);
            rv.catText = Grammar.parseCode(grammarService.initial, rv.tag).filter(function (kv) { return skip.indexOf(kv.key) < 0; });
            rv.subtree = Grammar.subtree(rv.tag, grammarService.initial.grammarTree);
            rv.forms = [];
            rv.catnames = [];
            rv.dictionaries = [];
            for (var _b = 0, _c = grammarService.initial.slouniki; _b < _c.length; _b++) {
                var sl = _c[_b];
                if (v.dictionaries.indexOf(sl.name) >= 0) {
                    rv.dictionaries.push(sl);
                }
            }
            for (var _d = 0, _e = v.forms; _d < _e.length; _d++) {
                var f = _e[_d];
                var gr = rv.subtree;
                for (var _f = 0, _g = f.tag.split(''); _f < _g.length; _f++) {
                    var c = _g[_f];
                    if (gr) {
                        var g = gr[c];
                        if (g) {
                            if (rv.catnames.indexOf(g.name) < 0 && skip.indexOf(g.name) < 0) {
                                rv.catnames.push(g.name);
                            }
                            gr = g.ch;
                        }
                    }
                    else {
                        break;
                    }
                }
            }
            rv.sourceForms = v.forms;
            rv.sourceFormsUsageCount = Array();
            for (var _h = 0, _j = rv.sourceForms; _h < _j.length; _h++) {
                var f = _j[_h];
                rv.sourceFormsUsageCount.push(0);
            }
            for (var _k = 0, _l = v.forms; _k < _l.length; _k++) {
                var f = _l[_k];
                var rf = new OutGrammarForm();
                rf.value = f.value;
                rf.data = [];
                rf.colspan = [];
                var gr = rv.subtree;
                for (var _m = 0, _o = f.tag.split(''); _m < _o.length; _m++) {
                    var c = _o[_m];
                    if (gr) {
                        var g = gr[c];
                        if (g) {
                            var idx = rv.catnames.indexOf(g.name);
                            rf.data[idx] = g.desc;
                            gr = g.ch;
                        }
                    }
                    else {
                        break;
                    }
                }
                for (var i = 0; i < rv.catnames.length; i++) {
                    if (rf.data[i]) {
                    }
                    else {
                        rf.data[i] = "";
                    }
                }
                rv.forms.push(rf);
            }
            for (var i = 0; i < rv.catnames.length; i++) {
                for (var fi = 0; fi < rv.forms.length; fi++) {
                    rv.forms[fi].colspan[i] = 1;
                }
                for (var fi = rv.forms.length - 1; fi > 0; fi--) {
                    if (rv.forms[fi].data[i] == rv.forms[fi - 1].data[i]) {
                        rv.forms[fi - 1].colspan[i] += rv.forms[fi].colspan[i];
                        rv.forms[fi].colspan[i] = 0;
                    }
                }
            }
            r.variants.push(rv);
        };
        for (var _i = 0, _a = p.variants; _i < _a.length; _i++) {
            var v = _a[_i];
            _loop_1(v);
        }
        return r;
    };
    return DialogGrammarDB;
}());
var DialogList = /** @class */ (function () {
    function DialogList(list, controlId, title) {
        this.controlId = controlId;
        var selectedSubcorpuses = document.getElementById('inputFilterCorpus').innerText.split(';');
        var fullList = [];
        if (!selectedSubcorpuses) {
            selectedSubcorpuses = Object.keys(list);
        }
        for (var _i = 0, selectedSubcorpuses_1 = selectedSubcorpuses; _i < selectedSubcorpuses_1.length; _i++) {
            var s = selectedSubcorpuses_1[_i];
            if (list[s]) {
                fullList = fullList.concat(list[s]);
            }
        }
        var BE = new Intl.Collator('be');
        fullList = fullList.filter(function (item, index) {
            return (fullList.indexOf(item) == index);
        }).sort(function (a, b) { return BE.compare(a, b); });
        var html = $.templates("#template-list").render({
            list: fullList,
            title: title
        });
        $('#dialog-list-place').html(html);
        var fs = document.getElementById(this.controlId).innerText.split(';');
        fs.forEach(function (v) {
            var cb = document.querySelector("#dialog-list input[type='checkbox'][name='" + v + "']");
            if (cb) {
                cb.checked = true;
            }
        });
        $('#dialog-list').modal('show');
    }
    DialogList.prototype.onOk = function () {
        var collection = document.querySelectorAll("#dialog-list input[type='checkbox']");
        var result = [];
        var hasUnchecked = false;
        collection.forEach(function (cb) {
            if (cb.checked) {
                result.push(cb.name);
            }
            else {
                hasUnchecked = true;
            }
        });
        if (!hasUnchecked) {
            result = [];
        }
        document.getElementById(this.controlId).innerText = result.length == 0 ? "Усе" : result.join(';');
        $('#dialog-list').modal('hide');
    };
    DialogList.prototype.onCancel = function () {
        $('#dialog-list').modal('hide');
    };
    DialogList.prototype.all = function (v) {
        var collection = document.querySelectorAll("#dialog-list input[type='checkbox']");
        collection.forEach(function (cb) { return cb.checked = v; });
    };
    DialogList.prototype.filter = function (elem) {
        var s = elem.value.trim().toLowerCase();
        var collection = document.querySelectorAll("#dialog-list input[type='checkbox']");
        collection.forEach(function (cb) {
            cb.closest('div').style.display = cb.checked || cb.name.toLowerCase().indexOf(s) >= 0 ? "block" : "none";
        });
    };
    return DialogList;
}());
var DialogText = /** @class */ (function () {
    function DialogText(row, target) {
        var html = $.templates("#template-text").render({
            biblio: row.doc,
            detailsText: row.origText
        });
        $('#dialog-text').html(html);
        $('#dialog-text').modal('show');
        korpusui.visitedList.push(row.docId);
        target.classList.add('visited');
    }
    DialogText.prototype.cancel = function () {
        $('#dialog-text').modal('hide');
    };
    return DialogText;
}());
/*
 * Паказвае граматычныя тэгі для фільтрацыі граматыкі.
 */
var DialogWordGrammar = /** @class */ (function () {
    function DialogWordGrammar(inner) {
        this.currentWordElement = inner.closest(".word-select");
    }
    DialogWordGrammar.createParadigmTagsDialog = function (inner, showFormGroups) {
        var result = new DialogWordGrammar(inner);
        result.hideParadigmGroups = false;
        result.hideFormGroups = !showFormGroups;
        var grammar = result.currentWordElement.querySelector(".wordgram-grammar-string").textContent;
        var html = $.templates("#template-wordgrammar").render({
            grammar: DialogWordGrammar.getInitial(),
            hideParadigmGroups: result.hideParadigmGroups,
            hideFormGroups: result.hideFormGroups
        });
        $('#dialog-wordgrammar-place').html(html);
        if (grammar) {
            document.getElementById("dialog-wordgrammar-grselected").value = grammar.charAt(0);
            result.changeType(grammar.charAt(0));
            DialogWordGrammar.textToWordGrammar(grammar, grammar, true, false);
        }
        $('#dialog-wordgrammar').modal('show');
        return result;
    };
    DialogWordGrammar.createFormOnlyTagsDialog = function (inner, paradigmSelectedSpanId) {
        var result = new DialogWordGrammar(inner);
        result.hideParadigmGroups = true;
        result.hideFormGroups = false;
        var paradigmGrammar = document.getElementById(paradigmSelectedSpanId).textContent;
        var grammar = result.currentWordElement.querySelector(".wordgram-grammar-string").textContent;
        var html = $.templates("#template-wordgrammar").render({
            grammar: DialogWordGrammar.getInitial(),
            hideParadigmGroups: result.hideParadigmGroups,
            hideFormGroups: result.hideFormGroups
        });
        $('#dialog-wordgrammar-place').html(html);
        document.getElementById("dialog-wordgrammar-grselected").value = paradigmGrammar.charAt(0);
        result.changeType(paradigmGrammar.charAt(0));
        DialogWordGrammar.textToWordGrammar(paradigmGrammar, grammar, true, true);
        $('#dialog-wordgrammar').modal('show');
        return result;
    };
    DialogWordGrammar.prototype.changeType = function (v) {
        var html = v ? $.templates("#template-wordgrammar2").render({
            grammar: DialogWordGrammar.getInitial(),
            grselected: v,
            hideParadigmGroups: this.hideParadigmGroups,
            hideFormGroups: this.hideFormGroups
        }) : "";
        $('#dialog-wordgrammar-place2').html(html);
    };
    DialogWordGrammar.prototype.onOk = function () {
        var gr = this.toString();
        this.currentWordElement.querySelector(".wordgram-grammar-string").textContent = gr;
        DialogWordGrammar.wordGrammarToText(gr, this.currentWordElement.querySelector(".wordgram-display"));
        $('#dialog-wordgrammar').modal('hide');
    };
    DialogWordGrammar.prototype.onCancel = function () {
        $('#dialog-wordgrammar').modal('hide');
    };
    DialogWordGrammar.prototype.toString = function () {
        var grselectedElement = document.getElementById("dialog-wordgrammar-grselected");
        var grselected = grselectedElement.value;
        if (!grselected) {
            return null;
        }
        var r = grselected;
        var db = DialogWordGrammar.getInitial().grammarWordTypesGroups[grselected];
        // усе пазначаныя checkboxes пераносім у Map<groupname,listofletters>
        var checked = {};
        for (var _i = 0, _a = db.groups; _i < _a.length; _i++) {
            var group = _a[_i];
            checked[group.name] = "";
        }
        var cbs = document.querySelectorAll("#dialog-wordgrammar-place input[type='checkbox']:checked");
        cbs.forEach(function (cb) {
            checked[cb.name] += cb.value;
        });
        // ствараем радок grammar regexp
        for (var _b = 0, _c = db.groups; _b < _c.length; _b++) {
            var group = _c[_b];
            var r1 = checked[group.name];
            if (r1.length == 0 || r1.length == group.items.length) {
                r1 = '.';
            }
            else if (r1.length > 1) {
                r1 = '[' + r1 + ']';
            }
            r += r1;
        }
        return r;
    };
    DialogWordGrammar.getInitial = function () {
        return grammarService.initial;
    };
    DialogWordGrammar.wordGrammarToText = function (grammar, outputElement) {
        if (grammar) {
            var p = grammar.charAt(0);
            var display = DialogWordGrammar.getInitial().grammarTree[p].desc;
            if (!/^\.*$/.test(grammar.substring(1))) {
                display += ',&nbsp;...';
            }
            outputElement.innerHTML = display;
            var describeFormsTagsOnly = outputElement.classList.contains('wordgram-display-formsonly');
            outputElement.title = DialogWordGrammar.textToWordGrammar(grammar, grammar, false, describeFormsTagsOnly);
        }
        else {
            outputElement.innerHTML = '---';
        }
    };
    DialogWordGrammar.hasFormTags = function (partName) {
        var db = DialogWordGrammar.getInitial().grammarWordTypesGroups[partName.charAt(0)];
        for (var _i = 0, _a = db.groups; _i < _a.length; _i++) {
            var group = _a[_i];
            if (group.formGroup) {
                return true;
            }
        }
        return false;
    };
    DialogWordGrammar.textToWordGrammar = function (partName, grammar, showinUI, describeFormsTagsOnly) {
        var db = DialogWordGrammar.getInitial().grammarWordTypesGroups[partName.charAt(0)];
        var checked = {};
        for (var _i = 0, _a = db.groups; _i < _a.length; _i++) {
            var group = _a[_i];
            checked[group.name] = "";
        }
        var info = "";
        var grindex = 0;
        for (var i = 1; i < grammar.length; i++) {
            var group = db.groups[grindex];
            var groupValues = DialogWordGrammar.getGroupValues(group);
            var ch = grammar.charAt(i);
            if (ch == '[') {
                var valuesList = [];
                for (i++; i < grammar.length; i++) {
                    ch = grammar.charAt(i);
                    if (ch == ']') {
                        break;
                    }
                    else {
                        checked[group.name] += ch;
                        valuesList.push(groupValues[ch]);
                    }
                }
                if (group.formGroup || !describeFormsTagsOnly) {
                    info += group.name + ': ' + valuesList + '\n';
                }
            }
            else if (ch == '.') {
            }
            else {
                checked[group.name] += ch;
                if (group.formGroup || !describeFormsTagsOnly) {
                    info += group.name + ': ' + groupValues[ch] + '\n';
                }
            }
            grindex++;
        }
        if (showinUI) {
            var cbs = document.querySelectorAll("#dialog-wordgrammar-place input[type='checkbox']");
            cbs.forEach(function (cb) {
                cb.checked = checked[cb.name].indexOf(cb.value) >= 0;
            });
        }
        return info.trim();
    };
    DialogWordGrammar.getGroupValues = function (group) {
        var r = {};
        for (var _i = 0, _a = group.items; _i < _a.length; _i++) {
            var item = _a[_i];
            r[item.code] = item.description;
        }
        return r;
    };
    return DialogWordGrammar;
}());
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        if (typeof b !== "function" && b !== null)
            throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var PopoverBiblio = /** @class */ (function (_super) {
    __extends(PopoverBiblio, _super);
    function PopoverBiblio(event, doc, page) {
        var _this = _super.call(this) || this;
        var html = $.templates("#template-biblio").render({ doc: doc, page: page });
        $('#dialog-biblio-details').html(html);
        var popover = document.getElementById('dialog-biblio');
        _this.showAt(event.target, 'dialog-biblio', PopoverPlaceVertical.TOP, PopoverPlaceHorizontal.RIGHT);
        return _this;
    }
    PopoverBiblio.prototype.leave = function () {
        $('#dialog-biblio').modal('hide');
    };
    return PopoverBiblio;
}(BasePopover));
var PopoverWord = /** @class */ (function (_super) {
    __extends(PopoverWord, _super);
    function PopoverWord(event) {
        var _this = _super.call(this) || this;
        if (event.target) {
            var wlemma = event.target.getAttribute("wlemma");
            var wcat = event.target.getAttribute("wcat");
            var popover = document.getElementById('popoverWord');
            if (wlemma && wcat) {
                document.getElementById('popoverWordContent').innerHTML = _this.html(wlemma, wcat);
                _this.showAt(event.target, 'popoverWord', PopoverPlaceVertical.TOP, PopoverPlaceHorizontal.CENTER);
            }
            else {
                popover.style.visibility = 'hidden';
            }
        }
        return _this;
    }
    PopoverWord.prototype.html = function (lemma, cat) {
        var o = "";
        if (lemma) {
            var lemmas = lemma.split(';');
            o += '<b>Пачатковая форма</b>:&nbsp;' + lemmas.join(', ');
        }
        if (cat) {
            o += '<br/><b>Граматыка</b>:';
            for (var _i = 0, _a = cat.split(';'); _i < _a.length; _i++) {
                var c = _a[_i];
                o += '<br/>' + c + ':&nbsp;';
                var oo = Grammar.parseCode(korpusService.initial.grammar, c);
                if (oo) {
                    o += oo.map(function (kv) { return kv.value; }).join(', ').replace(' ', '&nbsp;');
                }
            }
        }
        return o;
    };
    return PopoverWord;
}(BasePopover));
function popoverWordHide() {
    var popover = document.getElementById('popoverWord');
    popover.style.visibility = 'hidden';
}
var GrammarInitial = /** @class */ (function () {
    function GrammarInitial() {
    }
    return GrammarInitial;
}());
var GrammarInitialStat = /** @class */ (function () {
    function GrammarInitialStat() {
    }
    return GrammarInitialStat;
}());
var GrammarDict = /** @class */ (function () {
    function GrammarDict() {
    }
    return GrammarDict;
}());
var GrammarLetter = /** @class */ (function () {
    function GrammarLetter() {
    }
    return GrammarLetter;
}());
var KeyValue = /** @class */ (function () {
    function KeyValue() {
    }
    return KeyValue;
}());
var DBTagsGroups = /** @class */ (function () {
    function DBTagsGroups() {
    }
    return DBTagsGroups;
}());
var Group = /** @class */ (function () {
    function Group() {
    }
    return Group;
}());
var Item = /** @class */ (function () {
    function Item() {
    }
    return Item;
}());
var InitialData = /** @class */ (function () {
    function InitialData() {
    }
    return InitialData;
}());
var InitialDataStat = /** @class */ (function () {
    function InitialDataStat() {
    }
    return InitialDataStat;
}());
var GrammarSearchResult = /** @class */ (function () {
    function GrammarSearchResult() {
    }
    return GrammarSearchResult;
}());
var LemmaInfo = /** @class */ (function () {
    function LemmaInfo() {
    }
    return LemmaInfo;
}());
var LemmaParadigm = /** @class */ (function () {
    function LemmaParadigm() {
    }
    return LemmaParadigm;
}());
var LemmaVariant = /** @class */ (function () {
    function LemmaVariant() {
    }
    return LemmaVariant;
}());
var LemmaForm = /** @class */ (function () {
    function LemmaForm() {
    }
    return LemmaForm;
}());
var BaseParams = /** @class */ (function () {
    function BaseParams() {
        this.textStandard = new StandardTextRequest();
    }
    return BaseParams;
}());
var SearchParams = /** @class */ (function (_super) {
    __extends(SearchParams, _super);
    function SearchParams() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    return SearchParams;
}(BaseParams));
var ClusterParams = /** @class */ (function (_super) {
    __extends(ClusterParams, _super);
    function ClusterParams() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    return ClusterParams;
}(BaseParams));
var SearchResults = /** @class */ (function () {
    function SearchResults(o) {
        this.docId = o.docId;
        this.doc = o.doc;
        this.text = new Paragraph();
        this.text.page = o.text.page;
        this.text.sentences = o.text.sentences;
    }
    return SearchResults;
}());
var ResultKwicOutRow = /** @class */ (function () {
    function ResultKwicOutRow(o) {
        this.doc = o.doc;
        this.origText = o.text;
    }
    return ResultKwicOutRow;
}());
var ResultKwicOut = /** @class */ (function () {
    function ResultKwicOut(o, wordsInRequest) {
        this.rows = [];
        for (var _i = 0, o_1 = o; _i < o_1.length; _i++) {
            var orow = o_1[_i];
            var r = new SearchResults(orow);
            for (var i = 0; i < r.text.sentences.length; i++) {
                for (var j = 0; j < r.text.sentences[i].words.length; j++) {
                    if (r.text.sentences[i].words[j].requestedWord && j + wordsInRequest < r.text.sentences[i].words.length) {
                        // show
                        var row = new ResultKwicOutRow(r);
                        var wordsTo = this.showRow(row, r.text.sentences[i], j, wordsInRequest);
                        this.rows.push(row);
                        j = wordsTo;
                    }
                }
            }
        }
    }
    ResultKwicOut.prototype.showRow = function (out, sentence, wordsFrom, wordsCount) {
        var wordsTo = wordsFrom;
        out.kwicBefore = [];
        for (var i = wordsFrom - 1, count = 0; i >= 0 && count < 5; i--) {
            out.kwicBefore.push(sentence.words[i]);
            if (sentence.words[i].normalized) {
                count++;
            }
        }
        out.kwicBefore.reverse();
        out.kwicWords = [];
        for (var i = wordsFrom, count = 0; i < sentence.words.length && count < wordsCount; i++) {
            out.kwicWords.push(sentence.words[i]);
            if (sentence.words[i].normalized) {
                count++;
                wordsTo = i;
            }
        }
        out.kwicAfter = [];
        for (var i = wordsTo + 1, count = 0; i < sentence.words.length && count < 5; i++) {
            out.kwicAfter.push(sentence.words[i]);
            if (sentence.words[i].normalized) {
                count++;
            }
        }
        return wordsTo;
    };
    return ResultKwicOut;
}());
var ResultSearchOutRow = /** @class */ (function () {
    function ResultSearchOutRow(o) {
        this.words = [];
        this.docId = o.docId;
        this.doc = o.doc;
        this.origText = o.text;
    }
    return ResultSearchOutRow;
}());
var ResultSearchOut = /** @class */ (function () {
    function ResultSearchOut(o, wordsInRequest) {
        this.rows = [];
        for (var _i = 0, o_2 = o; _i < o_2.length; _i++) {
            var orow = o_2[_i];
            var r = new SearchResults(orow);
            var num = this.getRequestedWordsCountInResult(r.text);
            var wordsCount = void 0;
            switch (num) {
                case 0:
                case 1:
                    wordsCount = 5;
                    break;
                case 2:
                    wordsCount = 3;
                    break;
                case 3:
                    wordsCount = 2;
                    break;
                default:
                    wordsCount = 2;
                    break;
            }
            var out = new ResultSearchOutRow(r);
            this.outputText(r.text, out, wordsCount, 0, " ... ");
            this.rows.push(out);
        }
    }
    ResultSearchOut.prototype.outputText = function (words, row, wordAround, sentencesAround, separatorText) {
        var begin = new TextPos(words, 0, 0);
        var end = new TextPos(words, words.sentences.length - 1, words.sentences[words.sentences.length - 1].words.length - 1);
        var pos = this.getNextRequestedWordPosAfter(words, null);
        var currentAroundFrom = pos.addWords(-wordAround);
        if (sentencesAround != 0) {
            currentAroundFrom = currentAroundFrom.addSequences(-sentencesAround);
        }
        var currentAroundTo = pos.addWords(wordAround);
        if (sentencesAround != 0) {
            currentAroundTo = currentAroundTo.addSequences(sentencesAround);
        }
        if (currentAroundFrom.after(begin)) {
            row.words.push(new WordResult(separatorText));
        }
        while (true) {
            var next = this.getNextRequestedWordPosAfter(words, pos);
            if (next == null) {
                break;
            }
            var nextAroundFrom = next.addWords(-wordAround);
            if (sentencesAround != 0) {
                nextAroundFrom = nextAroundFrom.addSequences(-sentencesAround);
            }
            var nextAroundTo = next.addWords(wordAround);
            if (sentencesAround != 0) {
                nextAroundTo = nextAroundTo.addSequences(sentencesAround);
            }
            if (currentAroundTo.addWords(2).after(nextAroundFrom)) {
                // merge
                currentAroundTo = nextAroundTo;
            }
            else {
                this.output(words, row, currentAroundFrom, currentAroundTo);
                row.words.push(new WordResult(separatorText));
                currentAroundFrom = nextAroundFrom;
                currentAroundTo = nextAroundTo;
            }
            pos = next;
        }
        this.output(words, row, currentAroundFrom, currentAroundTo);
        if (end.after(currentAroundTo)) {
            row.words.push(new WordResult(separatorText));
        }
    };
    ResultSearchOut.prototype.output = function (words, row, from, to) {
        var curr = from;
        while (true) {
            var w = words.sentences[curr.sentence].words[curr.word];
            row.words.push(w);
            var next = curr.addWords(1);
            if (curr.equals(to)) {
                break;
            }
            curr = next;
        }
    };
    ResultSearchOut.prototype.getRequestedWordsCountInResult = function (words) {
        var count = 0;
        for (var _i = 0, _a = words.sentences; _i < _a.length; _i++) {
            var row = _a[_i];
            for (var _b = 0, _c = row.words; _b < _c.length; _b++) {
                var w = _c[_b];
                if (w.requestedWord) {
                    count++;
                }
            }
        }
        return count;
    };
    ResultSearchOut.prototype.getNextRequestedWordPosAfter = function (words, currentPos) {
        var startI, startJ;
        if (currentPos == null) {
            startI = 0;
            startJ = 0;
        }
        else {
            var next = currentPos.addWords(1);
            if (next.equals(currentPos)) {
                return null;
            }
            startI = next.sentence;
            startJ = next.word;
        }
        var j = startJ;
        for (var i = startI; i < words.sentences.length; i++) {
            for (; j < words.sentences[i].words.length; j++) {
                if (words.sentences[i].words[j].requestedWord) {
                    return new TextPos(words, i, j);
                }
            }
            j = 0;
        }
        return null;
    };
    return ResultSearchOut;
}());
var LatestMark = /** @class */ (function () {
    function LatestMark() {
        this.doc = null;
        this.score = null;
        this.fields = null;
        this.shardIndex = null;
    }
    return LatestMark;
}());
var TextInfo = /** @class */ (function () {
    function TextInfo() {
    }
    return TextInfo;
}());
var ClusterResult = /** @class */ (function () {
    function ClusterResult(r) {
        this.rows = r;
    }
    return ClusterResult;
}());
var ClusterRow = /** @class */ (function () {
    function ClusterRow() {
    }
    return ClusterRow;
}());
var FreqSpisResult = /** @class */ (function () {
    function FreqSpisResult() {
    }
    return FreqSpisResult;
}());
var StandardTextRequest = /** @class */ (function () {
    function StandardTextRequest() {
    }
    return StandardTextRequest;
}());
var WordRequest = /** @class */ (function () {
    function WordRequest() {
        this.word = "";
        this.grammar = null;
    }
    return WordRequest;
}());
var WordResult = /** @class */ (function () {
    function WordResult(text) {
        this.text = text;
        this.normalized = text;
    }
    WordResult.prototype.getOutput = function () {
        return this.source != null ? this.source : this.normalized;
    };
    return WordResult;
}());
var Sentence = /** @class */ (function () {
    function Sentence() {
    }
    return Sentence;
}());
var Paragraph = /** @class */ (function () {
    function Paragraph() {
    }
    return Paragraph;
}());
var Grammar = /** @class */ (function () {
    function Grammar() {
    }
    Grammar.getSkipParts = function (data, cat) {
        var skip = data.skipGrammar[cat.charAt(0)];
        return skip ? skip : [];
    };
    Grammar.parseCode = function (data, cat) {
        var gr = data.grammarTree;
        var r = [];
        for (var _i = 0, _a = cat.split(''); _i < _a.length; _i++) {
            var c = _a[_i];
            var g = gr[c];
            if (g) {
                var kv = new KeyValue();
                kv.key = g.name;
                kv.value = g.desc;
                r.push(kv);
                gr = g.ch;
                if (!gr) {
                    break;
                }
            }
            else {
                break;
            }
        }
        return r;
    };
    Grammar.getCodes = function (data, cat) {
        var gr = data.grammarTree;
        var r = {};
        for (var _i = 0, _a = cat.split(''); _i < _a.length; _i++) {
            var c = _a[_i];
            var g = gr[c];
            if (g) {
                r[g.name] = c;
                gr = g.ch;
                if (!gr) {
                    break;
                }
            }
            else {
                break;
            }
        }
        return r;
    };
    Grammar.subtree = function (cat, grammarTree) {
        var gr = grammarTree;
        for (var _i = 0, _a = cat.split(''); _i < _a.length; _i++) {
            var c = _a[_i];
            var g = gr[c];
            if (g) {
                gr = g.ch;
                if (!gr) {
                    return null;
                }
            }
            else {
                return gr;
            }
        }
        return gr;
    };
    return Grammar;
}());
function stringify(v) {
    return JSON.stringify(v, function (key, value) {
        if (value)
            return value;
    });
}
function fulltrim(s) {
    if (s) {
        s = s.trim();
    }
    return s ? s : null;
}
function roundnum(v) {
    var f = new Intl.NumberFormat('be', { maximumSignificantDigits: 3 });
    if (v < 1000) {
        return v.toString();
    }
    else if (v < 990000) {
        return "~" + f.format(v / 1000) + " тис";
    }
    else if (v < 990000000) {
        return "~" + f.format(v / 1000000) + " млн";
    }
    else {
        return "~" + f.format(v / 1000000000) + " млрд";
    }
}
/**
 * Mathematics for word position processing in text(paragraph).
 */
var TextPos = /** @class */ (function () {
    function TextPos(text, sentence, word) {
        this.text = text;
        this.sentence = sentence;
        this.word = word;
    }
    TextPos.prototype.addWords = function (count) {
        var r = new TextPos(this.text, this.sentence, this.word);
        if (count >= 0) {
            for (var i = 0; i < count; i++) {
                r.nextWord();
            }
        }
        else {
            for (var i = 0; i < -count; i++) {
                r.prevWord();
            }
        }
        return r;
    };
    TextPos.prototype.addSequences = function (count) {
        var newSentence = this.sentence + count;
        var newWord;
        if (count >= 0) {
            if (newSentence >= this.text.sentences.length) {
                newSentence = this.text.sentences.length - 1;
            }
            newWord = this.text[newSentence].length - 1;
        }
        else {
            if (newSentence < 0) {
                newSentence = 0;
            }
            newWord = 0;
        }
        return new TextPos(this.text, newSentence, newWord);
    };
    TextPos.prototype.nextWord = function () {
        if (this.word + 1 < this.text.sentences[this.sentence].words.length) {
            this.word++;
        }
        else if (this.sentence + 1 < this.text.sentences.length) {
            this.sentence++;
            this.word = 0;
        }
    };
    TextPos.prototype.prevWord = function () {
        if (this.word > 0) {
            this.word--;
        }
        else if (this.sentence > 0) {
            this.sentence--;
            this.word = this.text.sentences[this.sentence].words.length - 1;
        }
    };
    TextPos.prototype.normalize = function () {
        while (true) {
            if (this.sentence >= 0 && this.sentence < this.text.sentences.length && this.word >= 0
                && this.word < this.text.sentences[this.sentence].words.length) {
                return;
            }
            if (this.sentence < 0) {
                this.sentence = 0;
                this.word = 0;
                return;
            }
            else if (this.sentence >= this.text.sentences.length) {
                this.sentence = this.text.sentences.length - 1;
                this.word = this.text.sentences[this.sentence].words.length - 1;
                return;
            }
            if (this.word >= this.text.sentences[this.sentence].words.length) {
                this.word -= this.text.sentences[this.sentence].words.length;
                this.sentence++;
                continue;
            }
            else if (this.word < 0) {
                this.sentence--;
                if (this.sentence >= 0) {
                    this.word += this.text.sentences[this.sentence].words.length;
                }
                continue;
            }
            throw new Error("Error TextPos.normalize");
        }
    };
    TextPos.prototype.equals = function (o) {
        return this.text == o.text && this.sentence == o.sentence && this.word == o.word;
    };
    TextPos.prototype.after = function (o) {
        if (this.sentence > o.sentence) {
            return true;
        }
        else if (this.sentence == o.sentence && this.word > o.word) {
            return true;
        }
        return false;
    };
    TextPos.min = function (o1, o2) {
        if (o1.sentence < o2.sentence) {
            return o1;
        }
        if (o1.sentence > o2.sentence) {
            return o2;
        }
        if (o1.word < o2.word) {
            return o1;
        }
        else {
            return o2;
        }
    };
    TextPos.max = function (o1, o2) {
        if (o1.sentence > o2.sentence) {
            return o1;
        }
        if (o1.sentence < o2.sentence) {
            return o2;
        }
        if (o1.word > o2.word) {
            return o1;
        }
        else {
            return o2;
        }
    };
    return TextPos;
}());
//# sourceMappingURL=app2.js.map