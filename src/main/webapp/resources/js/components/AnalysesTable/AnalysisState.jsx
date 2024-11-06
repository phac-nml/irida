import React, { useContext, useState } from "react";
import { Badge } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { green6 } from "../../styles/colors";
import { IconSyncSpin } from "../icons/Icons";
import { useInterval } from "../../hooks";

import { AnalysesTableContext } from "../../contexts/AnalysesTableContext";

/**
 * Display the state of an analysis
 * @param {object} state
 * @param {string} analysisId the analysis identifier
 * @param {number} the polling delay
 * @returns {*}
 * @constructor
 */
export function AnalysisState({ state, analysisId, updateDelay }) {
  const { analysesTableContext, updateRowData } =
    useContext(AnalysesTableContext);
  const [currStateText, setCurrStateText] = useState(state.text);
  const [currStateValue, setCurrStateValue] = useState(state.value);

  // Update the analysis state using polling
  const intervalId = useInterval(() => {
    if (state.value !== "COMPLETED" && state.value !== "ERROR") {
      updateRowData(analysisId);

      let rowData = analysesTableContext.rows.filter(
        (row) => row.identifier === analysisId
      );
      let currRowData = rowData[rowData.length - 1];
      if (typeof currRowData !== "undefined") {
        if (currStateText !== currRowData.analysisState.text) {
          setCurrStateText(currRowData.analysisState.text);
        }

        if (currStateValue !== currRowData.analysisState.value) {
          setCurrStateValue(currRowData.analysisState.value);
        }

        if (currRowData.isCompleted || currRowData.isError) {
          clearInterval(intervalId);
        }
      }
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
