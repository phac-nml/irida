import React from "react";
import { useLaunch } from "./launch-context";
import { Checkbox, Divider, Form } from "antd";

/**
 * React component to share results with Project and Samples
 * @returns {JSX.Element}
 * @constructor
 */
export function SharePipelineResults() {
  const [{ canUpdateSamples }] = useLaunch();
  return (
    <>
      <Form.Item name="shareResultsWithProjects" valuePropName="checked">
        <Checkbox>{i18n("ShareResultsWithProjects.label")}</Checkbox>
      </Form.Item>
      {canUpdateSamples ? (
        <Form.Item name="updateSamples" valuePropName="checked">
          <Checkbox>{i18n("ShareResultsWithSamples.label")}</Checkbox>
        </Form.Item>
      ) : null}
      <Divider />
    </>
  );
}
