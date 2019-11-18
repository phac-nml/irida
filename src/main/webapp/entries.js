/**
 * Put all bundles to be created in this file.
 *  bundle_name ==> location_off_entry_file.
 *  Webpack will then create the bundle in `resource/js/build/`
 */
module.exports = {
  vendor: [
    "core-js/stable",
    "regenerator-runtime/runtime",
    "expose-loader?$!jquery",
    "expose-loader?angular!angular",
    "expose-loader?i18n!./resources/js/i18n.js",
    "./resources/js/vendors"
  ],
  access_confirmation: "./resources/js/pages/oauth/access_confirmation.js",
  cart: "./resources/js/pages/cart/index.js",
  "client-base": "./resources/js/client.js",
  "events/admin": "./resources/js/pages/activities/activities.js",
  "announcements/read": "./resources/js/pages/announcement/announcements.js",
  analysis: "./resources/js/pages/analysis/analysis.js",
  app: "./resources/js/app.js",
  dashboard: "./resources/js/pages/dashboard.js",
  "pipeline-launch": "./resources/js/pages/pipelines/pipeline.launch.js",
  "projects/project_details": "./resources/js/pages/projects/project-events.js",
  "projects/projects": "./resources/js/pages/projects/list/index.js",
  "projects/projects_samples_metadata_upload":
    "./resources/js/pages/projects/samples-metadata-import/index.js",
  "projects/project_samples": "./resources/js/pages/projects/samples/project-samples.js",
  "project-samples-merge":
    "./resources/js/pages/projects/samples/modals/samples-merge.js",
  "project-samples-copy":
    "./resources/js/pages/projects/samples/modals/samples-copy.js",
  "project-samples-remove":
    "./resources/js/pages/projects/samples/modals/samples-remove.js",
  "project-samples-filter":
    "./resources/js/pages/projects/samples/modals/samples-filter.js",
  "project-samples-linker":
    "./resources/js/pages/projects/samples/modals/samples-linker.js",
  "projects/project_add_sample": "./resources/js/pages/projects/project-add-samples.js",
  "projects/project-linelist": "./resources/js/pages/projects/linelist/index.js",
  "projects/project_metadata_edit":
    "./resources/js/pages/projects/project-metadata-edit.js",
  "projects/export/ncbi": "./resources/js/pages/projects/export/ncbi-export.js",
  "projects/project_new": "./resources/js/pages/projects/projects-new.js",
  "projects/settings/pages/basic":
    "./resources/js/pages/projects/settings/project-settings-basic.js",
  "projects/settings/pages/remote":
    "./resources/js/pages/projects/settings/project-settings-remote.js",
  "projects/project_sync": "./resources/js/pages/projects/project-sync.js",
  "remote_apis/remote_api_details":
    "./resources/js/pages/remote-apis/remote-api-details.js",
  "remote_apis/list": "./resources/js/pages/remote-apis/remote-apis-list.js",
  "projects/project_samples_metadata_template":
    "./resources/js/pages/projects/metadata-template/create-metadata-template.js",
  "analysis/visualizations/phylocanvas-metadata":
    "./resources/js/pages/visualizations/phylogenetics/index.js",
  "projects/settings/pages/groups":
    "./resources/js/pages/projects/project-users-groups.js",
  "projects/settings/pages/members":
    "./resources/js/pages/projects/project-users-groups.js",
  "projects/settings/pages/associated":
    "./resources/js/pages/projects/associated-projects/index.js",
  "sample-files": "./resources/js/pages/samples/sample-files.js",
  "projects/settings/pages/references":
    "./resources/js/pages/projects/project-reference-files.js",
  "projects/analyses/pages/analyses_table":
    "./resources/js/pages/projects/project-analyses/ProjectAnalysesPage.jsx",
  "analyses/analyses-table":
    "./resources/js/pages/analyses/analysis-outputs-table.js",
  "analyses/analyses": "./resources/js/pages/analyses/AnalysesPage.jsx",
  "clients/list": "./resources/js/pages/clients/clients-list.js",
  "users/list": "./resources/js/pages/users/users-list.js",
  "users-password": "./resources/js/pages/users/users-password.js",
  "announcements/details":
    "./resources/js/pages/announcement/announcement-users.js",
  "announcements/control":
    "./resources/js/pages/announcement/announcement-admin.js",
  "samples/sample-edit": "./resources/js/pages/samples/sample-edit.js",
  "sequencingRuns/list":
    "./resources/js/pages/sequencing-runs/SequencingRunsPage.jsx",
  "groups/list": "./resources/js/pages/users/groups-list.js",
  "groups/details": "./resources/js/pages/users/groups-members.js",
  "ncbi-exports": "./resources/js/pages/projects/ncbi-export.js",
  "search/search": "./resources/js/pages/search/search.js",
  "sequenceFiles/file_overrepresented": "./resources/js/pages/sequence-files/overrepresented.js",
  "sequencingRuns/run_files": "./resources/js/pages/sequence-files/run-files.js"
};
