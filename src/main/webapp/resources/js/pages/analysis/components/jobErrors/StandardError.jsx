/*
 * This file returns a list of the `Standard Error` for
 * a given index. If there is no index provided then just the
 * standard error from the first index in the galaxyJobErrors
 * object is returned.
 */

import React from "react";
import { OutputWrapper } from "../../../../components/OutputFiles/OutputWrapper";

export function StandardError({ galaxyJobErrors, currIndex }) {
  // Returns the standard error for the given index from the jobErrors object
  function getStandardError(index = 0) {
    return (
      <OutputWrapper overflowRequired={true}>
        {galaxyJobErrors[index].standardError.trim()}
      </OutputWrapper>
    );
  }

  return <>{getStandardError(currIndex)}</>;
}
