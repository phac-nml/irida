import React from "react";
import { getPipelineDetails } from "../../apis/pipelines/pipelines";
import {
  deepCopy,
  formatDefaultPipelineName,
  formatSavedParameterSets,
  PIPELINE_ID,
} from "./launch-utilities";

/**
 * @file React Context for providing to access to shared data and actions for the
 * IRIDA Workflow launch system.
 * @type {React.Context<unknown>}
 */

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
  UPDATE_FILES: "launch:update_files",
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
    case TYPES.UPDATE_FILES:
      return {
        ...state,
        files: action.payload.files,
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
        dynamicSources,
        ...details
      }) => {
        const formattedParameterSets = formatSavedParameterSets(
          savedPipelineParameters
        );

        const initialValues = {
          name: formatDefaultPipelineName(type, Date.now()),
          parameterSet: 0,
          shareResultsWithProjects: true,
          updateSamples: false,
          emailPipelineResult: "none", // default to not sending emails
          ["projects"]: details.projects.map((project) => project.value),
        };

        if (details.requiresReference) {
          initialValues.reference = details.referenceFiles.length
            ? details.referenceFiles[0].id
            : null;
        }

        // Get initial values for parameters with options.
        parameterWithOptions.forEach((parameter) => {
          initialValues[parameter.name] =
            parameter.value || parameter.options[0].value;
        });

        // Check for dynamic sources
        if (dynamicSources) {
          initialValues.dynamicSources = {};
          dynamicSources.forEach((source) => {
            initialValues[source.id] = source.options[0].value;
          });

          dynamicSources.forEach((parameter) => {
            initialValues[parameter.name] =
              parameter.value || parameter.options[0].value;
          });
        }

        dispatch({
          type: TYPES.LOADED,
          payload: {
            ...details,
            initialValues,
            pipeline: { name, description },
            parameterSet: deepCopy(formattedParameterSets[0]), // This will be the default set of saved parameters
            parameterWithOptions,
            parameterSets: formattedParameterSets,
            dynamicSources,
            files: [],
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

export { TYPES, LaunchProvider, useLaunch };
