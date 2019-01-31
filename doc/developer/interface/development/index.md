---
layout: default
---


User Interface Development
==========================

## Yarn

[Yarn](https://yarnpkg.com/en/) is a front-end package manage for JavaScript dependencies.

### Yarn Installation

**Yarn** should be installed globally on your machine.  Read the [install guide](https://yarnpkg.com/en/docs/install) for your OS.

### Yarn Install Dependencies

When IRIDA is built all front-end dependencies will automatically be downloaded into a to `src/main/webapp/node_modules`.

If this directory gets removed in can be re-downloaded by running the command `yarn` in the `webapp` directory.

#### Add New Yarn Dependency

All new dependencies should be approved before adding.  To install a new dependency, please read the [Yarn Documentation](https://yarnpkg.com/en/docs/usage).

### Errors

An error can occur if the system **Yarn** is different from the one used by IRIDA.  If this occurs the system on can still by re-installing the dependencies using the system version of **Yarn**.  In `src/main/webapp` run `yarn clean && yarn`.

### Yarn Scripts

`yarn build`
 * Run webpack for a production build.
    * Compiles and minimizes `scss` & `css`
    * Compiles and minimises JavaScript
    
`yarn start`
  * Start the front-end development environment:
    * Auto-compilation of assets
    * Auto browser refresh
    
`yarn clean`
 * Removes:
    * `src/main/webapp/node_modules`
    * `src/main/webapp/resources/dist`