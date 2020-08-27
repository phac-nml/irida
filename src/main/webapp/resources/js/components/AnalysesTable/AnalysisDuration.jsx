import React, { useState } from "react";
import { notification } from "antd";

import { useInterval } from "../../hooks";
import { getUpdatedTableDetails } from "../../apis/analysis/analysis";
import { getHumanizedDuration } from "../../utilities/date-utilities.js";

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

  // Update the analysis duration using polling
  const intervalId = useInterval(() => {
    if(state.value !== "COMPLETED" && state.value !== "ERROR") {
      getUpdatedTableDetails(analysisId).then(res => {
          if(res.duration !== duration) {
            setCurrDuration(res.duration);
          }
          if (res.analysisStateModel.value === "COMPLETED" || res.analysisStateModel.value === "ERROR") {
            clearInterval(intervalId);
          }
        }).catch((message) => {
          notification.error({message});
          clearInterval(intervalId);
        });
    } else {
      clearInterval(intervalId);
    }

  }, updateDelay);

  return (
    <>
    {getHumanizedDuration({ date: currDuration })}
    </>
  );

}
