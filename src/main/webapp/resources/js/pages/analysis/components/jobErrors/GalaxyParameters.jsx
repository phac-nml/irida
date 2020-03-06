/*
 * This file returns a list of the `Galaxy Parameters` for
 * a given index. If there is no index provided then just the
 * parameters from the first index in the galaxyJobErrors object
 * is returned.
 */

import React from "react";
import { OutputWrapper } from "../../../../components/OutputFiles/OutputWrapper";

export function GalaxyParameters({ galaxyJobErrors, currIndex }) {
  // Returns the galaxy parameters for the given index from the jobErrors object
  function getGalaxyParameters(index = 0) {
    return (
      <OutputWrapper overflowRequired={true}>
        {JSON.stringify(
          JSON.parse(galaxyJobErrors[index].parameters.trim()),
          null,
          3
        )}
      </OutputWrapper>
    );
  }

  return <>{getGalaxyParameters(currIndex)}</>;
}
