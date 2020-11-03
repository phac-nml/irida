import React from "react";
import { useLaunchState } from "../launch-context";
import { Checkbox, Form } from "antd";

export function ShareResultsWithSamples() {
  const { canUpdateSamples } = useLaunchState();
  return canUpdateSamples ? (
    <Form.Item name="updateSamples" valuePropName="checked">
      <Checkbox>{i18n("ShareResultsWithSamples.label")}</Checkbox>
    </Form.Item>
  ) : null;
}
