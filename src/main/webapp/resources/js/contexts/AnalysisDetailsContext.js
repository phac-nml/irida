/*
 * This file contains the state and functions
 * required for displaying analysis details, and
 * samples as well as, updating analysis (email
 * pipeline result, priority).
 */

import React, { useReducer, useContext, useEffect } from "react";

// Functions required by context
import {
  getVariablesForDetails,
  saveToRelatedSamples,
  updateAnalysisEmailPipelineResult,
  updateAnalysis
} from "../apis/analysis/analysis";

import {
  showNotification,
  showErrorNotification
} from "../modules/notifications";
import { AnalysisContext } from "../contexts/AnalysisContext";

const TYPES = {
  DETAILS: "ANALYSIS_DETAILS",
  EMAIL_PIPELINE_RESULT: "UPDATED_EMAIL_PIPELINE_RESULT",
  PRIORITY: "UPDATED_PRIORITY",
  UPDATE_SAMPLES: "UPDATE_SAMPLES",
  UPDATE_DURATION: "UPDATE_DURATION"
};

// Updates the state and returns a new copy.
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
    case TYPES.UPDATE_DURATION:
      return { ...context, duration: action.duration };
    default:
      return;
  }
};

const initialContext = {
  analysisDescription: null,
  emailPipelineResult: false,
  workflowName: null,
  version: null,
  duration: null,
  createdDate: null,
  priority: null,
  priorities: [],
  canShareToSamples: false,
  updatePermission: false,
  updateSamples: false
};

const AnalysisDetailsContext = React.createContext(initialContext);

function AnalysisDetailsProvider(props) {
  const [analysisDetailsContext, dispatch] = useReducer(
    reducer,
    initialContext
  );
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);

  // On page load get the analysis details
  useEffect(() => {
    getVariablesForDetails(analysisIdentifier).then(data => {
      dispatch({ type: TYPES.DETAILS, payload: data });
    });
  }, [getVariablesForDetails]);

  useEffect(() => {
    dispatch({ type: TYPES.UPDATE_DURATION, duration: analysisContext.duration });
  }, [analysisContext.duration]);

  /*
   * Saves results to related samples and updates
   * `updateSamples` state variable.
   */
  function saveResultsToRelatedSamples() {
    saveToRelatedSamples(analysisIdentifier).then(res => {
      if (res.type === "error") {
        showErrorNotification({ text: res.text, type: res.type });
      } else {
        showNotification({ text: res });
        dispatch({ type: TYPES.UPDATE_SAMPLES, updateSamples: true });
      }
    });
  }

  /*
   * Updates the submission priority, displays a notification
   * to the user, and updates the `priority` state variable.
   */
  function analysisDetailsContextUpdateSubmissionPriority(updatedPriority) {
    updateAnalysis({
      submissionId: analysisIdentifier,
      analysisName: null,
      priority: updatedPriority
    }).then(res => {
      if (res.type === "error") {
        showErrorNotification({ text: res.text, type: res.type });
      } else {
        showNotification({ text: res });
        dispatch({ type: TYPES.PRIORITY, priority: updatedPriority });
      }
    });
  }

  /*
   * Updates if a user wants to receive an email on analysis completion,
   * displays a notification to the user, and updates the
   * emailPipelineResult` state variable.
   */
  function analysisDetailsContextUpdateEmailPipelineResult(
    emailPipelineResult
  ) {
    updateAnalysisEmailPipelineResult({
      submissionId: analysisIdentifier,
      emailPipelineResult: emailPipelineResult
    }).then(res => {
      if (res.type === "error") {
        showErrorNotification({ text: res.text, type: res.type });
      } else {
        showNotification({ text: res });
        dispatch({
          type: TYPES.EMAIL_PIPELINE_RESULT,
          emailPipelineResult
        });
      }
    });
  }

  return (
    <AnalysisDetailsContext.Provider
      value={{
        analysisDetailsContext,
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
