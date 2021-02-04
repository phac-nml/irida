import React from "react";
import { Form, Input, Select } from "antd";
import { SectionHeading } from "../../components/ant.design/SectionHeading";

/**
 * React component for editing the basic information for launching an IRIDA Workflow Pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchDetails() {
  return (
    <section className="t-launch-details">
      <SectionHeading id="launch-details">
        {i18n("LaunchDetails.label")}
      </SectionHeading>
      <Form.Item
        className="t-name-control"
        label={i18n("LaunchDetails.name")}
        name="name"
        rules={[
          {
            required: true,
            message: (
              <div className="t-name-required">
                {i18n("LaunchDetails.name.required")}
              </div>
            ),
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item label={i18n("LaunchDetails.description")} name="description">
        <Input.TextArea />
      </Form.Item>
      <Form.Item
        name="emailPipelineResult"
        label={i18n("LaunchDetails.emailResults")}
      >
        <Select className="t-email-results">
          <Select.Option value="none">
            {i18n("AnalysisDetailsEmailPref.noEmail")}
          </Select.Option>
          <Select.Option value="error">
            {i18n("AnalysisDetailsEmailPref.errorEmail")}
          </Select.Option>
          <Select.Option value="completion">
            {i18n("AnalysisDetailsEmailPref.completionEmail")}
          </Select.Option>
        </Select>
      </Form.Item>
    </section>
  );
}
