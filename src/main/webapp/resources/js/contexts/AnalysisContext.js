import React, { useState } from "react";
import { showNotification } from "../modules/notifications";

import { updateAnalysis } from "../apis/analysis/analysis";

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

const initialContext = {
  analysis: window.PAGE.analysis,
  analysisName: window.PAGE.analysisName,
  analysisState: window.PAGE.analysisState,
  analysisType: window.PAGE.analysisType,
  isAdmin: window.PAGE.isAdmin,
  stateMap: stateMap,
  isCompleted: window.PAGE.analysisState == "COMPLETED" ? true : false,
  isError: window.PAGE.analysisState.includes("ERROR") ? true : false
};

const AnalysisContext = React.createContext(initialContext);

function AnalysisProvider(props) {
  const [analysisContext, setAnalysisContext] = useState(initialContext);

  function analysisContextUpdateSubmissionName(updatedAnalysisName) {
    if (
      updatedAnalysisName !== "" &&
      updatedAnalysisName !== analysisContext.analysisName
    ) {
      updateAnalysis(
        analysisContext.analysis.identifier,
        updatedAnalysisName,
        null
      ).then(res => {
        showNotification({ text: res.message });
        setAnalysisContext(analysisContext => {
          return { ...analysisContext, analysisName: updatedAnalysisName };
        });
      });
    }
  }

  return (
    <AnalysisContext.Provider
      value={{ analysisContext, analysisContextUpdateSubmissionName }}
    >
      {props.children}
    </AnalysisContext.Provider>
  );
}
export { AnalysisContext, AnalysisProvider };
