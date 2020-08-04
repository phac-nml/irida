import React from "react";
import { Alert, Typography } from "antd";
import { LaunchProvider } from "./launch-context";

const { Title } = Typography;

export default function Launch() {
  return (
    <LaunchProvider>
      <Title>LAUNCH</Title>
      <Alert
        message={"Nothing to see here, you forgot to select samples"}
        description={"Gibberish"}
        type="error"
        showIcon
      />
    </LaunchProvider>
  );
}
