import React from "react";
import { CodeBlock } from "../../../components/typography";

export function StandardError(props) {
  // Returns the standard error for the given index from the jobErrors object
  function getStandardError(index = 0) {
    return (
      <CodeBlock>
        {props.value.galaxyJobErrors[index].standardError.trim()}
      </CodeBlock>
    );
  }

  return <>{getStandardError(props.value.currIndex)}</>;
}
