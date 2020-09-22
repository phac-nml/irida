import React, { useEffect, useReducer, useState } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";
import axios from "axios";
import { PageLoader } from "../../components/page-loader/PageLoader";

const LaunchStateContext = React.createContext();

function launchReducer(state, action) {
  const { field, value } = action;
  switch (action.type) {
    case "loaded":
      return {
        ...state,
        fetching: false,
        pipelineName: action.value.name,
        name: `${action.value.name}_${Date.now()}`,
        description: "",
      };
    case "detail_update":
      return {
        ...state,
        [action.field]: action.value,
      };
    default:
      throw new Error(`Unhandled action type: ${action.type}`);
  }
}

function LaunchProvider({ children, pipelineId, automated = false }) {
  const [loading, setLoading] = useState(true);
  const [state, dispatch] = useReducer(launchReducer, {
    name: "",
    description: "",
  });

  useEffect(() => {
    axios
      .get(setBaseUrl(`/ajax/pipelines/${pipelineId}?automated=${automated}`))
      .then(({ data }) =>
        dispatch({
          type: "loaded",
          value: data,
        })
      )
      .then(() => setLoading(false))
      .catch(() => dispatch({ type: "error" }));
  }, [pipelineId, automated]);

  /**
   * Update one of the fields in the PipelineDetails
   * @param field - which field to update
   * @param value - new value
   * @returns {*}
   */
  const updateDetailsField = ({ field, value }) =>
    dispatch({ type: "detail_update", field, value });

  const launchPipeline = () => {
    console.log("Launching", state);
  };

  return (
    <LaunchStateContext.Provider value={{ ...state, updateDetailsField }}>
      {loading ? <PageLoader /> : children}
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

export { LaunchProvider, useLaunchState };
