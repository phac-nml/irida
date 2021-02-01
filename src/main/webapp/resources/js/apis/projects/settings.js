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

export const updateProcessingCoverage = async (projectId, coverage) =>
  fetch(setBaseUrl(`ajax/projects/${projectId}/settings/coverage`), {
    method: `PUT`,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(coverage),
  })
    .then((response) => response.json())
    .then((data) => data.message);
