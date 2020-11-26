import React from "react";
import { LaunchPageHeader } from "./LaunchPageHeader";
import { LaunchDetails } from "./LaunchDetails";
import { Button, Col, Form, Row } from "antd";
import { IconLaunchPipeline } from "../../components/icons/Icons";
import { ParameterWithOptions } from "./ParameterWithOptions";
import { SavedParameters } from "./parameters/SavedParameters";
import { ReferenceFiles } from "./references/ReferenceFiles";
import { launchNewPipeline, useLaunch } from "./launch-context";
import { SharePipelineResults } from "./SharePipelineResults";

/**
 * React component to layout the content of the pipeline launch.
 * It will act as the top level logic controller.
 */
export function LaunchContent() {
  const [
    { initialValues, pipeline, parameterWithOptions },
    launchDispatch,
  ] = useLaunch();
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
    <>
      <LaunchPageHeader pipeline={pipeline} />
      <Row gutter={[16, 16]}>
        <Col sm={24} md={12} xl={14}>
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
            <Button
              type="primary"
              htmlType="submit"
              icon={<IconLaunchPipeline />}
            >
              {i18n("LaunchContent.submit")}
            </Button>
          </Form>
        </Col>
        <Col sm={24} md={12} xl={10}>
          SAMPLES
        </Col>
      </Row>
    </>
  );
}
