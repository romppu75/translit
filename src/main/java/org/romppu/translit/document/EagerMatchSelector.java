package org.romppu.translit.document;

import java.util.Iterator;

/**
 * Default implementation of {@link MatchSelector}
 */
public class EagerMatchSelector implements MatchSelector {

    /**
     * Selects longest match from specified parsing context.
     * @param context
     * @return
     */
    public TranslitDocument.Match selectMatch(TranslitDocument.ParsingContext context) {
        Iterator<TranslitDocument.Match> iterator = context.getCurrentMatchSet().iterator();
        int longest = 0;
        TranslitDocument.Match selection = context.getCurrentMatchSet().first();
        while (iterator.hasNext()) {
            TranslitDocument.Match match = iterator.next();
            if (match.getStringPart().length() > longest) selection = match;
        }
        return selection;
    }
}
