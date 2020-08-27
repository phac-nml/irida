import React, { useState } from "react";
import { Button, notification } from "antd";

import { useInterval } from "../../hooks";
import { getUpdatedTableDetails } from "../../apis/analysis/analysis";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconDownloadFile } from "../icons/Icons";

/**
 * Display the duration of an analysis
 * @param {number} duration The time elapsed for the analysis
 * @param {string} analysisId the analysis identifier
 * @param {number} the polling delay
 * @param {object} the current analysis state
 * @returns {*}
 * @constructor
 */
export function AnalysisDownloadButton({ state, analysisId, updateDelay }) {
  const [currState, setCurrState] = useState(state);

  // Update the analysis duration using polling
  const intervalId = useInterval(() => {
    if(state !== "COMPLETED" && state !== "ERROR") {
      getUpdatedTableDetails(analysisId).then(res => {
        setCurrState(res.analysisStateModel.value);

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
    <Button
      shape="circle-outline"
      disabled={currState !== "COMPLETED"}
      href={setBaseUrl(`ajax/analyses/download/${analysisId}`)}
      download
    >
      <IconDownloadFile />
    </Button>
  );

}
