/**
 * Put all bundles to be created in this file.
 *  bundle_name ==> location_off_entry_file.
 *  Webpack will then create the bundle in `resource/js/build/`
 */
module.exports = {
  app: "./resources/js/app.js",
  events: "./resources/js/modules/events/events.js",
  projects: "./resources/js/pages/projects/projects.js",
  "samples-metadata-import":
    "./resources/js/pages/projects/samples-metadata-import/index.js",
  "project-add-sample": "./resources/js/pages/projects/project-add-samples.js",
  "project-linelist": "./resources/js/pages/projects/linelist/index.js",
  "create-metadata-template":
    "./resources/js/pages/projects/metadata-template/create-metadata-template.js",
  "visualizations-phylogenetics":
    "./resources/js/pages/visualizations/phylogenetics/index.js",
  "projects-associated-edit":
    "./resources/js/pages/projects/associated-projects/edit.module.js",
  "sample-files": "./resources/js/pages/samples/sample-files.js",
  "project-reference-files":
    "./resources/js/pages/projects/project-reference-files.js",
  "analyses-table": "./resources/js/pages/analyses/analyses-table.js",
  "clients-list": "./resources/js/pages/clients/clients-list.js",
  "users-list": "./resources/js/pages/users/users-list.js",
  "announcement-users":
    "./resources/js/pages/announcement/announcement-users.js",
  "announcement-admin":
    "./resources/js/pages/announcement/announcement-admin.js",
  "sequencing-runs":
    "./resources/js/pages/sequencing-runs/sequencing-runs-list.js",
  "groups-list": "./resources/js/pages/users/groups-list.js"
};
