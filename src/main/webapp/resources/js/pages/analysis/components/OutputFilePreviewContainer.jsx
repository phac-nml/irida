import React from "react";
import { AnalysisOutputsProvider } from "../../../contexts/AnalysisOutputsContext";
import { OutputFilePreview } from "./OutputFilePreview";

export default function OutputFilePreviewContainer() {
  return (
    <AnalysisOutputsProvider>
      <OutputFilePreview />
    </AnalysisOutputsProvider>
  );
}
