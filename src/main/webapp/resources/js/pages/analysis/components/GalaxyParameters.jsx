import React from "react";
import { CodeBlock } from "../../../components/typography";

export function GalaxyParameters({ galaxyJobErrors, currIndex }) {
  // Returns the galaxy parameters for the given index from the jobErrors object
  function getGalaxyParameters(index = 0) {
    return (
      <CodeBlock>
        {JSON.stringify(
          JSON.parse(galaxyJobErrors[index].parameters.trim()),
          null,
          3
        )}
      </CodeBlock>
    );
  }

  return <>{getGalaxyParameters(currIndex)}</>;
}
