/*
 * This file returns a list of the `Standard Error/Output`
 */

import React, { useState } from "react";
import { Button } from "antd";
import { OutputWrapper } from "../../../../components/OutputFiles/OutputWrapper";
import { SPACE_MD, SPACE_XS } from "../../../../styles/spacing";

import {
  IconSortAscending,
  IconSortDescending,
} from "../../../../components/icons/Icons";

export function StandardErrorOutput({ galaxyError }) {
  const error = galaxyError.trim().split("\n");

  const [reversed, setReversed] = useState(false);

  return (
    <div>
      {error.length > 1 ? (
        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            marginBottom: SPACE_MD,
          }}
        >
          <Button type="default" onClick={() => setReversed(!reversed)}>
            <span style={{ marginRight: SPACE_XS }}>
              {reversed ? <IconSortAscending /> : <IconSortDescending />}
            </span>
            {i18n("AnalysisError.reverseOutput")}
          </Button>
        </div>
      ) : (
        ""
      )}
      <OutputWrapper overflowRequired={true}>
        {reversed ? error.reverse().join("\n") : error.join("\n")}
      </OutputWrapper>
    </div>
  );
}
