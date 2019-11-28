/*
 * This file contains the state and functions
 * required for displaying analysis outputs.
 */

import React, { useContext, useEffect, useState } from "react";
import { AnalysisContext } from "../contexts/AnalysisContext";

// Functions required by context
import { getOutputInfo } from "../apis/analysis/analysis";

const initialContext = {
  outputs: null,
  fileTypes: [{ hasJsonFile: false, hasTabularFile: false, hasTextFile: false }]
};

const AnalysisOutputsContext = React.createContext(initialContext);
const jsonExtSet = new Set(["json"]);
const tabExtSet = new Set(["tab", "tsv", "tabular", "csv"]);

function AnalysisOutputsProvider(props) {
  const [analysisOutputsContext, setAnalysisOutputsContext] = useState(
    initialContext
  );
  const { analysisContext } = useContext(AnalysisContext);

  function getAnalysisOutputs() {
    let jsonExists = false;
    let textExists = false;
    let tabularExists = false;

    getOutputInfo(analysisContext.analysis.identifier).then(data => {
      // Check if json, tab, and/or text files exist
      // Used by output file preview to only display
      // tabs that are required
      data.find(function(el) {
        if (!jsonExists && jsonExtSet.has(el.fileExt)) {
          jsonExists = true;
        }

        if (!tabularExists && tabExtSet.has(el.fileExt)) {
          tabularExists = true;
        }

        if (
          !textExists &&
          (!tabExtSet.has(el.fileExt) && !jsonExtSet.has(el.fileExt))
        ) {
          textExists = true;
        }
      });

      setAnalysisOutputsContext(analysisOutputsContext => {
        return {
          ...analysisOutputsContext,
          outputs: data,
          fileTypes: [
            {
              hasJsonFile: jsonExists,
              hasTabularFile: tabularExists,
              hasTextFile: textExists
            }
          ]
        };
      });
    });
  }

  return (
    <AnalysisOutputsContext.Provider
      value={{
        analysisOutputsContext,
        getAnalysisOutputs,
        tabExtSet,
        jsonExtSet
      }}
    >
      {props.children}
    </AnalysisOutputsContext.Provider>
  );
}
export { AnalysisOutputsContext, AnalysisOutputsProvider };
