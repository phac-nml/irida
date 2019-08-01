import React from "react";
import { Badge, Progress } from "antd";

/**
 * Display the state of an analysis, if it is running show the percentage
 * @param {string} state
 * @param {number} percentage
 * @returns {*}
 * @constructor
 */
export function AnalysisState({ state, percentage }) {
  return (
    <>
      {percentage < 100 && state !== "Error" ? (
        <div style={{ display: "flex", alignItems: "center" }}>
          <Progress
            type="circle"
            percent={percentage}
            width={25}
            style={{ paddingRight: 8 }}
          />
          {state}
        </div>
      ) : state === "Error" ? (
        <Badge status="error" text={state} />
      ) : (
        <Badge status="success" text={state} />
      )}
    </>
  );
}
