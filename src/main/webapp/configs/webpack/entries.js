/**
 * Put all bundles to be created in this file.
 *  bundle_name ==> location_off_entry_file.
 *  Webpack will then create the bundle in `resource/js/build/`
 */
module.exports = {
  app: './resources/js/app.js',
  "samples-metadata-import": "./resources/js/pages/projects/samples-metadata-import/index.js",
  "project-linelist": "./resources/js/pages/projects/linelist/index.js",
  'project-linelist-template': './resources/js/pages/projects/template/index.js',
  'visualizations-phylogenetics': './resources/js/pages/visualizations/phylogenetics/index.js'
};
