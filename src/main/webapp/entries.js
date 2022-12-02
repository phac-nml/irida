/**
 * Put all bundles to be created in this file.
 *  bundle_name ==> location_off_entry_file.
 *  Webpack will then create the bundle in `resource/js/build/`
 */
module.exports = {
  login: "./resources/js/pages/LoginPage.tsx",
  "project-spa": "./resources/js/pages/projects/ProjectSPA.tsx",
  access_confirmation: "./resources/js/pages/oauth/access_confirmation.js",
  cart: "./resources/js/pages/cart/index.tsx",
  announcements: "./resources/js/pages/announcement",
  analysis: "./resources/js/pages/analysis",
  app: "./resources/js/app.js",
  dashboard: "./resources/js/pages/dashboard/Dashboard.tsx",
  launch: "./resources/js/pages/launch",
  project: "./resources/js/components/project/ProjectNav.jsx",
  "project-activity": "./resources/js/pages/projects/ProjectActivity.jsx",
  projects: "./resources/js/pages/projects/list/index.js",
  "samples-metadata-import":
    "./resources/js/pages/projects/samples-metadata-import",
  samples: "./resources/js/pages/projects/samples",
  "project-linelist": "./resources/js/pages/projects/linelist/index.js",
  "project-settings": "./resources/js/pages/projects/settings",
  "project-share": "./resources/js/pages/projects/share",
  "project-sync": "./resources/js/pages/projects/project-sync.js",
  "remote-apis": "./resources/js/pages/remote-apis/RemoteApiPage.jsx",
  "project-analyses": "./resources/js/pages/projects/project-analyses/",
  analyses: "./resources/js/pages/analyses/AnalysesPage.jsx",
  "users-list": "./resources/js/pages/user/components/UserListPage.jsx",
  "sequencing-runs": "./resources/js/pages/sequencing-runs/index.js",
  groups: "./resources/js/pages/UserGroupsPage",
  "project-ncbi-exports": "./resources/js/pages/projects/ncbi",
  "project-ncbi-export": "./resources/js/pages/projects/ncbi/create",
  search: "./resources/js/pages/search",
  user: "./resources/js/pages/user",
  admin: "./resources/js/pages/admin/index.tsx",
  "analyses-outputs": "./resources/js/pages/analyses/analyses-outputs/",
  password: "./resources/js/pages/password/PasswordReset.jsx",
};
