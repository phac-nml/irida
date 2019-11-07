import axios from "axios";

const URL = `${window.TL.BASE_URL}ajax/projects`;

/**
 * Returns the projects on the current page of the projects table.
 * @param {Object} params
 * @returns {Promise<{}|T>}
 */
export async function getPagedProjectsForUser(params) {
  try {
    const { data } = await axios.post(
      `${URL}?admin=${window.location.href.includes("all")}
  `,
      params
    );
    return data;
  } catch (e) {
    return {};
  }
}

/**
 * Returns a list of associated projects for the current project.  If the user
 * is the project manager or admin, the list will include available projects.
 * @param {number} id
 * @returns {Promise<{associatedProjectList: []}>}
 */
export async function getAssociatedProjects(id) {
  try {
    const { data } = await axios.get(`${URL}/${id}/settings/associated`);
    return data;
  } catch (e) {
    return { associatedProjectList: [] };
  }
}

/**
 * Removes the project with the assoicatedId from the current project.
 * @param {number} id
 * @param {number} associatedId
 * @returns {Promise<void>}
 */
export async function removeAssociatedProject(id, associatedId) {
  await axios.post(
    `${URL}/${id}/settings/associated/remove?associatedId=${associatedId}`
  );
}

/**
 * Adds the project with the assoicatedId from the current project.
 * @param {number} id
 * @param {number} associatedId
 * @returns {Promise<void>}
 */
export async function addAssociatedProject(id, associatedId) {
  await axios.post(
    `${URL}/${id}/settings/associated/add?associatedId=${associatedId}`
  );
}
