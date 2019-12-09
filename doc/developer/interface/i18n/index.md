---
layout: default
---

Internationalization for IRIDA
===============================

Adding internationalization's for IRIDA is an easy but tedious task, and a very helpful way to contribute to the IRIDA project.  Internationalization files can be found under the `/src/main/resources/i18n` path in the IRIDA source code.  To add your language, all of the messages under `/src/main/resources/i18n/messages.properties` should be translated to your language, then saved to a new file with the language code.  For example: English = `messages_en.properties`, French = `messages_fr.properties`.  See a full list of language codes at <https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes>.  Any messages which are not translated will fall back to the `messages.properties` file.

After translating the messages file, your language must be enabled in IRIDA's web configuration.  See the [web configuration guide](../../../administrator/web#web-configuration) for more.

## Within HTML Templates

[Thymeleaf](https://www.thymeleaf.org) template engine is responsible for templating internationalized strings directly within HTML templates.  

Adding the html attribute `th:text` (where `th` is the prefix for Thymeleaf, and `text`  means you want to directly add text) with the value set to the key in the translation messages file.  Thymeleaf will update the content of the html element with the text from the messages file.

###Example
In the Thymeleaf Template:
```html
<span th:text="${messages.key}">This should be translated</span>
```

Sent to the client:
```html
<span>The message file value</span>
```

Please read the [Thymeleaf docs](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html). 

## Within JavaScript External Files

[Webpack](../webpack) is used to compile and minify JavaScript assets.  Webpack's output cannot be parsed by the Thymeleaf JavaScript parser.  To handle internationalized strings within these files, we have created a webpack plugin called `i18nThymeleafWebpackPlugin`.  This works by going through all the webpack entries and looking for all function calls to `i18n("term.to.translate")`, the argument to this method is the the string key in the messages file.
 
 ***NOTE:** Do not import the `i18n` method into the JavaScript file, webpack handles this dynamically*.
 
 **In `messages.properties`**:
 
 ```
feature_name_title=Interesting Modal
 ```
 
 **In JavaScript file that needs the translation**
 ```js
 i18n("feature_name_title");
 ```
 
 For each entry, webpack will gather all the internationalization keys in the related files and create a new html file (`dist/i18n/[entry_name].html`) that contains a Thymeleaf `fragment` which contains a script tag with the strings to be internationalized. 
 
###Example output for the analyses listing page:

 ```html
<script id="analyses-translations" th:inline="javascript" th:fragment="i18n">
      window.translations = window.translations || [];
      window.translations.push({
        "analyses.analysis-name": /*[[#{analyses.analysis-name}]]*/ "",
        "analyses.state": /*[[#{analyses.state}]]*/ "",
        "analyses.type": /*[[#{analyses.type}]]*/ "",
        "analyses.submitter": /*[[#{analyses.submitter}]]*/ "",
        "analysis.duration": /*[[#{analysis.duration}]]*/ "",
        "analyses.delete-confirm": /*[[#{analyses.delete-confirm}]]*/ "",
        "analyses.delete": /*[[#{analyses.delete}]]*/ "",
        "analyses.header": /*[[#{analyses.header}]]*/ ""
      });
    </script>
```
  
 At runtime, Thymeleaf will processes the html template and look for JavaScript entry points, if one is found that contains an entry, Thymeleaf will look to see if there is a translations file available.  If one is found, Thymeleaf will inject the translations into the page immediately before the entry tag.  This will exposes a JSON object called `translations` to the `window` object which is consumed by the `i18n.js` loaded through the application.
