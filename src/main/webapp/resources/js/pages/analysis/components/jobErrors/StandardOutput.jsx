/*
 * This file returns a list of the `Standard Output` for
 * a given index. If there is no index provided then just the
 * standard output from the first index in the galaxyJobErrors
 * object is returned.
 */

import React from "react";
import { OutputWrapper } from "../../../../components/OutputFiles/OutputWrapper";

export function StandardOutput({ galaxyJobErrors, currIndex }) {
  // Returns the standard output for the given index from the jobErrors object
  function getStandardOutput(index = 0) {
    return (
      <OutputWrapper overflowRequired={true}>
        {galaxyJobErrors[index].standardOutput.trim()}
      </OutputWrapper>
    );
  }

  return <>{getStandardOutput(currIndex)}</>;
}
