import React, { useReducer } from "react";

const reducer = (state, action) => {
  switch (action.type) {
    case "analysisName":
      return { ...state, analysisName: action.analysisName };
    case "emailPipelineResult":
      return { ...state, emailPipelineResult: action.emailPipelineResult };
    case "priority":
      return { ...state, priority: action.priority };
    default:
      return;
  }
};

const initialState = {
  analysis: window.PAGE.analysis,
  analysisName: window.PAGE.analysis.name,
  analysisState: window.PAGE.analysisState,
  analysisType: window.PAGE.analysisType,
  emailPipelineResult: window.PAGE.analysisEmailPipelineResult,
  workflowName: window.PAGE.workflowName,
  version: window.PAGE.version,
  updatePermission: window.PAGE.updatePermission,
  duration: window.PAGE.duration,
  isAdmin: window.PAGE.isAdmin,
  stateMap: {
    NEW: 0,
    PREPARING: 1,
    SUBMITTING: 2,
    RUNNING: 3,
    COMPLETING: 4,
    COMPLETED: 5,
    ERROR: 6
  },
  analysisCreatedDate: window.PAGE.analysisCreatedDate,
  canShareToSamples: window.PAGE.canShareToSamples,
  isCompleted: window.PAGE.analysisState == "COMPLETED" ? true : false,
  isError: window.PAGE.analysisState == "ERROR" ? true : false,
  priority: window.PAGE.priority
};

const AnalysisContext = React.createContext(initialState);

function AnalysisProvider(props) {
  const [state, dispatch] = useReducer(reducer, initialState);

  return (
    <AnalysisContext.Provider value={{ state, dispatch }}>
      {props.children}
    </AnalysisContext.Provider>
  );
}
export { AnalysisContext, AnalysisProvider };
