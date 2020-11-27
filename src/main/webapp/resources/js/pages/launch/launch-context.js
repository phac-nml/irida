import React from "react";
import {
  getPipelineDetails,
  saveNewPipelineParameters,
} from "../../apis/pipelines/pipelines";
import {
  deepCopy,
  formatDefaultPipelineName,
  formatParametersWithOptions,
  formatSavedParameterSets,
} from "./launch-utilities";

/**
 * @file React Context for providing to access to shared data and actions for the
 * IRIDA Workflow launch system.
 * @type {React.Context<unknown>}
 */

/*
IRIDA Workflow identifier can be found as a query parameter within the URL.
Here we grab it and hold onto it so that we can use it to gather all the
details about the pipeline.
 */
const PIPELINE_ID = (() => {
  const params = new URLSearchParams(window.location.search);
  return params.get("id");
})();

const LaunchContext = React.createContext();
LaunchContext.displayName = "LaunchContext";

const TYPES = {
  LOADED: "launch:loaded",
  PARAMETER_SET: "launch:parameters",
  MODIFIED_PARAMETERS: "launch:modified_params",
  USE_MODIFIED_PARAMETERS: "launch:use_modified_params",
  SAVE_MODIFIED_PARAMETERS: "launch:save_modified_params",
  REFERENCE_FILE: "launch:reference_file",
  ADD_REFERENCE: "launch:add_reference",
  USE_REFERENCE: "launch:use_reference",
};

const reducer = (state, action) => {
  function addReference(file) {
    const files = [...state.referenceFiles];
    files.push(file);
    return files;
  }

  function setParameterSetById(id) {
    const set = state.parameterSets.find((p) => p.id === id);
    return deepCopy(set);
  }

  function updateModifiedParameters(currentSet) {
    /*
    Suffix to be added to the identifier to identify when it is modified
     */
    const SUFFIX = `-MODIFIED`;

    /*
    Get a copy of all the sets
     */
    const sets = deepCopy(state.parameterSets);

    /*
    Three different states:
      1. Set that has not been modified before
      2. Set that has been modified before and currently selected
      3. Set that has been modified before but the original is selected
     */

    if (`${currentSet.key}`.endsWith(SUFFIX)) {
      // Previously modified and selected.
      // Remove the modified one from the list and add the updated one
      const updatedSets = sets.filter((a) => a.id !== currentSet.id);
      updatedSets.push(currentSet);

      return {
        parameterSets: updatedSets,
        parameterSet: currentSet,
      };
    } else if (!currentSet.modified) {
      // First time modified
      currentSet.modified = true;

      // Set modified to the original set
      sets.find((s) => s.id === currentSet.id).modified = true;

      // Update the id to show that it has been modified
      currentSet.id = `${currentSet.id}${SUFFIX}`;
      currentSet.key = `set-${currentSet.id}`;

      // Add the current set to the list
      sets.push(currentSet);

      // Update the data model
      return {
        parameterSets: sets,
        parameterSet: currentSet,
      };
    } else {
      // Need to find the actual modified set
      const index = sets.findIndex((s) => s.id === `${currentSet.id}${SUFFIX}`);
      // Remove the current set.
      const [set] = sets.splice(index, 1);

      // Use the new modified parameters
      set.parameters = currentSet.parameters;
      sets.push(set);

      return { parameterSets: sets, parameterSet: set };
    }
  }

  switch (action.type) {
    case TYPES.LOADED:
      return {
        ...state,
        shareResultsWithProjects: true,
        loading: false,
        ...action.payload,
      };
    case TYPES.PARAMETER_SET:
      return {
        ...state,
        parameterSet: setParameterSetById(action.payload.id),
      };
    case TYPES.USE_MODIFIED_PARAMETERS:
      return {
        ...state,
        ...updateModifiedParameters(action.payload.set),
      };
    case TYPES.SAVE_MODIFIED_PARAMETERS:
      return {
        ...state,
        parameterSets: [...state.parameterSets, action.parameterSet],
        parameterSet: action.parameterSet,
      };
    case TYPES.REFERENCE_FILE:
      return { ...state, referenceFile: action.payload.referenceFile };
    case TYPES.ADD_REFERENCE:
      return {
        ...state,
        referenceFile: action.payload.id,
        referenceFiles: addReference(action.payload),
      };
    case TYPES.MODIFIED_PARAMETERS:
      return {
        ...state,
        parameterSet: action.payload.set,
        parameterSets: action.payload.sets,
      };
    case TYPES.USE_REFERENCE:
      return {
        ...state,
        referenceFile: action.payload.id,
      };
  }
};

