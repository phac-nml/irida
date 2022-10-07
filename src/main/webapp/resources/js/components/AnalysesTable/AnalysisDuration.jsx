import React, { useContext, useState } from "react";

import { useInterval } from "../../hooks";
import { getHumanizedDuration } from "../../utilities/date-utilities";
import { AnalysesTableContext } from "../../contexts/AnalysesTableContext";

/**
 * Display the duration of an analysis
 * @param {number} duration The time elapsed for the analysis
 * @param {string} analysisId the analysis identifier
 * @param {number} the polling delay
 * @param {object} the current analysis state
 * @returns {*}
 * @constructor
 */
export function AnalysisDuration({ duration, analysisId, updateDelay, state }) {
  const [currDuration, setCurrDuration] = useState(duration);
  const { analysesTableContext } = useContext(AnalysesTableContext);

  // Update the analysis duration using polling
  const intervalId = useInterval(() => {
    if (state.value !== "COMPLETED" && state.value !== "ERROR") {
      let rowData = analysesTableContext.rows.filter(
        (row) => row.identifier === analysisId
      );
      let currRowData = rowData[rowData.length - 1];
      if (typeof currRowData !== "undefined") {
        if (currDuration !== currRowData.analysisDuration) {
          setCurrDuration(currRowData.analysisDuration);
        }

        if (currRowData.isCompleted || currRowData.isError) {
          clearInterval(intervalId);
        }
      }
    } else {
      clearInterval(intervalId);
    }
  }, updateDelay);

  return <>{getHumanizedDuration({ date: currDuration })}</>;
}
