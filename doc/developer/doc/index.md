---
layout: default
---

This guide describes how to write documentation for IRIDA.

* This comment becomes the table of contents
{:toc}

Prerequisites
-------------

We're using [Jekyll](http://jekyllrb.com/) to generate a static site for our documentation. Jekyll allows us to write documentation in a variety of different formats (i.e., markdown, HTML) and generates a nice, consistent set of output files.

You should install Jekyll as described on its web site: http://jekyllrb.com/docs/installation/

### Running `jekyll`

Once you've got Jekyll installed, from the root of the documentation directory (`$IRIDA/doc`) you can run:

    jekyll serve

Jekyll will run a web server on port 4000 that you can navigate to with your web browser. If you make changes to any files or add new files under the `/doc` directory, Jekyll with auto-compile the document and update the files that it's serving (how handy!).

Deploying documentation
-----------------------
You can create the complete deployable documentation package by running

    ./gradlew clean javadoc

from your root IRIDA directory, where pom.xml lives. Running this command will execute `jekyll` using the `_build-config.yml` file, and produce [JavaDoc](http://maven.apache.org/plugins/maven-javadoc-plugin/) inside the target `doc/_site` directory. You can copy the contents of the `doc/_site` directory to the web server hosting the documentation.

Writing documentation
---------------------

We keep our documentation in three different directories, organized by role:

1. User,
2. Administrator,
3. Developer.

### General guidelines

Jekyll uses a small block at the top of the file defining things like author, page title, the layout to be used, and more (see the top of the source for this page as an example). We minimally use the `layout` tag on most pages. Please **do not** add a title tag to your page, unless you want the page to appear as a tab at the top of the index pages.

You should opt to organize pages into directories. The file name of for the root page of your documentation should be `index` with *some* suffix (in the case of Markdown, `index.md`). Create additional pages if your page becomes too long; you can use directory-relative links (i.e., `./some-other-page/`) to have Jekyll link to other pages.

Images may be placed into the same directory as your root page, or in a subdirectory. The only rule for images is that all images related to your page should be kept near your page (please don't drop images for your page into the root documentation directory).

### User documentation

All user documentation should be stored under `$IRIDA/doc/user`.

We recommend that you write user documentation using a markup language that provides you with fine-grained control over the layout of the page (traditionally HTML). User guides *should* contain many images, so please take many screenshots, and abide by the image location rules set out above.

All user documentation pages minimally **must** contain a *brief* summary of the page (1 sentence) and a table of contents at the top of the page. The remainder of the formatting is left to the author. You may write out the table of contents by hand, or use something like this: https://github.com/ghiculescu/jekyll-table-of-contents. If you find a better (automated) way to get table of contents generated for HTML documents, please update this documentation.

### Developer documentation

All developer documentation should be stored under `$IRIDA/doc/developer`.

Please feel free to write developer documentation using whatever markup language you feel most comfortable using. If you use Markdown, you can generate a table of contents at the top of your file by defining a list followed by the string `{:toc}` on the next line:

    * This comment is not rendered, but becomes the table of contents
    {:toc}

Developer documentation should be strictly related to what's necessary to write code or plugins for IRIDA using our internal API or external REST API. Examples of suitable documentation for the developer directory might include:

1. REST API documentation and examples,
2. A guide for adding new workflows to IRIDA,
3. Automatically generated Javadoc.

### Administrator documentation

All administrator documentation should be stored under `$IRIDA/doc/administrator`.

Please feel free to write administrator documentation using whatever markup language you feel most comfortable using. If you use Markdown, you can generate a table of contents at the top of your file by defining a list followed by the string `{:toc}` on the next line:

    * This comment is not rendered, but becomes the table of contents
    {:toc}

Administrator documentation should be strictly related to **installing or running** IRIDA in a staging or production environment. Examples of suitable administrator documentation might include:

1. An install guide for running Galaxy in a clustered environment,
2. How to install IRIDA on a CentOS 7 machine with Tomcat,
3. Configuring IRIDA's file management options.

General Tips
------------

Writing tables in Markdown is tedious, a nice tool to do that automatically for you is: <http://www.tablesgenerator.com/markdown_tables>
