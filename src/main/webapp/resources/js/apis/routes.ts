const CONTEXT_PATH = document.documentElement.dataset.context || "/";

function formatUrl(path: TemplateStringsArray, ...keys: string[]) {
  return function (urlParams?: Record<string, string | number>): string {
    let url = CONTEXT_PATH;
    if (urlParams) {
      const temp = path.slice();
      keys.forEach((key, i) => {
        temp[i] = temp[i] + urlParams[key];
      });
      url += temp.join("");
    } else {
      url += path;
    }

    return url;
  };
}

export const activities_project_route = formatUrl`ajax/activities/project`;
export const activities_recent_route = formatUrl`ajax/activities/all`;
export const activities_user_route = formatUrl`ajax/activities/user`;

export const admin_statistics_route = formatUrl`ajax/statistics/basic`;
export const admin_statistics_users_route = formatUrl`ajax/statistics/users`;
export const admin_statistics_samples_route = formatUrl`ajax/statistics/samples`;
export const admin_statistics_analyses_route = formatUrl`ajax/statistics/analyses`;
export const admin_statistics_projects_route = formatUrl`ajax/statistics/projects`;

export const analyses_outputs_route = formatUrl`ajax/analyses-outputs`;
export const analyses_outputs_download_file_route = formatUrl`ajax/analyses-outputs/download/file`;
export const analyses_outputs_download_files_zip_route = formatUrl`ajax/analyses-outputs/download/file/zip`;
export const analyses_outputs_prepare_download_route = formatUrl`ajax/analyses-outputs/download/prepare`;
export const analyses_pipeline_states_route = formatUrl`ajax/analyses/states`;
export const analyses_pipeline_types_route = formatUrl`ajax/analyses/types`;
export const analyses_delete_submissions_route = formatUrl`ajax/analyses/delete`;
export const analyses_queue_count_route = formatUrl`ajax/analyses/queue`;
export const analyses_update_table_progress_route = formatUrl`ajax/analyses/update-table-progress`;

export const analysis_info_route = formatUrl`ajax/analysis/${"submissionId"}/analysis-details`;
export const analysis_details_route = formatUrl`ajax/analysis/details/${"submissionId"}`;
export const analysis_input_files_route = formatUrl`ajax/analysis/inputs/${"submissionId"}`;
export const analysis_update_email_route = formatUrl`ajax/analysis/update-email-pipeline-result`;
export const analysis_update_route = formatUrl`ajax/analysis/update-analysis`;
export const analysis_delete_route = formatUrl`ajax/analysis/delete/${"submissionId"}`;
export const analysis_shared_projects_route = formatUrl`ajax/analysis/${"submissionId"}/share`;
export const analysis_share_route = formatUrl`ajax/analysis/${"submissionId"}/share`;
export const analysis_save_to_sample_route = formatUrl`ajax/analysis/${"submissionId"}/save-results`;
export const analysis_job_errors_router = formatUrl`ajax/analysis/${"submissionId"}/job-errors`;
export const analysis_sistr_results_route = formatUrl`ajax/analysis/sistr/${"submissionId"}`;
export const analysis_output_info_route = formatUrl`ajax/analysis/${"submissionId"}/outputs`;
export const analysis_progress_update_route = formatUrl`ajax/analysis/${"submissionId"}/updated-progress`;
export const analysis_data_via_chunks_route = formatUrl`ajax/analysis/${"submissionId"}/outputs/${"fileId"}`;
export const analysis_data_via_lines_route = formatUrl`ajax/analysis/${"submissionId"}/outputs/${"fileId"}`;
export const analysis_newick_route = formatUrl`ajax/analysis/${"submissionId"}/tree`;
export const analysis_download_zip_route = formatUrl`ajax/analysis/download/${"submissionId"}`;
export const analysis_provenance_by_file_route = formatUrl`ajax/analysis/${"submissionId"}/provenance`;
export const analysis_parse_excel_route = formatUrl`ajax/analysis/${"submissionId"}/parseExcel`;
export const analysis_image_route = formatUrl`ajax/analysis/${"submissionId"}/image`;

export const announcements_get_route = formatUrl`ajax/announcements/${"aID"}`;
export const announcements_user_list_route = formatUrl`ajax/announcements/user/list`;
export const announcements_user_read_route = formatUrl`ajax/announcements/user/read`;
export const announcements_user_unread_route = formatUrl`ajax/announcements/user/unread`;
export const announcements_mark_as_read_route = formatUrl`ajax/announcements/read/${"aID"}`;
export const announcements_create_route = formatUrl`ajax/announcements/create`;
export const announcements_update_route = formatUrl`ajax/announcements/update`;
export const announcements_delete_route = formatUrl`ajax/announcements/delete`;

export const cart_api_route = formatUrl`ajax/cart`;
export const cart_add_samples_route = formatUrl`ajax/cart`;
export const cart_count_route = formatUrl`ajax/cart/count`;
export const cart_empty_route = formatUrl`ajax/cart`;
export const cart_remove_sample_route = formatUrl`ajax/cart/sample`;

export const clients_revoke_token_route = formatUrl`ajax/clients/revoke`;
export const clients_validate_route = formatUrl`ajax/clients/validate`;
export const clients_create_route = formatUrl`ajax/clients`;
export const clients_delete_route = formatUrl`ajax/clients`;
export const clients_regenerate_secret_route = formatUrl`ajax/clients/secret`;
export const clients_update_route = formatUrl`ajax/clients`;

export const dashboard_route = formatUrl`ajax/user/statistics`;

export const export_ncbi_project_route = formatUrl`ajax/ncbi/project/${"projectId"}/list`;
export const export_ncbi_details_route = formatUrl`ajax/ncbi/project/${"projectId"}/details/${"uploadId"}`;

export const files_sequence_file_upload_route = formatUrl`ajax/samples/${"sampleId"}/sequenceFiles/upload`;
export const files_fast5_upload_route = formatUrl`ajax/samples/${"sampleId"}/fast5/upload`;
export const files_assembly_upload_route = formatUrl`ajax/samples/${"sampleId"}/assembly/upload`;

export const galaxy_samples_route = formatUrl`ajax/galaxy-export/samples`;
export const galaxy_remove_session_route = formatUrl`ajax/galaxy-export/remove`;

export const sequence_file_fastqc_details_route = formatUrl`ajax/sequenceFiles/fastqc-details`;
export const sequence_file_fastqc_charts_route = formatUrl`ajax/sequenceFiles/fastqc-charts`;
export const sequence_file_overrepresented_sequences_route = formatUrl`ajax/sequenceFiles/overrepresented-sequences`;
