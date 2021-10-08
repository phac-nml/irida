import { Button, Card, Result } from "antd";
import React from "react";

export function ShareNoSamples({ redirect }) {
  return (
    <Card>
      <Result
        status="warning"
        title={i18n("ShareNoSamples.description")}
        extra={[
          <Button type="primary" key="sampls" href={redirect}>
            {i18n("ShareNoSamples.link")}
          </Button>,
        ]}
      />
    </Card>
  );
}
