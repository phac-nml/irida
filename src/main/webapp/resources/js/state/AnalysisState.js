import React, { useReducer } from "react";

const reducer = (state, action) => {
  switch (action.type) {
    case "analysisName":
      return { ...state, analysisName: action.analysisName };
    case "emailPipelineResult":
      return { ...state, emailPipelineResult: action.emailPipelineResult };
    case "priority":
      return { ...state, priority: action.priority };
    case "workflowName":
      return { ...state, workflowName: action.workflowName };
    case "version":
      return { ...state, version: action.version };
    case "analysisCreatedDate":
      return { ...state, analysisCreatedDate: action.analysisCreatedDate };
    case "duration":
      return { ...state, duration: action.duration };
    case "priorities":
      return { ...state, priorities: action.priorities };
    case "canShareToSamples":
      return { ...state, canShareToSamples: action.canShareToSamples };
    default:
      return;
  }
};

const initialState = {
  analysis: window.PAGE.analysis,
  analysisName: window.PAGE.analysis.name,
  analysisState: window.PAGE.analysisState,
  analysisType: window.PAGE.analysisType,
  emailPipelineResult: false,
  workflowName: null,
  version: null,
  updatePermission: window.PAGE.updatePermission,
  duration: null,
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
  analysisCreatedDate: null,
  canShareToSamples: false,
  isCompleted: window.PAGE.analysisState == "COMPLETED" ? true : false,
  isError: window.PAGE.analysisState == "ERROR" ? true : false,
  priority: null,
  priorities: []
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
