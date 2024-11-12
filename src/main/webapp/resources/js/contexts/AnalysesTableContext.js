/*
 * This file gets the state and duration for
 * an analysis.
 */

import React, { useState } from "react";
import { getUpdatedTableDetails } from "../apis/analysis/analysis";
import { notification } from "antd";

const initialContext = {
  rows: [{}],
};

const AnalysesTableContext = React.createContext(initialContext);

function AnalysesTableProvider(props) {
  const [analysesTableContext, setAnalysesTableContext] =
    useState(initialContext);

  /*
   * This function gets the analysis duration and state, and
   * sets if it is completed or has errored.
   */
  function updateRowData(analysisId) {
    getUpdatedTableDetails(analysisId)
      .then((res) => {
        let currRowData = {
          identifier: analysisId,
          analysisState: res.analysisStateModel,
          analysisDuration: res.duration,
          isCompleted: res.completed,
          isError: res.error,
        };

        setAnalysesTableContext((analysesTableContext) => {
          return {
            ...analysesTableContext,
            rows: [...analysesTableContext.rows, currRowData],
          };
        });
      })
      .catch((message) => {
        notification.error({ message });
      });
  }

  return (
    <AnalysesTableContext.Provider
      value={{
        analysesTableContext,
        updateRowData,
      }}
    >
      {props.children}
    </AnalysesTableContext.Provider>
  );
}
export { AnalysesTableContext, AnalysesTableProvider };
