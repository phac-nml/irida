import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

export const fetchProcessingInformation = async (projectId) =>
  fetch(setBaseUrl(`ajax/projects/${projectId}/settings/priorities`))
    .then((response) => response.json())
    .then((data) => data);

export const updateProcessingPriority = async (projectId, priority) =>
  fetch(
    setBaseUrl(
      `ajax/projects/${projectId}/settings/priority?priority=${priority}`
    ),
    {
      method: `PUT`,
    }
  )
    .then((response) => response.json())
    .then((data) => data.message);

export const fetchProcessingCoverage = async (projectId) =>
  fetch(setBaseUrl(`ajax/projects/${projectId}/settings/coverage`))
    .then((response) => response.json())
    .then((data) => data);

export const updateProcessingCoverage = async (projectId, coverage) => {
  try {
    const { data } = await axios.put(
      setBaseUrl(`ajax/projects/${projectId}/settings/coverage`),
      coverage
    );
    return data.message;
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
};

export async function fetchAnalysisTemplatesForProject(projectId) {
  try {
    const { data } = await axios.get(
      setBaseUrl(`ajax/projects/${projectId}/settings/analysis-templates`)
    );
    return data;
  } catch (e) {
    return Promise.reject();
  }
}

export async function deleteAnalysisTemplateForProject(templateId, projectId) {
  try {
    const { data } = await axios.delete(
      setBaseUrl(
        `ajax/projects/${projectId}/settings/analysis-templates?templateId=${templateId}`
      )
    );
    return data.message;
  } catch (e) {
    return Promise.reject();
  }
}
