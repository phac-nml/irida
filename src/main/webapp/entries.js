/**
 * Put all bundles to be created in this file.
 *  bundle_name ==> location_off_entry_file.
 *  Webpack will then create the bundle in `resource/js/build/`
 */
module.exports = {
  vendor: ["expose-loader?exposes=$,jQuery!jquery", "./resources/js/vendors"],
  login: "./resources/js/pages/LoginPage.jsx",
  access_confirmation: "./resources/js/pages/oauth/access_confirmation.js",
  cart: "./resources/js/pages/cart/index.js",
  "client-base": "./resources/js/client.js",
  announcements: "./resources/js/pages/announcement",
  analysis: "./resources/js/pages/analysis",
  app: "./resources/js/app.js",
  dashboard: "./resources/js/pages/dashboard/Dashboard.jsx",
  launch: "./resources/js/pages/launch",
  project: "./resources/js/components/project/ProjectNav.jsx",
  "project-activity": "./resources/js/pages/projects/ProjectActivity.jsx",
  projects: "./resources/js/pages/projects/list/index.js",
  "samples-metadata-import":
    "./resources/js/pages/projects/samples-metadata-import",
  samples: "./resources/js/pages/projects/samples",
  "project-linelist": "./resources/js/pages/projects/linelist/index.js",
  "project-metadata-edit":
    "./resources/js/pages/projects/project-metadata-edit.js",
  "project-create": "./resources/js/pages/projects/create",
  "project-settings": "./resources/js/pages/projects/settings",
  "project-share": "./resources/js/pages/projects/share",
  "project-sync": "./resources/js/pages/projects/project-sync.js",
  "remote-apis": "./resources/js/pages/remote-apis/RemoteApiPage.jsx",
  "visualizations-phylogenetics":
    "./resources/js/pages/visualizations/phylogenetics/index.js",
  sample: "./resources/js/pages/SamplePage.jsx",
  "sample-files": "./resources/js/pages/samples/sample-files.js",
  "project-analyses": "./resources/js/pages/projects/project-analyses/",
  analyses: "./resources/js/pages/analyses/AnalysesPage.jsx",
  "users-list": "./resources/js/pages/UsersPage.jsx",
  "users-password": "./resources/js/pages/users/users-password.js",
  "sample-edit": "./resources/js/pages/samples/sample-edit.js",
  "sample-files-concatenate":
    "./resources/js/pages/samples/sample-files-concatenate.js",
  "sequencing-runs": "./resources/js/pages/sequencing-runs/index.js",
  groups: "./resources/js/pages/UserGroupsPage",
  "project-ncbi-exports": "./resources/js/pages/projects/ncbi-export",
  "project-ncbi-export":
    "./resources/js/pages/projects/ncbi-export/ncbi-export.js",
  search: "./resources/js/pages/search/search.js",
  user: "./resources/js/pages/user",
  admin: "./resources/js/pages/admin/index.js",
  "sequence-files": "./resources/js/pages/sequence-files/",
  "analyses-outputs": "./resources/js/pages/analyses/analyses-outputs/",
  password: "./resources/js/pages/password/PasswordReset.jsx",
};
