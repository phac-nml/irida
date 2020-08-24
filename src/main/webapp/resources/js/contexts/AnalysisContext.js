/*
 * This file loads basic analysis info from the server.
 */

import React, { useContext, useEffect, useState } from "react";
import { showNotification } from "../modules/notifications";

// Functions required by context
import { getNewickTree, updateAnalysis } from "../apis/analysis/analysis";
import { getUpdatedDetails } from "../apis/analysis/analysis";
import { AnalysisDetailsContext} from "./AnalysisDetailsContext";
/*
 * Since we are using Steps and only want to display
 * certain ones, we group some of the analysis states
 * together.
 */
export const stateMap = {
  NEW: 0,
  PREPARING: 1,
  PREPARED: 1,
  SUBMITTING: 2,
  RUNNING: 3,
  FINISHED_RUNNING: 3,
  POST_PROCESSING: 4,
  TRANSFERRING: 4,
  COMPLETING: 4,
  COMPLETED: 5
};

export const isAdmin = window.PAGE.isAdmin;

const initialContext = {
  analysis: window.PAGE.analysis,
  analysisName: window.PAGE.analysisName,
  analysisState: window.PAGE.analysisState,
  analysisType: window.PAGE.analysisType.type,
  isCompleted: window.PAGE.analysisState === "COMPLETED",
  isError: window.PAGE.analysisState.includes("ERROR"),
  previousState: window.PAGE.previousState,
  duration: window.PAGE.duration
};

const AnalysisContext = React.createContext(initialContext);

function AnalysisProvider(props) {
  const [analysisContext, setAnalysisContext] = useState(initialContext);

  // Update the analysis details that
  // are required to display the progression
  // using polling
  useEffect(() => {
      const interval = setInterval(() => {
        getUpdatedDetails(analysisContext.analysis.identifier).then(res => {
          setAnalysisContext(analysisContext => {
            return {
              ...analysisContext,
              analysisState: res.analysisState,
              isCompleted: res.analysisState === "COMPLETED",
              isError: res.analysisState.includes("ERROR"),
              previousState: res.previousState,
              duration: res.duration
            };
          });
          if(res.analysisState === "COMPLETED" || res.analysisState.includes("ERROR")) {
            clearInterval(interval);
          }
        })
      }, 5000);
      return () => clearInterval(interval);
  }, []);

  /*
   * Updates the submission name, displays a notification
   * to the user, and updates the `analysisName` state variable.
   */
  function analysisContextUpdateSubmissionName(updatedAnalysisName) {
    updateAnalysis({
      submissionId: analysisContext.analysis.identifier,
      analysisName: updatedAnalysisName,
      priority: null
    }).then(message => {
      showNotification({ text: message });
      setAnalysisContext(analysisContext => {
        return { ...analysisContext, analysisName: updatedAnalysisName };
      });
    });
  }

  return (
    <AnalysisContext.Provider
      value={{
        analysisContext,
        analysisContextUpdateSubmissionName
      }}
    >
      {props.children}
    </AnalysisContext.Provider>
  );
}
export { AnalysisContext, AnalysisProvider };
