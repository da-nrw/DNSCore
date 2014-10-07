# Formatkonversion mit DNSCore

Formatkonversionen in DNSCore basieren auf einem Modell von [Konversionsrichtlinien](object_model.de.md#conversionpolicy---die-regel-zur-anwendung-einer-konversion) (ConversionPolicies) und [Konversionsroutinen](object_model.de.md#conversionroutine---die-konversionsroutine). Konversionsroutinen beschreiben ein Verfahren, mit dessen Hilfe eine Datei eines bestimmten Formates in ein anderes Zielformat konvertiert werden kann. Konversionsrichtlinien hingegen legen fest, welche Konversionsroutinen für Dateien mit bestimmten Dateiformaten durchzuführen sind, nachdem ebendiese Dateiformate vom System erkannt wurden.

![Bild](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_object_users.jpg)
