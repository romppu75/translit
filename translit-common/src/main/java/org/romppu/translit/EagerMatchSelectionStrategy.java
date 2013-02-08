package org.romppu.translit;

import org.romppu.translit.document.TranslitDocument;

import java.util.Iterator;

/**
 * Default implementation of {@link MatchSelectionStrategy}
 * The EagerMatchSelectionStrategy selects the longest match.
 */
public class EagerMatchSelectionStrategy implements MatchSelectionStrategy {

    /**
     * Selects the longest match from the specified parsing context.
     * @param context
     * @return
     */
    public TranslitDocument.Match selectMatch(TranslitDocument.ParsingContext context) {
        Iterator<TranslitDocument.Match> iterator = context.currentMatchSet().iterator();
        int longest = 0;
        TranslitDocument.Match selection = context.currentMatchSet().first();
        while (iterator.hasNext()) {
            TranslitDocument.Match match = iterator.next();
            if (match.getStringPart().length() > longest) selection = match;
        }
        return selection;
    }
}
