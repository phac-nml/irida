import { Button, Result } from "antd";
import React from "react";
import { useSelector } from "react-redux";

export function ShareFinish() {
  const { destination = {} } = useSelector((state) => state.reducer);
  return (
    <Result
      status="success"
      title={`Successfully copied 21 samples to ${destination.name}`}
      extra={[
        <Button key="return" type="link">
          Return to 1t project
        </Button>,
        <Button key="go" type="link">
          Return to other project
        </Button>,
      ]}
    />
  );
}
