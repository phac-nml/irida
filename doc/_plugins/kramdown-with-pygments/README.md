Kramdown with Pygments
========
A Jekyll plugin that enables Pygments syntax highlighting for Kramdown-parsed fenced code blocks.
Heavily based on [krampygs](https://github.com/navarroj/krampygs), but refactored to make it work with Jekyll 2.*.

In the standard setup, Jekyll Kramdown only works with Pygments for syntax highlighting
when you use Liquid tags. This plugin makes Kramdown also use Pygments when using 
fenced code blocks. That way you can use more Markdown and less Liquid. Yay!

## Usage

* Clone this project into your `_plugins` directory.
* Add the following lines to your `_config.yml`

```yaml
markdown: KramdownPygments
```

Fenced code blocks can now be syntax highlighted using the power of Pygments.


    ~~~php
    print "Hello World"
    ~~~
 

The same goes for inline code:

    You could also do something like this: `var foo = 'bar'`{:.language-javascript}. Amazing!

## Setting the default language
If you don't want to set the language for inline code blocks like that every time, 
you can define a global default language for the entire site in your `_config.yml`

```yaml
kramdown:
  default_lang: php
```

If you want to override that for a single page, add the following at the top of 
that page, but below the front-matter

```
{::options kramdown_default_lang="php" /}
```

## Options
This plugin supports all options that the original kramdown converter supports:

```yaml
kramdown:
  auto_ids: true
  footnote_nr: 1
  entity_output: as_char
  toc_levels: 1..6
  smart_quotes: lsquo,rsquo,ldquo,rdquo
  input: GFM
```

## Tested with

* kramdown 1.4.0
* pygments.rb 0.6.0
* jekyll 2.1.1
* ruby 2.0.0p451

## Thanks

This plugin is heavily based on [krampygs](https://github.com/navarroj/krampygs).
Thanks to [@navarroj](https://github.com/navarroj) for developing the original plugin.
