import React from "react";
import { Spin } from "antd";

export function PageLoader() {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Spin tip={"Loading"} />
    </div>
  );
}
