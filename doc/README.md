# IRIDA Documentation
This is the development copy of the IRIDA documenation.  View the published documentation pages at <https://phac-nml.github.io/irida-documentation/>.

## Running the pages locally
GitHub Pages uses Jekyll to generate IRIDA's documentation.  You can run Jekyll locally to verify your documentation changes locally before pushing to GitHub.

* Ensure Ruby is installed. <https://www.ruby-lang.org/en/downloads/>
* Install bundler
```bash
gem install bundler
```
* Install Jekyll from the Gemfile in the `/docs` subdirectory
```bash
# from the docs directory
bundle install
```
* Run Jekyll from the docs directory
```bash
# from the docs directory
bundle exec jekyll serve
```
* View the pages at <http://localhost:4000/irida-documentation>