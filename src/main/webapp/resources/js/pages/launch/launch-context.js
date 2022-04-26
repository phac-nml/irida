import React from "react";
import { getPipelineDetails } from "../../apis/pipelines/pipelines";
import {
  AUTOMATED_ID,
  formatDefaultPipelineName,
  formatSavedParameterSets,
  PIPELINE_ID,
} from "./launch-utilities";
import { isTruthy } from "../../utilities/form-utilities";

/**
 * @file React Context for providing to access to shared data and actions for the
 * IRIDA Workflow launch system.
 * @type {React.Context<unknown>}
 */

const LaunchContext = React.createContext();
LaunchContext.displayName = "LaunchContext";

const TYPES = {
  LOADED: "launch:loaded",
  SAVE_MODIFIED_PARAMETERS: "launch:save_modified_params",
  ADD_REFERENCE: "launch:add_reference",
  UPDATE_FILES: "launch:update_files",
  UPDATE_ASSEMBLIES: "launch:update_assemblies",
};

const reducer = (state, action) => {
  function addReference(file) {
    const files = [...state.referenceFiles];
    files.push(file);
    return files;
  }

  switch (action.type) {
    case TYPES.LOADED:
      return {
        ...state,
        shareResultsWithProjects: true,
        loading: false,
        ...action.payload,
      };
    case TYPES.ADD_REFERENCE:
      return {
        ...state,
        reference: action.payload.id,
        referenceFiles: addReference(action.payload),
      };
    case TYPES.SAVE_MODIFIED_PARAMETERS:
      return {
        ...state,
        parameterSets: [...state.parameterSets, action.parameterSet],
      };
    case TYPES.UPDATE_FILES:
      return {
        ...state,
        files: action.payload.files,
      };
    case TYPES.UPDATE_ASSEMBLIES:
      return {
        ...state,
        assemblies: action.payload.assemblies,
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
          shareResultsWithProjects: true,
          updateSamples: false,
          emailPipelineResult: "none", // default to not sending emails
          ["projects"]: details.projects.map((project) => project.value),
        };

        formattedParameterSets[0].parameters.forEach(
          ({ name, value }) => (initialValues[name] = value)
        );

        if (details.requiresReference) {
          initialValues.reference = details.referenceFiles.length
            ? details.referenceFiles[0].id
            : null;
        }

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

        /*
         For parameters with options that are true/false we set the initialvalue to the value
         if its set by the developer otherwise we set it to false
         */
        if (parameterWithOptions) {
          parameterWithOptions.forEach((parameter) => {
            if (isTruthy(parameter.options)) {
              initialValues[parameter.name] = parameter.value === "true";
            }
          });
        }

        dispatch({
          type: TYPES.LOADED,
          payload: {
            ...details,
            initialValues,
            pipeline: { name, description },
            parameterWithOptions,
            parameterSets: formattedParameterSets,
            dynamicSources,
            files: [],
            assemblies: [],
            automatedId: AUTOMATED_ID,
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
