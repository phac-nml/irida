import React from "react";
import { LaunchDetails } from "./LaunchDetails";
import { SharePipelineResults } from "./SharePipelineResults";
import { ReferenceFiles } from "./references/ReferenceFiles";
import { Button, Form, notification, Space } from "antd";
import { IconLaunchPipeline } from "../../components/icons/Icons";
import { useLaunch } from "./launch-context";
import { LaunchFiles } from "./LaunchFiles";
import { launchNewPipeline } from "./launch-dispatch";
import { LaunchParameters } from "./LaunchParameters";
import { SPACE_LG } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * React component to handle all form components for launching a pipeline.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchForm() {
  const LAUNCH_STATES = {
    WAITING: "WAITING",
    LOADING: "LOADING",
    SUCCESS: "SUCCESS",
  };

  const [state, launchDispatch] = useLaunch();
  const [launchState, setLaunchState] = React.useState(LAUNCH_STATES.WAITING);
  const [form] = Form.useForm();

  /**
   * Triggered when submitting the launch.
   * This let's us perform any last minute validation and UI updates.
   */
  const onFinish = () => {
    // Add any required extra validation here.
    form.validateFields().then((values) => {
      setLaunchState(LAUNCH_STATES.LOADING);
      launchNewPipeline(launchDispatch, values, state)
        .then(({ id }) => {
          // Redirect to analysis page or project settings processing page for automated pipelines
          setLaunchState(LAUNCH_STATES.SUCCESS);
          const url = state.automatedId
            ? `project/${state.automatedId}/settings/processing`
            : `analysis/${id}`;
          window.setTimeout(() => {
            window.location.href = setBaseUrl(url);
          }, 350);
        })
        .catch(({ error }) => {
          setLaunchState(LAUNCH_STATES.WAITING);
          notification.error({
            message: error,
          });
        });
    });
  };

  return (
    <Form
      form={form}
      onFinish={onFinish}
      name="details"
      layout="vertical"
      initialValues={state.initialValues}
      className="t-launch-form"
    >
      <Space direction="vertical" style={{ width: `100%` }}>
        <LaunchDetails />
        <LaunchParameters form={form} />
        <SharePipelineResults />
        <ReferenceFiles form={form} />
        {state.automatedId ? null : <LaunchFiles />}
        <Button
          type="primary"
          className="t-submit-btn"
          size="large"
          htmlType="submit"
          icon={<IconLaunchPipeline />}
          loading={launchState === LAUNCH_STATES.LOADING}
          style={{ marginTop: SPACE_LG }}
          disabled={
            launchState === LAUNCH_STATES.LOADING ||
            launchState === LAUNCH_STATES.SUCCESS
          }
        >
          {i18n("LaunchContent.submit")}
        </Button>
      </Space>
    </Form>
  );
}
