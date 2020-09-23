import React, { useEffect, useReducer, useState } from "react";
import { PageLoader } from "../../components/page-loader/PageLoader";
import {
  fetchPipelineDetails,
  launchPipeline,
} from "../../apis/pipelines/launch";

const LaunchStateContext = React.createContext();

function launchReducer(state, action) {
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
    fetchPipelineDetails(pipelineId)
      .then((data) =>
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

  const startPipeline = () => {
    console.log("Launching", state);
    const details = {
      name: state.name,
      description: state.description,
      shareWithProjects: state.shareWithProjects,
    };
    launchPipeline({ id: pipelineId, details }).then((response) =>
      console.log(response)
    );
  };

  return (
    <LaunchStateContext.Provider
      value={{ ...state, updateDetailsField, startPipeline }}
    >
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
