package org.romppu.translit.document;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: roman
 * Date: 26.10.2011
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDecisionMaker implements TranslitDocument.DecisionMaker {

    public TranslitDocument.Match checkMatchesAndReturnMatch(TranslitDocument.ParserContext context) {
        Iterator<TranslitDocument.Match> iterator = context.getCurrentMatchSet().iterator();
        int longest = 0;
        TranslitDocument.Match toDecide = context.getCurrentMatchSet().first();
        while (iterator.hasNext()) {
            TranslitDocument.Match match = iterator.next();
            if (match.getStringPart().length() > longest) toDecide = match;
        }
        return toDecide;
    }
}
