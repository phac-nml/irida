import React, { useReducer } from "react";

const TYPES = {
  DETAILS: "ANALYSIS_DETAILS",
  ANALYSIS_NAME: "UPDATED_ANALYSIS_NAME",
  EMAIL_PIPELINE_RESULT: "UPDATED_EMAIL_PIPELINE_RESULT",
  PRIORITY: "UPDATED_PRIORITY",
  SAMPLES: "SAMPLES_DATA",
  UPDATE_SAMPLES: "UPDATE_SAMPLES"
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

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.DETAILS:
      return {
        ...state,
        emailPipelineResult: action.emailPipelineResult,
        priority: action.priority,
        workflowName: action.workflowName,
        version: action.version,
        analysisCreatedDate: action.analysisCreatedDate,
        duration: action.duration,
        priorities: action.priorities,
        canShareToSamples: action.canShareToSamples
      };
    case TYPES.ANALYSIS_NAME:
      return { ...state, analysisName: action.analysisName };
    case TYPES.EMAIL_PIPELINE_RESULT:
      return { ...state, emailPipelineResult: action.emailPipelineResult };
    case TYPES.PRIORITY:
      return { ...state, priority: action.priority };
    case TYPES.UPDATE_SAMPLES:
      return { ...state, updateSamples: action.updateSamples };
    case TYPES.SAMPLES:
      return {
        ...state,
        samples: action.samples,
        sequenceFilePairList: action.sequenceFilePairList,
        sequenceFileSizeList: action.sequenceFileSizeList,
        referenceFile: action.referenceFile
      };
    default:
      return;
  }
};

const initialState = {
  analysis: window.PAGE.analysis,
  analysisName: window.PAGE.analysis.name,
  analysisState: window.PAGE.analysisState,
  analysisType: window.PAGE.analysisType,
  emailPipelineResult: window.PAGE.analysis.emailPipelineResult,
  workflowName: null,
  version: null,
  updatePermission: window.PAGE.updatePermission,
  duration: null,
  isAdmin: window.PAGE.isAdmin,
  stateMap: stateMap,
  analysisCreatedDate: null,
  canShareToSamples: false,
  isCompleted: window.PAGE.analysisState == "COMPLETED" ? true : false,
  isError: window.PAGE.analysisState.includes("ERROR") ? true : false,
  priority: null,
  priorities: [],
  samples: [],
  sequenceFilePairList: [],
  sequenceFileSizeList: [],
  referenceFile: [],
  updateSamples:
    window.PAGE.analysis.updateSamples == null
      ? false
      : window.PAGE.analysis.updateSamples
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
