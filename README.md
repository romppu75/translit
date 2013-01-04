Using example
========
<pre>
XmlTranslitProfileImpl handler = new XmlTranslitProfileImpl("cyrillic_default.xml");
TranslitDocument document = TranslitDocument.parse(handler, "SCH'i da kasha - pisch'a nasha!", ITranslitProfile.Side.RIGHT);
System.out.println(document.getString(ITranslitProfile.Side.LEFT));
<pre>
Output
<pre>
Щи да каша - пища наша!
</pre>
