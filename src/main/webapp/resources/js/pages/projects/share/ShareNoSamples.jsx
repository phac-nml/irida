import { Button, Card, Result } from "antd";
import React from "react";

export function ShareNoSamples() {
  /*
  Create redirect href to project samples page.
  */
  const [redirect] = React.useState(
    () => window.location.href.match(/(.*)\/share/)[1]
  );

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
