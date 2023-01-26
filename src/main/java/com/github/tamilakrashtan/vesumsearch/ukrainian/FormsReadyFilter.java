package com.github.tamilakrashtan.vesumsearch.ukrainian;

import com.github.tamilakrashtan.vesumsearch.grammar.Form;
import com.github.tamilakrashtan.vesumsearch.grammar.FormType;
import com.github.tamilakrashtan.vesumsearch.grammar.Paradigm;
import com.github.tamilakrashtan.vesumsearch.grammar.Variant;
import com.github.tamilakrashtan.vesumsearch.utils.SetUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Filters only those variants and forms which it makes sense to show to the user
 */
public class FormsReadyFilter {

    public static List<Form> getAcceptedForms(Paradigm p, Variant v) {
        if (v.getForm().isEmpty()) {
            return null;
        }
        String tag = SetUtils.tag(p, v);
        if (tag.startsWith("K")) {
            return null;
        }
//        Set<String> dct = SetUtils.getDictionaries(v.getDictionaries()).keySet();
        boolean use = true;
//        boolean use = !dct.isEmpty();
//        if (!SetUtils.getDictionaries(v.getForm().get(0).getDictionaries()).isEmpty()) {
//            use = true;
//        }
//        if (tag.startsWith("NP")) {
//            use = true;
//        }
//        RegulationType reg = v.getRegulation() != null ? v.getRegulation() : p.getRegulation();
//        if (reg != null) {
//            switch (reg) {
//            case ADD:
//                use = true;
//                break;
//            case MISTAKE:
//            case FANTASY:
//            case UNDESIRABLE:
//            case LIMITED:
//            case OBSCENISM:
//            case INVECTIVE:
//                use = false;
//                break;
//            case RARE:
//            case RARE_BRANCH:
//                switch (mode) {
//                case SPELL:
//                    use = false;
//                    break;
//                case SHOW:
//                    use = true;
//                    break;
//                }
//                break;
//            }
//        }
        if (!use) {
            return null;
        }
        Stream<Form> result = v.getForm().stream();
//        Stream<Form> result;
//        if (SetUtils.hasOrthography(v, "A2008")) {
//            result = v.getForm().stream();
//        } else if (v.getOrthography() == null) {
//            result = v.getForm().stream().filter(f -> SetUtils.hasOrthography(f, "A2008"));
//        } else {
//            return null;
//        }
        List<Form> r = result.filter(standardForms).filter(f -> !f.getValue().isEmpty()).collect(Collectors.toList());
        return r.isEmpty() ? null : r;
    }

    private static Predicate<Form> standardForms = (f) -> (f.getType() == null || f.getType() == FormType.NUMERAL);
}
