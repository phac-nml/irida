import React, { useEffect, useReducer } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import * as CONSTANTS from "./lauch-constants";

const LaunchStateContext = React.createContext();
const LaunchDispatchContext = React.createContext();

function launchReducer(state, action) {
  const modifyParameter = () => {
    const modified = [...state.parameters];
    modified[action.index].value = action.value;
    return modified;
  };

  const getParameters = () => {
    const parameters = state.original.parameters[action.index];
    if (parameters) {
      return [...parameters.parameters];
    }
    return [];
  };

  switch (action.type) {
    case CONSTANTS.DISPATCH_PIPELINE_LOADED:
      return {
        ...state,
        original: { ...action.value },
        name: `${action.value.name}_${Date.now()}`,
        parameters: [...action.value.parameters[0].parameters],
        requiresReference: action.value.requiresReference,
        files: action.value.files,
        modified: false,
        fetching: false,
      };
    case CONSTANTS.DISPATCH_DETAILS_UPDATE:
      return { ...state, [action.field]: action.value };
    case CONSTANTS.DISPATCH_PARAMETER_CHANGE:
      return {
        ...state,
        modified: false,
        parameters: getParameters(),
      };
    case CONSTANTS.DISPATCH_PARAMETERS_MODIFIED:
      return {
        ...state,
        modified: true,
        parameters: modifyParameter(),
      };
    case CONSTANTS.DISPATCH_REFERENCE_UPLOADED:
      return {
        ...state,
        files: [...state.files, action.file],
      };
    default:
      throw new Error(`Unhandled action type: ${action.type}`);
  }
}

function LaunchProvider({ children, pipelineId, automated = false }) {
  const [state, dispatch] = useReducer(launchReducer, {
    original: {},
    complete: false,
    fetching: true,
    name: "",
    description: "",
    parameters: [],
  });

  useEffect(() => {
    fetch(setBaseUrl(`/ajax/pipelines/${pipelineId}?automated=${automated}`))
      .then((response) => response.json())
      .then((json) => {
        dispatch({ type: CONSTANTS.DISPATCH_PIPELINE_LOADED, value: json });
      });
  }, [pipelineId, automated]);

  return (
    <LaunchStateContext.Provider value={state}>
      <LaunchDispatchContext.Provider value={dispatch}>
        {children}
      </LaunchDispatchContext.Provider>
    </LaunchStateContext.Provider>
  );
}

function useLaunchState() {
  const context = React.useContext(LaunchStateContext);
  if (typeof context === "undefined") {
    throw new Error(`useLaunchContext must be used within a LaunchProvider`);
  }
  return context;
}

function useLaunchDispatch() {
  const context = React.useContext(LaunchDispatchContext);
  if (typeof context === "undefined") {
    throw new Error(`useLaunchDispatch must be used within a LaunchDispatch`);
  }
  return context;
}

export { LaunchProvider, useLaunchState, useLaunchDispatch };
