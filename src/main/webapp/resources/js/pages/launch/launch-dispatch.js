import {
  launchPipeline,
  saveNewPipelineParameters,
} from "../../apis/pipelines/pipelines";
import { TYPES } from "./launch-context";
import { formatSavedParameterSets, PIPELINE_ID } from "./launch-utilities";

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
    const { pipelineParameters: data } = await saveNewPipelineParameters({
      label,
      parameters,
      id: PIPELINE_ID,
    });

    const [newParameterSet] = formatSavedParameterSets([data]);

    // Update the state
    dispatch({
      type: TYPES.SAVE_MODIFIED_PARAMETERS,
      parameterSet: newParameterSet,
    });
    return Promise.resolve(data.id);
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
  const {
    files: fileIds,
    assemblies: assemblyIds,
    automatedId: automatedProjectId,
  } = state;

  const params = {
    name,
    description,
    fileIds,
    assemblyIds,
    emailPipelineResult,
    projects,
    updateSamples,
    reference,
    parameters,
    automatedProjectId,
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
 * Update which sample files are selected
 *
 * @param {function} dispatch - specific the the launch context
 * @param {array} files - list of sample files to run on the pipeline
 */
export function setSelectedSampleFiles(dispatch, files) {
  dispatch({
    type: TYPES.UPDATE_FILES,
    payload: {
      files,
    },
  });
}

/**
 * Updated which samples assemblies are selected
 *
 * @param {function} dispatch - specify the launch context
 * @param {array} assemblies - list of sample assemblies to run the pipeline
 */
export function setSelectedSampleAssemblies(dispatch, assemblies) {
  dispatch({
    type: TYPES.UPDATE_ASSEMBLIES,
    payload: {
      assemblies,
    },
  });
}
