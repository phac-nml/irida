/*
 * This file returns a list of the `Standard Output` for
 * a given index. If there is no index provided then just the
 * standard output from the first index in the galaxyJobErrors
 * object is returned.
 */

import React, { useState } from "react";
import { Button } from "antd";
import { OutputWrapper } from "../../../../components/OutputFiles/OutputWrapper";
import { SPACE_XS } from "../../../../styles/spacing";

export function StandardOutput({ galaxyJobErrors, currIndex }) {
  const index = currIndex || 0;
  const [errorOutput, setErrorOutput] = useState(
    galaxyJobErrors[index].standardOutput.trim().split("\n")
  );

  return (
    <div>
      {errorOutput.length > 1 ? (
        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            marginBottom: SPACE_XS
          }}
        >
          <Button
            type="default"
            onClick={() =>
              setErrorOutput(prevErrorOutput => [...prevErrorOutput.reverse()])
            }
          >
            <i
              style={{ marginRight: SPACE_XS }}
              className="fa fa-sort-amount-up"
              aria-hidden="true"
            ></i>
            {i18n("AnalysisError.reverseOutput")}
          </Button>
        </div>
      ) : (
        ""
      )}
      <OutputWrapper overflowRequired={true}>
        {errorOutput.join("\n")}
      </OutputWrapper>
    </div>
  );
}
