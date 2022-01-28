import React, { useContext } from "react";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { Error, Running, Success } from "../../../components/icons";

/**
 * React component to render the title of analysis with the appropriate
 * icon to reflect status.
 * @returns React.ReactElement
 */
export default function AnalysisTitle() {
  const {
    analysisContext: { isLoading, isCompleted, isError, analysisName },
  } = useContext(AnalysisContext);
  return isLoading ? null : (
    <>
      {isCompleted ? <Success /> : isError ? <Error /> : <Running />}
      {analysisName}
    </>
  );
}
