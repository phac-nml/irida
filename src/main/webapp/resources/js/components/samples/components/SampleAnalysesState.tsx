import React from "react";
import {
  IconCheckCircle,
  IconClock,
  IconCloseCircle,
  IconLoading,
} from "../../icons/Icons";

import { AnalysisState } from "../../../types/irida";

const commonIconStyle = {
  fontSize: "16px",
};

export interface SampleAnalysesStateProps {
  state: AnalysisState;
}

/**
 * React component to display sample analyses state
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleAnalysesState({
  state,
}: SampleAnalysesStateProps): JSX.Element {
  return (
    <span className="t-analysis-state">
      {state === "COMPLETED" ? (
        <IconCheckCircle
          style={{ ...commonIconStyle, color: `var(--green-6)` }}
        />
      ) : state === "ERROR" ? (
        <IconCloseCircle
          style={{ ...commonIconStyle, color: `var(--red-6)` }}
        />
      ) : state === "NEW" ? (
        <IconClock style={{ ...commonIconStyle, color: `var(--grey-6)` }} />
      ) : (
        <IconLoading style={{ ...commonIconStyle, color: `var(--blue-6)` }} />
      )}
    </span>
  );
}
