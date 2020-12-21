import React from "react";
import { Divider, Form, Input, Typography } from "antd";
import { SectionHeading } from "../../components/ant.design/SectionHeading";

/**
 * React component for editing the basic information for launching an IRIDA Workflow Pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchDetails() {
  return (
    <section>
      <SectionHeading id="launch-details">
        {i18n("LaunchDetails.label")}
      </SectionHeading>
      <Form.Item
        label={i18n("LaunchDetails.name")}
        name="name"
        rules={[
          {
            required: true,
            message: i18n("LaunchDetails.name.required"),
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item label={i18n("LaunchDetails.description")} name="description">
        <Input.TextArea />
      </Form.Item>
    </section>
  );
}
