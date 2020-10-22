import React from "react";
import { LaunchPageHeader } from "./LaunchPageHeader";
import { LaunchDetails } from "./LaunchDetails";
import { Button, Divider, Form } from "antd";
import { IconLaunchPipeline } from "../../components/icons/Icons";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import { ParameterWithOptions } from "./ParameterWithOptions";
import { SavedParameters } from "./SavedParameters";

/**
 * React component to layout the content of the pipeline launch.
 * It will act as the top level logic controller.
 */
export function LaunchContent() {
  const {
    initialValues,
    parameterWithOptions,
    savedPipelineParameters,
  } = useLaunchState();
  const { dispatchLaunch } = useLaunchDispatch();
  const [form] = Form.useForm();

  /**
   * Triggered when submitting the launch.
   * This let's us perform any last minute validation and UI updates.
   */
  const onFinish = () => {
    // Add any required extra validation here.
    form.validateFields().then((values) => dispatchLaunch(values));
    // Add any required UI updates here.
  };

  return (
    <>
      <LaunchPageHeader />
      <Form
        form={form}
        onFinish={onFinish}
        name="details"
        layout="vertical"
        initialValues={initialValues}
      >
        <LaunchDetails />
        <Divider />
        <SavedParameters sets={savedPipelineParameters} />
        <Divider />
        <ParameterWithOptions parameters={parameterWithOptions} />
        <Button type="primary" htmlType="submit" icon={<IconLaunchPipeline />}>
          {i18n("LaunchContent.submit")}
        </Button>
      </Form>
    </>
  );
}
