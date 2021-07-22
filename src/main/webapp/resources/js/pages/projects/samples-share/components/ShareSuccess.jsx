import { Button, Result } from "antd";
import React from "react";

export function ShareSuccess({ count, target, current }) {
  return (
    <Result
      status="success"
      title={`Successfully copied ${count} samples to ${target.label}`}
      extra={[
        <Button key="return" type="link">{`Return to `}</Button>,
        <Button key="go" type="link">
          {`Go to ${target.label}`}
        </Button>,
      ]}
    />
  );
}
