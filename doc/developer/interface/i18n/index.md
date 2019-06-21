---
layout: default
---


Internationalization
==========================================================

## Default Internationalization
Default internationalization can be set in the `src/main/resources/configuration.properties` file via the `default.locale` property. This value should be set to the string representation (eg. `en` for `Locale.ENGLISH`) for the locale you want to set.

All internationalization terms should be put into the appropriate language messages bundle which can be found in `src/main/resources/i18n/messages_[locale].properties`. 

## Within HTML Templates

[Thymeleaf](https://www.thymeleaf.org) template engine is responsible for templating internationalized strings directly within HTML templates.  Please read the [Thymeleaf docs](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html). 

## Within JavaScript External Files

 Because we use [Webpack](../webpack) to compile and minify JavaScript assets, Thymeleaf cannot be used to internationalize (or any other templating) since the syntax in not proper JavaScript.
 
 Instead, we have created a webpack plugin called `i18nPropertiesWebpackPlugin` to handle client side internationalization.  This works by going through all the webpack entries and looking for all function calls `i18n("term.to.translate")`, the argument to this method is the the string key in the messages file.
 
 ***NOTE:** Do not import the `i18n` method into the JavaScript file, webpack handles this dynamically*.
 
 **In `messages_en.properties`**:
 
 ```
feature_name_title=Interesting Modal
 ```
 
 **In JavaScript file that needs the translation**
 ```js
 i18n("feature_name_title");
 ```
 
 For each entry, webpack will gather the key from the JavaScript file, along with the translation from the messages file and create a new JavaScript file `[entry_name].[locale].js`.  This should be added to the HTML template above the script tag for the webpack bundle for that entry.  This file exposed a JSON object called `translations` to the `window` object which is consumed by the `i18n.js` loaded through the application.
 
 **Example**
 ```js
<script th:src="@{/dist/i18n/app.__${#locale.language}__.js}"></script>
```

Here, Thymeleaf will replace `__${#locale.language}__` with the required locale for the user (currently set in the `configuration.properties` file).
 