translit 1.2
=========
Using example:
<pre>
XmlTranslitDictionary dictionary = new XmlTranslitDictionary("cyrillic_default.xml");
TranslitDocument document = TranslitDocument.parse(dictionary, "SCH'i da kasha - pisch'a nasha!", TranslitDictionary.Side.RIGHT);
System.out.println(document.getString(TranslitDictionary.Side.LEFT));
</pre>
Output:
<pre>
Щи да каша - пища наша!
</pre>
