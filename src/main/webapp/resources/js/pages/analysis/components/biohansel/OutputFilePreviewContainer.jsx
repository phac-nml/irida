import React from "react";
import { AnalysisOutputsProvider } from "../../../../contexts/AnalysisOutputsContext";
import { OutputFilePreview } from "./OutputFilePreview";

export function OutputFilePreviewContainer({ bioHanselResults }) {
  return (
    <AnalysisOutputsProvider>
      <OutputFilePreview bioHanselResults={bioHanselResults} />
    </AnalysisOutputsProvider>
  );
}
