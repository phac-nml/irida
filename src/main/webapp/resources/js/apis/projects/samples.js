import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`/ajax/projects/${window.project.id}/samples`);

/**
 * Server side validation of a new sample name.
 * @param {string} name - sample name to validate
 * @returns {Promise<any>}
 */
export async function validateSampleName(name) {
  const params = new URLSearchParams();
  params.append("name", name.trim());
  const response = await fetch(`${URL}/add-sample/validate?${params}`);
  return response.json();
}

/**
 * Create a new sample within a project
 * @param {string} name - name of the new sample
 * @param {string} organism - name of the organism (optional)
 * @returns {Promise<Response>}
 */
export async function createNewSample({ name, organism }) {
  const response = await fetch(setBaseUrl(`${URL}/add-sample`), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ name: name.trim(), organism }),
  });

  return response;
}
