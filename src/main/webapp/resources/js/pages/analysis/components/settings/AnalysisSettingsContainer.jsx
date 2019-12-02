import React from "react";
import { AnalysisSamplesProvider } from "../../../../contexts/AnalysisSamplesContext";
import { AnalysisShareProvider } from "../../../../contexts/AnalysisShareContext";
import { AnalysisDetailsProvider } from "../../../../contexts/AnalysisDetailsContext";
import AnalysisSettings from "../AnalysisSettings";

export default function AnalysisSettingsContainer(props) {
  return (
    <AnalysisDetailsProvider>
      <AnalysisSamplesProvider>
        <AnalysisShareProvider>
          <AnalysisSettings props={props} />
        </AnalysisShareProvider>
      </AnalysisSamplesProvider>
    </AnalysisDetailsProvider>
  );
}
