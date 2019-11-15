/*
 * This file contains the state and functions
 * required for displaying analysis outputs.
 */

import React, { useContext, useEffect, useState } from "react";
import { AnalysisContext } from "../contexts/AnalysisContext";

// Functions required by context
import { getOutputInfo } from "../apis/analysis/analysis";

const initialContext = {
  outputs: []
};

const AnalysisOutputsContext = React.createContext(initialContext);

function AnalysisOutputsProvider(props) {
  const [analysisOutputsContext, setAnalysisOutputsContext] = useState(
    initialContext
  );
  const { analysisContext } = useContext(AnalysisContext);

  function getAnalysisOutputs() {
    getOutputInfo(analysisContext.analysis.identifier).then(data => {
      setAnalysisOutputsContext(analysisOutputsContext => {
        return { ...analysisOutputsContext, outputs: data };
      });
    });
  }

  return (
    <AnalysisOutputsContext.Provider
      value={{
        analysisOutputsContext,
        getAnalysisOutputs
      }}
    >
      {props.children}
    </AnalysisOutputsContext.Provider>
  );
}
export { AnalysisOutputsContext, AnalysisOutputsProvider };
