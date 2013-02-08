package org.romppu.translit.document.impl;

import org.romppu.translit.document.TranslitDocument;

/**
* This interface is used by the {@link org.romppu.translit.document.impl.DefaultTranslitDocument}
* in the  parsing phase to decide what match will be actually used when
* many matches will be found in the {@link org.romppu.translit.dictionary.TranslitDictionary}.
 */
public interface MatchSelectionStrategy {

    /**
     * Selects and returns appropriate match from the specified parsing context.
     * @param context
     * @return
     */
    public DefaultTranslitDocument.Match selectMatch(DefaultTranslitDocument.ParsingContext context);
}
