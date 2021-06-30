import { Button, Result } from "antd";
import React from "react";

export function ShareFinish() {
  return (
    <Result
      status="success"
      title={`Successfully copied 21 samples to another project`}
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
