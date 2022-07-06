import React from "react";
import {
  IconCheckCircle,
  IconClock,
  IconCloseCircle,
  IconLoading,
} from "../../icons/Icons";
import { blue6, green6, grey6, red6 } from "../../../styles/colors";

const commonIconStyle = {
  fontSize: "16px",
};

export interface SampleAnalysesStateProps {
  state: string;
}

/**
 * React component to display sample analyses state
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleAnalysesState({ state }: SampleAnalysesStateProps): JSX.Element {
  return (
    <span className="t-analysis-state">
      {state === "COMPLETED" ? (
        <IconCheckCircle style={{ ...commonIconStyle, color: green6 }} />
      ) : state === "ERROR" ? (
        <IconCloseCircle style={{ ...commonIconStyle, color: red6 }} />
      ) : state === "QUEUED" ? (
        <IconClock style={{ ...commonIconStyle, color: grey6 }} />
      ) : (
        <IconLoading style={{ ...commonIconStyle, color: blue6 }} />
      )}
    </span>
  );
}
