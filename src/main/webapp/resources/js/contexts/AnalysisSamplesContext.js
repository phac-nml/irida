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
  referenceFile: [],
  loading: true
};

const AnalysisSamplesContext = React.createContext(initialContext);

function AnalysisSamplesProvider(props) {
  const [analysisSamplesContext, setAnalysisSamplesContext] = useState(
    initialContext
  );
  const { analysisContext } = useContext(AnalysisContext);
  const [sampleDisplayHeight, setSampleDisplayHeight] = useState(null);

  useEffect(() => {
    updateHeight();
    getAnalysisInputFiles(analysisContext.analysis.identifier).then(
      ({ samples, referenceFile }) => {
        setAnalysisSamplesContext(analysisSamplesContext => {
          return {
            ...analysisSamplesContext,
            samples: samples,
            referenceFile: referenceFile,
            loading: false
          };
        });
      }
    );
  }, [getAnalysisInputFiles]);

  /*
    On page load gets the max height
    for the sample display
  */
  const updateHeight = () => {
    const DEFAULT_MAX_HEIGHT = 600;

    if (window.innerHeight > DEFAULT_MAX_HEIGHT) {
      const TOP_BUFFER = 400;
      const BOTTOM_BUFFER = 90;
      const newHeight = window.innerHeight - TOP_BUFFER - BOTTOM_BUFFER;
      setSampleDisplayHeight(newHeight);
    } else {
      // Minimum sample display height set to 600px
      setSampleDisplayHeight(DEFAULT_MAX_HEIGHT);
    }
  };

  return (
    <AnalysisSamplesContext.Provider
      value={{
        analysisSamplesContext,
        sampleDisplayHeight
      }}
    >
      {props.children}
    </AnalysisSamplesContext.Provider>
  );
}
export { AnalysisSamplesContext, AnalysisSamplesProvider };
