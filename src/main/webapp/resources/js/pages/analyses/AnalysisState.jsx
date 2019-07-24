import React from "react";
import { Progress, Tag } from "antd";

export function AnalysisState({ state, percentage }) {
  return (
    <div style={{ display: "flex", alignItems: "center" }}>
      {percentage < 100 && state !== "Error" ? (
        <>
          <Progress
            type="circle"
            percent={percentage}
            width={25}
            style={{ paddingRight: 8 }}
          />
          {state}
        </>
      ) : (
        <Tag color={state === "Error" ? "red" : "green"}>{state}</Tag>
      )}
    </div>
  );
}
