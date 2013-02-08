package org.romppu.translit.document.impl;

import org.romppu.translit.document.TranslitDocument;
import org.romppu.translit.document.impl.MatchSelectionStrategy;

import java.util.Iterator;

/**
 * Default implementation of {@link org.romppu.translit.document.impl.MatchSelectionStrategy}
 * The EagerMatchSelectionStrategy selects the longest match.
 */
public class EagerMatchSelectionStrategy implements MatchSelectionStrategy {

    /**
     * Selects the longest match from the specified parsing context.
     * @param context
     * @return
     */
    public DefaultTranslitDocument.Match selectMatch(DefaultTranslitDocument.ParsingContext context) {
        Iterator<DefaultTranslitDocument.Match> iterator = context.currentMatchSet().iterator();
        int longest = 0;
        DefaultTranslitDocument.Match selection = context.currentMatchSet().first();
        while (iterator.hasNext()) {
            DefaultTranslitDocument.Match match = iterator.next();
            if (match.getStringPart().length() > longest) selection = match;
        }
        return selection;
    }
}
