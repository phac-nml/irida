import React from "react";
import { LaunchDetails } from "./LaunchDetails";
import { SharePipelineResults } from "./SharePipelineResults";
import { ReferenceFiles } from "./references/ReferenceFiles";
import { SavedParameters } from "./parameters/SavedParameters";
import { ParameterWithOptions } from "./ParameterWithOptions";
import { Button, Form } from "antd";
import { IconLaunchPipeline } from "../../components/icons/Icons";
import { launchNewPipeline, useLaunch } from "./launch-context";

export function LaunchForm() {
  const [{ initialValues, parameterWithOptions }, launchDispatch] = useLaunch();
  const [form] = Form.useForm();

  /**
   * Triggered when submitting the launch.
   * This let's us perform any last minute validation and UI updates.
   */
  const onFinish = () => {
    // Add any required extra validation here.
    form
      .validateFields()
      .then((values) => launchNewPipeline(launchDispatch, values));
    // Add any required UI updates here.
  };

  return (
    <Form
      form={form}
      onFinish={onFinish}
      name="details"
      layout="vertical"
      initialValues={initialValues}
    >
      <LaunchDetails />
      <SharePipelineResults />
      <ReferenceFiles />
      <SavedParameters form={form} />
      <ParameterWithOptions parameters={parameterWithOptions} />
      <Button type="primary" htmlType="submit" icon={<IconLaunchPipeline />}>
        {i18n("LaunchContent.submit")}
      </Button>
    </Form>
  );
}
