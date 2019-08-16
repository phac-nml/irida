import React, { useReducer } from "react";

const TYPES = {
  ANALYSIS_NAME: "UPDATED_ANALYSIS_NAME"
};

const stateMap = {
  NEW: 0,
  PREPARING: 1,
  SUBMITTING: 2,
  RUNNING: 3,
  FINISHED_RUNNING: 3,
  POST_PROCESSING: 4,
  TRANSFERRING: 4,
  COMPLETING: 4,
  COMPLETED: 5
};

const reducer = (context, action) => {
  console.log(action);
  switch (action.type) {
    case TYPES.ANALYSIS_NAME:
      return { ...context, analysisName: action.payload.analysisName };
    default:
      return;
  }
};

const initialContext = {
  analysis: window.PAGE.analysis,
  analysisName: window.PAGE.analysis.name,
  analysisState: window.PAGE.analysisState,
  analysisType: window.PAGE.analysisType,
  isAdmin: window.PAGE.isAdmin,
  stateMap: stateMap,
  isCompleted: window.PAGE.analysisState == "COMPLETED" ? true : false,
  isError: window.PAGE.analysisState.includes("ERROR") ? true : false
};

export const actions = {
  updateSubmissionName: analysisName => ({
    type: TYPES.ANALYSIS_NAME,
    payload: {
      analysisName
    }
  })
};

const AnalysisContext = React.createContext(initialContext);

function AnalysisProvider(props) {
  const [context, dispatch] = useReducer(reducer, initialContext);

  return (
    <AnalysisContext.Provider value={{ context, dispatch }}>
      {props.children}
    </AnalysisContext.Provider>
  );
}
export { AnalysisContext, AnalysisProvider };
