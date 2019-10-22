import React from "react";
import { CodeBlock } from "../../../components/typography";

export function StandardOutput(props) {

    // Returns the standard output for the given index from the jobErrors object
    function getStandardOutput(index=0) {
        return (
          <CodeBlock>
              {props.value.galaxyJobErrors[index].standardOutput.trim()}
            </CodeBlock>
        );
    }

    return (
        <>
            {getStandardOutput(props.value.currIndex)}
        </>
    );
}