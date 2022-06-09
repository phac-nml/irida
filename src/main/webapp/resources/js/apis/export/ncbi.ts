import axios from "axios";
import {ExportUploadState, NcbiSubmission, User, UserMinimal} from "../../types/irida";
import {setBaseUrl} from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/ncbi/project`);

export interface NcbiExportSubmissionTableModel {
    exportedSamples: number;
    state: ExportUploadState;
    submitter: UserMinimal;
    bioProjectId: string;
}

/**
 * Get a list of ncbi exports for the current project.
 */
export async function getProjectNCBIExports(projectId: number): Promise<NcbiExportSubmissionTableModel[]> {
    try {
        const {data} = await axios.get(`/ajax/ncbi/project/${projectId}/list`);
        return data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            if (error.response) {
                return Promise.reject(error.response.data.error);
            } else {
                return Promise.reject(error.message);
            }
        } else {
            return Promise.reject("An unexpected error occurred");
        }
    }
}

export async function getNcbiSubmission(
    projectId: number,
    uploadId: number
): Promise<NcbiSubmission> {
    try {
        const {data} = await axios.get(
            `${BASE_URL}/${projectId}/details/${uploadId}`
        );
        return data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            if (error.response) {
                return Promise.reject(error.response.data.error);
            } else {
                return Promise.reject(error.message);
            }
        } else {
            return Promise.reject("An unexpected error occurred");
        }
    }
}