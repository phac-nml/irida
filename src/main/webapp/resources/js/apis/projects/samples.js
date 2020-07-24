import {setBaseUrl} from "../../utilities/url-utilities";

const URL = setBaseUrl(`/ajax/projects/${window.project.id}/samples`)

/**
 * Server side validation of a new sample name.
 * @param {string} name - sample name to validate
 * @returns {Promise<any>}
 */
export async function validateSampleName(name) {
    const response = await fetch(`${URL}/add-sample/validate`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ name }),
        }
    )
    return response.json();
}