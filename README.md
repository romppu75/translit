translit
=========
Using example:
<pre>
XmlTranslitProfile profile = new XmlTranslitProfile("cyrillic_default.xml");
TranslitDocument document = TranslitDocument.parse(profile, "SCH'i da kasha - pisch'a nasha!", ITranslitProfile.Side.RIGHT);
System.out.println(document.getString(ITranslitProfile.Side.LEFT));
</pre>
Output:
<pre>
Щи да каша - пища наша!
</pre>
