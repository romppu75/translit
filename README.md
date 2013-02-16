translit 2.2
=========
Usage example 1:
<pre>
TranslitDocument document = TranslitDocumentFactory.newInstance().newTranslitDocument();
document.insertAt(0, "SCH'i da kasha - pisch'a nasha.", TranslitDictionary.Side.RIGHT);
System.out.println(document.getString(TranslitDictionary.Side.LEFT));
</pre>

Usage example 2:
<pre>
TranslitDocument document = TranslitDocumentFactory.newInstance().newTranslitDocument(new XmlTranslitDictionary("/dictionary_def.xml"));
document.insertAt(0, "SCH'i da kasha - pisch'a nasha.", TranslitDictionary.Side.RIGHT);
System.out.println(document.getString(TranslitDictionary.Side.LEFT));
</pre>

Usage example 3:
<pre>
TranslitDocument document = new DefaultTranslitDocument(new XmlTranslitDictionary("/dictionary_def.xml"));
document.insertAt(0, "SCH'i da kasha - pisch'a nasha.", TranslitDictionary.Side.RIGHT);
System.out.println(document.getString(TranslitDictionary.Side.LEFT));
</pre>

Output:
<pre>
Щи да каша - пища наша.
</pre>
