import React, { useState } from "react";
import { Button, notification } from "antd";

import { useInterval } from "../../hooks";
import { getUpdatedTableDetails } from "../../apis/analysis/analysis";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconDownloadFile } from "../icons/Icons";

/**
 * Display the download results button for an analysis
 * @param {string} state The state of the analysis
 * @param {number} analysisId the analysis identifier
 * @param {number} updateDelay the polling delay
 * @returns {*}
 * @constructor
 */
export function AnalysisDownloadButton({ state, analysisId, updateDelay }) {
  const [currState, setCurrState] = useState(state);

  // Get the updated analysis state using polling
  const intervalId = useInterval(() => {
    if(currState !== "COMPLETED" && currState !== "ERROR") {
      getUpdatedTableDetails(analysisId).then(res => {
        if(res.analysisStateModel.value !== state) {
          setCurrState(res.analysisStateModel.value);
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
