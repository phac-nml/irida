import React from "react";
import { CodeBlock } from "../../../components/typography";

export function GalaxyParameters(props) {
  // Returns the galaxy parameters for the given index from the jobErrors object
  function getGalaxyParameters(index = 0) {
    return (
      <CodeBlock>
        {JSON.stringify(
          JSON.parse(props.value.galaxyJobErrors[index].parameters.trim()),
          null,
          3
        )}
      </CodeBlock>
    );
  }

  return <>{getGalaxyParameters(props.value.currIndex)}</>;
}
