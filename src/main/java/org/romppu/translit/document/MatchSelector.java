package org.romppu.translit.document;

/**
 * This interface is used by the {@link TranslitDocument}
 * in the  parsing phase to decide what match will be actually used when
 * more than one matches was found in the {@link TranslitDictionary}.
 */
public interface MatchSelector {

    /**
     * Selects and returns appropriate match from the specified parsing context.
     * @param context
     * @return
     */
    public TranslitDocument.Match selectMatch(TranslitDocument.ParsingContext context);
}