function LaunchProvider({ children }) {
  /*
  Using a reducer to hold all user data that the user can modify and must be
  sent to the server to launch the workflow pipeline.
   */
  const [state, dispatch] = React.useReducer(reducer, { loading: true });

  React.useEffect(() => {
    getPipelineDetails({ id: PIPELINE_ID }).then(
      ({
        name,
        description,
        type,
        parameterWithOptions,
        savedPipelineParameters,
        ...details
      }) => {
        const formattedParameterWithOptions = formatParametersWithOptions(
          parameterWithOptions
        );

        const formattedParameterSets = formatSavedParameterSets(
          savedPipelineParameters
        );

        const initialValues = {
          name: formatDefaultPipelineName(type, Date.now()),
          parameterSet: 0,
          shareResultsWithProjects: true,
          updateSamples: false,
        };

        // Get initial values for parameters with options.
        formattedParameterWithOptions.forEach((parameter) => {
          initialValues[parameter.name] =
            parameter.value || parameter.options[0].value;
        });

        dispatch({
          type: TYPES.LOADED,
          payload: {
            ...details,
            initialValues,
            pipeline: { name, description },
            parameterSet: deepCopy(formattedParameterSets[0]), // This will be the default set of saved parameters
            parameterWithOptions: formattedParameterWithOptions,
            parameterSets: formattedParameterSets,
            referenceFile:
              details.requiresReference && details.referenceFiles.length
                ? details.referenceFiles[0].id
                : null,
          },
        });
      }
    );
  }, []);

  const value = [state, dispatch];
  return (
    <LaunchContext.Provider value={value}>{children}</LaunchContext.Provider>
  );
}

/**
 * Launch a the pipeline.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {object} parameters - all parameters required to launch the pipe
 * @returns {Promise<void>}
 */
async function launchNewPipeline(dispatch, parameters) {
  console.log(parameters);
  return Promise.resolve();
}

/**
 * Set the currently selected reference file by its id.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {number} id - identifier for the reference file to use
 */
function setReferenceFileById(dispatch, id) {
  dispatch({ type: TYPES.USE_REFERENCE, payload: { id } });
}

/**
 * Called when a reference file has been uploaded to the server.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {string} name - filename
 * @param {number} id - identifier for the reference file
 */
function referenceFileUploadComplete(dispatch, name, id) {
  dispatch({ type: TYPES.ADD_REFERENCE, payload: { id, name } });
}

/**
 * Set the current parameter set by its identifier.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {number} id - identifier for the set of parameters to use.
 */
function setParameterSetById(dispatch, id) {
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
function setModifiedParameters(dispatch, set) {
  dispatch({
    type: TYPES.USE_MODIFIED_PARAMETERS,
    payload: {
      set,
    },
  });
}

/**
 * Save a set of modified parameters.
 *
 * @param {function} dispatch - specific the the launch context
 * @param {string} label - new label for the parameter set.
 * @param {array} parameters - list of modified parameters to save.
 * @returns {Promise<*|void>}
 */
async function saveModifiedParametersAs(dispatch, label, parameters) {
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

/*
 * Custom React hooks to get access to the contexts, prevents needing to wrap your child
 * components in context consumers.
 * See: {@link https://kentcdodds.com/blog/how-to-use-react-context-effectively#the-custom-consumer-hook}
 * @returns {unknown}
 */
function useLaunch() {
  const context = React.useContext(LaunchContext);
  if (context === undefined) {
    throw new Error(`useLaunchState must be used with a LaunchProvider`);
  }
  return context;
}

export {
  LaunchProvider,
  useLaunch,
  launchNewPipeline,
  setReferenceFileById,
  referenceFileUploadComplete,
  setParameterSetById,
  setModifiedParameters,
  saveModifiedParametersAs,
};
