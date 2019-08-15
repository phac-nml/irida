---
layout: default
---

Internationalization for IRIDA
===============================

Adding internationalizations for IRIDA is an easy but tedious task, and a very helpful way to contribute to the IRIDA project.  Internationalization files can be found under the `/src/main/resources/i18n` path in the IRIDA source code.  To add your language, all of the messages under `/src/main/resources/i18n/messages.properties` should be translated to your language, then saved to a new file with the language code.  For example: English = `messages_en.properties`, French = `messages_fr.properties`.  See a full list of language codes at <https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes>.  Any messages which are not translated will fall back to the `messages.properties` file.

After translating the messages file, your language must be enabled in IRIDA's web configuration.  See the [web configuration guide](../../../administrator/web#web-configuration) for more.