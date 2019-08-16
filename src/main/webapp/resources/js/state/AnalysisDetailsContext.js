import React, { useReducer } from "react";

const TYPES = {
  DETAILS: "ANALYSIS_DETAILS",
  ANALYSIS_NAME: "UPDATED_ANALYSIS_NAME",
  EMAIL_PIPELINE_RESULT: "UPDATED_EMAIL_PIPELINE_RESULT",
  PRIORITY: "UPDATED_PRIORITY",
  SAMPLES: "SAMPLES_DATA",
  UPDATE_SAMPLES: "UPDATE_SAMPLES"
};

const reducer = (context, action) => {
  switch (action.type) {
    case TYPES.ANALYSIS_NAME:
      return { ...context, analysisName: action.analysisName };
    case TYPES.DETAILS:
      return {
        ...context,
        emailPipelineResult: action.emailPipelineResult,
        priority: action.priority,
        workflowName: action.workflowName,
        version: action.version,
        analysisCreatedDate: action.analysisCreatedDate,
        duration: action.duration,
        priorities: action.priorities,
        canShareToSamples: action.canShareToSamples
      };
    case TYPES.EMAIL_PIPELINE_RESULT:
      return { ...context, emailPipelineResult: action.emailPipelineResult };
    case TYPES.PRIORITY:
      return { ...context, priority: action.priority };
    case TYPES.UPDATE_SAMPLES:
      return { ...context, updateSamples: action.updateSamples };
    case TYPES.SAMPLES:
      return {
        ...context,
        samples: action.samples,
        sequenceFilePairList: action.sequenceFilePairList,
        sequenceFileSizeList: action.sequenceFileSizeList,
        referenceFile: action.referenceFile
      };
    default:
      return;
  }
};

const initialContext = {
  analysis: window.PAGE.analysis,
  analysisState: window.PAGE.analysisState,
  analysisName: window.PAGE.analysis.name,
  emailPipelineResult: window.PAGE.analysis.emailPipelineResult,
  workflowName: null,
  version: null,
  duration: null,
  isAdmin: window.PAGE.isAdmin,
  analysisCreatedDate: null,
  priority: null,
  priorities: [],
  canShareToSamples: false,
  updatePermission: window.PAGE.updatePermission,
  samples: [],
  sequenceFilePairList: [],
  sequenceFileSizeList: [],
  referenceFile: [],
  updateSamples:
    window.PAGE.analysis.updateSamples == null
      ? false
      : window.PAGE.analysis.updateSamples
};

const AnalysisDetailsContext = React.createContext(initialContext);

function AnalysisDetailsProvider(props) {
  const [context, dispatch] = useReducer(reducer, initialContext);

  return (
    <AnalysisDetailsContext.Provider value={{ context, dispatch }}>
      {props.children}
    </AnalysisDetailsContext.Provider>
  );
}
export { AnalysisDetailsContext, AnalysisDetailsProvider };
