import React from "react";
import { Badge, Progress } from "antd";

/**
 * Display the state of an analysis, if it is running show the percentage
 * @param {string} state
 * @param {number} percentage
 * @param {string} type a non-internationalized state
 * @returns {*}
 * @constructor
 */
export function AnalysisState({ state, percentage }) {
  switch (state.value) {
    case "NEW":
      return <Badge status="default" text={state.text} />;
    case "TRANSFERRED":
      return <Badge status="processing" text={state.text} />;
    case "ERROR":
      return <Badge status="error" text={state.text} />;
    case "COMPLETED":
      return <Badge status="success" text={state.text} />;
    default:
      return (
        <div style={{ display: "flex", alignItems: "center" }}>
          <Progress
            type="circle"
            percent={percentage}
            width={25}
            style={{ paddingRight: 8 }}
          />
          {state.text}
        </div>
      );
  }
}
