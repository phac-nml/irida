/*
 * This file contains the state and functions
 * required for displaying analysis samples.
 */

import React, { useContext, useEffect, useState } from "react";
import { AnalysisContext } from "../contexts/AnalysisContext";

// Functions required by context
import { getAnalysisInputFiles } from "../apis/analysis/analysis";

const initialContext = {
  samples: [],
  sequenceFilePairList: [],
  sequenceFileSizeList: [],
  referenceFile: []
};

const AnalysisSamplesContext = React.createContext(initialContext);

function AnalysisSamplesProvider(props) {
  const [analysisSamplesContext, setAnalysisSamplesContext] = useState(
    initialContext
  );
  const { analysisContext } = useContext(AnalysisContext);

  useEffect(() => {
    getAnalysisInputFiles(analysisContext.analysis.identifier).then(
      ({
        samples,
        sequenceFilePairList,
        sequenceFileSizeList,
        referenceFile
      }) => {
        setAnalysisSamplesContext(analysisSamplesContext => {
          return {
            ...analysisSamplesContext,
            samples: samples,
            sequenceFilePairList: sequenceFilePairList,
            sequenceFileSizeList: sequenceFileSizeList,
            referenceFile: referenceFile
          };
        });
      }
    );
  }, [getAnalysisInputFiles]);

  return (
    <AnalysisSamplesContext.Provider
      value={{
        analysisSamplesContext
      }}
    >
      {props.children}
    </AnalysisSamplesContext.Provider>
  );
}
export { AnalysisSamplesContext, AnalysisSamplesProvider };
