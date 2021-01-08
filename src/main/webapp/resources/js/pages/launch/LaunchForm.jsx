import React from "react";
import { LaunchDetails } from "./LaunchDetails";
import { SharePipelineResults } from "./SharePipelineResults";
import { ReferenceFiles } from "./references/ReferenceFiles";
import { Button, Form, Space } from "antd";
import { IconLaunchPipeline } from "../../components/icons/Icons";
import { useLaunch } from "./launch-context";
import { LaunchFiles } from "./LaunchFiles";
import { launchNewPipeline } from "./launch-dispatch";
import { LaunchParameters } from "./LaunchParameters";
import { SPACE_LG } from "../../styles/spacing";

/**
 * React component to handle all form components for launching a pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchForm() {
  const [state, launchDispatch] = useLaunch();
  const [form] = Form.useForm();

  /**
   * Triggered when submitting the launch.
   * This let's us perform any last minute validation and UI updates.
   */
  const onFinish = () => {
    // Add any required extra validation here.
    form
      .validateFields()
      .then((values) => launchNewPipeline(launchDispatch, values, state));
    // Add any required UI updates here.
  };

  return (
    <Form
      form={form}
      onFinish={onFinish}
      name="details"
      layout="vertical"
      initialValues={state.initialValues}
    >
      <Space direction="vertical" style={{ width: `100%` }}>
        <LaunchDetails />
        <LaunchParameters form={form} />
        <SharePipelineResults />
        <ReferenceFiles />
        <LaunchFiles />
        <Button
          type="primary"
          size="large"
          htmlType="submit"
          icon={<IconLaunchPipeline />}
          style={{ marginTop: SPACE_LG }}
        >
          {i18n("LaunchContent.submit")}
        </Button>
      </Space>
    </Form>
  );
}
