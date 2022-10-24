/*
 * This file contains the state and functions
 * required for displaying analysis details, and
 * samples as well as, updating analysis (email
 * pipeline result, priority).
 */

import React, { useContext, useEffect, useReducer } from "react";

// Functions required by context
import {
  getVariablesForDetails,
  saveToRelatedSamples,
  updateAnalysis,
  updateAnalysisEmailPipelineResult,
} from "../apis/analysis/analysis";

import { AnalysisContext } from "../contexts/AnalysisContext";
import { notification } from "antd";

const TYPES = {
  DETAILS: "ANALYSIS_DETAILS",
  EMAIL_PIPELINE_RESULT_COMPLETED: "UPDATED_EMAIL_PIPELINE_RESULT_COMPLETED",
  EMAIL_PIPELINE_RESULT_ERROR: "UPDATED_EMAIL_PIPELINE_RESULT_ERROR",
  PRIORITY: "UPDATED_PRIORITY",
  UPDATE_SAMPLES: "UPDATE_SAMPLES",
  UPDATE_DURATION: "UPDATE_DURATION",
};

// Updates the state and returns a new copy.
const reducer = (context, action) => {
  switch (action.type) {
    case TYPES.DETAILS:
      return {
        ...context,
        ...action.payload,
      };
    case TYPES.EMAIL_PIPELINE_RESULT_COMPLETED:
      return {
        ...context,
        emailPipelineResultCompleted: action.emailPipelineResultCompleted,
        emailPipelineResultError: action.emailPipelineResultError,
      };
    case TYPES.EMAIL_PIPELINE_RESULT_ERROR:
      return {
        ...context,
        emailPipelineResultError: action.emailPipelineResultError,
      };
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
  emailPipelineResultCompleted: false,
  emailPipelineResultError: false,
  workflowName: null,
  version: null,
  duration: null,
  createdDate: null,
  priority: null,
  priorities: [],
  canShareToSamples: false, // submission has ability to save results back to samples
  updatePermission: false, // user can update the submission details/parameters
  updateSamples: false, // Update samples from results on completion
  allowedToModifySample: false, // User can modify sample
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
    getVariablesForDetails(analysisIdentifier).then((data) => {
      dispatch({ type: TYPES.DETAILS, payload: data });
    });
  }, [getVariablesForDetails]);

  useEffect(() => {
    dispatch({
      type: TYPES.UPDATE_DURATION,
      duration: analysisContext.duration,
    });
  }, [analysisContext.duration]);

  /*
   * Saves results to related samples and updates
   * `updateSamples` state variable.
   */
  function saveResultsToRelatedSamples() {
    saveToRelatedSamples(analysisIdentifier).then((res) => {
      if (res.type === "error") {
        notification.error({ message: res.text });
      } else {
        notification.success({ message: res });
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
      priority: updatedPriority,
    }).then((res) => {
      if (res.type === "error") {
        notification.error({ message: res.text });
      } else {
        notification.success({ message: res });
        dispatch({ type: TYPES.PRIORITY, priority: updatedPriority });
      }
    });
  }

  /*
   * Updates if a user wants to receive an email on analysis completion,
   * displays a notification to the user, and updates the
   * emailPipelineResult` state variable.
   */
  function analysisDetailsContextUpdateEmailPipelineResult({
    emailPreference,
  }) {
    let emailPipelineResultCompleted = false;
    let emailPipelineResultError = false;

    if (emailPreference === "error") {
      emailPipelineResultError = true;
    } else if (emailPreference === "completed") {
      emailPipelineResultCompleted = true;
      emailPipelineResultError = true;
    }

    updateAnalysisEmailPipelineResult({
      submissionId: analysisIdentifier,
      emailPipelineResultCompleted: emailPipelineResultCompleted,
      emailPipelineResultError: emailPipelineResultError,
    }).then((res) => {
      if (res.type === "error") {
        notification.error({ message: res.text });
      } else {
        notification.success({ message: res });
        dispatch({
          type: TYPES.EMAIL_PIPELINE_RESULT_COMPLETED,
          emailPipelineResultCompleted,
          emailPipelineResultError,
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
        analysisDetailsContextUpdateEmailPipelineResult,
      }}
    >
      {props.children}
    </AnalysisDetailsContext.Provider>
  );
}
export { AnalysisDetailsContext, AnalysisDetailsProvider };
