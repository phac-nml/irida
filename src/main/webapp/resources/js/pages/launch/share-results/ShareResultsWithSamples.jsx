import React from "react";
import { Checkbox, Form } from "antd";

export const ShareResultsWithSamples = () => (
  <Form.Item name="updateSamples" valuePropName="checked">
    <Checkbox>{i18n("ShareResultsWithSamples.label")}</Checkbox>
  </Form.Item>
);
