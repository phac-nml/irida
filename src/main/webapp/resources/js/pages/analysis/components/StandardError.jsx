import React from "react";
import { CodeBlock } from "../../../components/typography";

export function StandardError({ galaxyJobErrors, currIndex }) {
  // Returns the standard error for the given index from the jobErrors object
  function getStandardError(index = 0) {
    return <CodeBlock>{galaxyJobErrors[index].standardError.trim()}</CodeBlock>;
  }

  return <>{getStandardError(currIndex)}</>;
}
