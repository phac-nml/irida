import {
  launchPipeline,
  saveNewPipelineParameters,
} from "../../apis/pipelines/pipelines";
import { TYPES } from "./launch-context";
import { PIPELINE_ID } from "./launch-utilities";

/**
 * Save a set of modified parameters.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {string} label - new label for the parameter set.
 * @param {array} parameters - list of modified parameters to save.
 * @returns {Promise<*|void>}
 */
export async function saveModifiedParametersAs(dispatch, label, parameters) {
  try {
    const data = await saveNewPipelineParameters({
      label,
      parameters,
      id: PIPELINE_ID,
    });

    const newParameterSet = {
      id: data.id,
      label,
      key: `set-${data.id}`,
      parameters,
    };

    // Update the state
    dispatch({
      type: TYPES.SAVE_MODIFIED_PARAMETERS,
      parameterSet: newParameterSet,
    });
    return data;
  } catch (e) {
    return Promise.reject(e);
  }
}

/**
 * Launch a the pipeline.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {object} formValues - all values found directly on the form
 * @returns {Promise<void>}
 */
export async function launchNewPipeline(dispatch, formValues, state) {
  /*
  The ...parameters will capture any dynamic parameters added to the page since everything
  else should be accounted for with their own variable names.  To these parameters we need to
  add the parameters for the saved ones.
   */
  const {
    name,
    description,
    emailPipelineResult,
    projects,
    updateSamples,
    reference,
    ...parameters
  } = formValues;
  const { files: fileIds } = state;

  const params = {
    name,
    description,
    fileIds,
    emailPipelineResult,
    projects,
    updateSamples,
    reference,
    parameters,
  };
  return launchPipeline(PIPELINE_ID, params);
}

/**
 * Called when a reference file has been uploaded to the server.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {string} name - filename
 * @param {number} id - identifier for the reference file
 */
export function referenceFileUploadComplete(dispatch, name, id) {
  dispatch({ type: TYPES.ADD_REFERENCE, payload: { id, name } });
}

/**
 * Set the current parameter set by its identifier.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {number} id - identifier for the set of parameters to use.
 */
export function setParameterSetById(dispatch, id) {
  dispatch({
    type: TYPES.PARAMETER_SET,
    payload: { id },
  });
}

/**
 * Use a set of modified.  This will store them and add it as a "modified" set
 * the the list of available parameter sets.  NOTE: This will not save them.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {object} set - modified parameter set.
 */
export function setModifiedParameters(dispatch, set) {
  dispatch({
    type: TYPES.USE_MODIFIED_PARAMETERS,
    payload: {
      set,
    },
  });
}

export function setSelectedSampleFiles(dispatch, files) {
  dispatch({
    type: TYPES.UPDATE_FILES,
    payload: {
      files,
    },
  });
}
