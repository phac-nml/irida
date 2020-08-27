import React, { useState } from "react";
import { Badge, notification } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { green6 } from "../../styles/colors";
import { IconSyncSpin } from "../icons/Icons";
import { useInterval } from "../../hooks";
import { getUpdatedTableDetails } from "../../apis/analysis/analysis";


/**
 * Display the state of an analysis
 * @param {object} state
 * @param {string} analysisId the analysis identifier
 * @param {number} the polling delay
 * @returns {*}
 * @constructor
 */
export function AnalysisState({ state, analysisId, updateDelay }) {
  const [currStateText, setCurrStateText] = useState(state.text);
  const [currStateValue, setCurrStateValue] = useState(state.value);

  // Update the analysis state using polling
  const intervalId = useInterval(() => {
    if(currStateValue !== "COMPLETED" && currStateValue !== "ERROR") {
      getUpdatedTableDetails(analysisId).then(res => {
        if (state.value !== res.analysisStateModel.value) {
          setCurrStateText(res.analysisStateModel.text);
          setCurrStateValue(res.analysisStateModel.value);
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

  switch (currStateValue) {
    case "NEW":
      return <Badge status="default" text={currStateText} />;
    case "ERROR":
      return <Badge status="error" text={currStateText} />;
    case "COMPLETED":
      return <Badge status="success" text={currStateText} />;
    default:
      return (
        <div>
          <IconSyncSpin style={{ marginRight: SPACE_XS, color: green6 }} />
          {currStateText}
        </div>
      );
  }
}
