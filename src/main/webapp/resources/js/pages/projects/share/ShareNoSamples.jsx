import { Button, Card, Result } from "antd";
import React from "react";

/**
 * React component to display to a user if no samples can be shared / moved
 * to the selected project.
 *
 * @param {string} redirect - url to redirect the user to
 * @returns {JSX.Element}
 * @constructor
 */
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
