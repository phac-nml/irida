import React, { useReducer, useContext } from "react";

import {
  getVariablesForDetails,
  getAnalysisInputFiles,
  saveToRelatedSamples,
  updateAnalysisEmailPipelineResult,
  updateAnalysis
} from "../apis/analysis/analysis";

import { showNotification } from "../modules/notifications";
import { AnalysisContext } from "../contexts/AnalysisContext";

const TYPES = {
  DETAILS: "ANALYSIS_DETAILS",
  EMAIL_PIPELINE_RESULT: "UPDATED_EMAIL_PIPELINE_RESULT",
  PRIORITY: "UPDATED_PRIORITY",
  SAMPLES: "SAMPLES_DATA",
  UPDATE_SAMPLES: "UPDATE_SAMPLES"
};

const reducer = (context, action) => {
  switch (action.type) {
    case TYPES.DETAILS:
      return {
        ...context,
        ...action.payload
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
        ...action.payload
      };
    default:
      return;
  }
};

const initialContext = {
  emailPipelineResult: window.PAGE.analysis.emailPipelineResult,
  workflowName: null,
  version: null,
  duration: null,
  createdDate: null,
  priority: null,
  priorities: [],
  canShareToSamples: false,
  updatePermission: false,
  samples: [],
  sequenceFilePairList: [],
  sequenceFileSizeList: [],
  referenceFile: [],
  updateSamples: window.PAGE.analysis.updateSamples
};

const AnalysisDetailsContext = React.createContext(initialContext);

function AnalysisDetailsProvider(props) {
  const [analysisDetailsContext, dispatch] = useReducer(
    reducer,
    initialContext
  );
  const { analysisContext } = useContext(AnalysisContext);

  function loadAnalysisDetails() {
    getVariablesForDetails(analysisContext.analysis.identifier).then(data => {
      dispatch({ type: TYPES.DETAILS, payload: data });
    });
  }

  function loadAnalysisSamples() {
    getAnalysisInputFiles(analysisContext.analysis.identifier).then(data => {
      dispatch({ type: TYPES.SAMPLES, payload: data });
    });
  }

  function saveResultsToRelatedSamples() {
    saveToRelatedSamples(analysisContext.analysis.identifier).then(res => {
      showNotification({ text: res.message });
      dispatch({ type: TYPES.UPDATE_SAMPLES, updateSamples: true });
    });
  }

  function analysisDetailsContextUpdateSubmissionPriority(updatedPriority) {
    updateAnalysis(
      analysisContext.analysis.identifier,
      null,
      updatedPriority
    ).then(res => {
      showNotification({ text: res.message });
      dispatch({ type: TYPES.PRIORITY, priority: updatedPriority });
    });
  }

  function analysisDetailsContextUpdateEmailPipelineResult(
    emailPipelineResult
  ) {
    updateAnalysisEmailPipelineResult(
      analysisContext.analysis.identifier,
      emailPipelineResult
    ).then(res => {
      showNotification({ text: res.message });
      dispatch({
        type: TYPES.EMAIL_PIPELINE_RESULT,
        emailPipelineResult: emailPipelineResult
      });
    });
  }

  return (
    <AnalysisDetailsContext.Provider
      value={{
        analysisDetailsContext,
        loadAnalysisDetails,
        loadAnalysisSamples,
        saveResultsToRelatedSamples,
        analysisDetailsContextUpdateSubmissionPriority,
        analysisDetailsContextUpdateEmailPipelineResult
      }}
    >
      {props.children}
    </AnalysisDetailsContext.Provider>
  );
}
export { AnalysisDetailsContext, AnalysisDetailsProvider };
