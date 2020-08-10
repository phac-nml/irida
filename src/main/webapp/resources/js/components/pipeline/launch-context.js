import React, { useEffect } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import * as CONSTANTS from "./lauch-constants";

const LaunchStateContext = React.createContext();
const LaunchDispatchContext = React.createContext();

function launchReducer(state, action) {
  const formatName = (n) => `${n.replace(/ /g, "_")}_${Date.now()}`;

  switch (action.type) {
    case CONSTANTS.DISPATCH_DETAILS_UPDATE:
      return { ...state, [action.field]: action.value };
    case CONSTANTS.DISPATCH_PIPELINE_LOADED:
      return {
        ...state,
        ...action.value,
        fetching: false,
        name: formatName(action.value.name),
      };
    default:
      throw new Error(`Unhandled action type: ${action.type}`);
  }
}

function LaunchProvider({ children, pipelineId, automated = false }) {
  const [state, dispatch] = React.useReducer(launchReducer, {
    fetching: true,
    step: CONSTANTS.STEP_DETAILS,
    name: "",
    description: "",
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
