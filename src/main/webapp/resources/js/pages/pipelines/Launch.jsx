import React from "react";
import { Alert, Typography } from "antd";

const { Title } = Typography;

export default function Launch() {
  return (
    <>
      <Title>LAUNCH</Title>
      <Alert
        message={"Nothing to see here, you forgot to select samples"}
        description={"Gibberish"}
        type="error"
        showIcon
      />
    </>
  );
}
