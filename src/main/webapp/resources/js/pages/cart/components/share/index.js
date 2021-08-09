import { Alert, Form, Skeleton, Space, Steps, Typography } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { useGetCartQuery } from "../../../../apis/cart/cart";
import { ShareProject } from "./ShareProject";

const { Step } = Steps;

/**
 * React component for Sharing samples with other projects.
 */
export function ShareLayout() {
  const { data: samples, status } = useGetCartQuery();
  const current = useSelector((state) => state.share.current);
  return (
    <Skeleton loading={status !== "fulfilled"}>
      {samples?.length ? (
        <Space direction="vertical" style={{ display: "block" }} size="large">
          <Typography.Title level={3}>
            {i18n("ShareLayout.title")}
          </Typography.Title>
          <Steps current={current}>
            <Step title={i18n("ShareLayout.step.projects")} />
            <Step title={i18n("ShareLayout.step.samples")} />
            <Step title={i18n("ShareLayout.step.restrictions")} />
          </Steps>
          <Form layout="vertical">
            <ShareProject />
          </Form>
        </Space>
      ) : (
        <Alert message={i18n("ShareLayout.empty")} type="info" showIcon />
      )}
    </Skeleton>
  );
}
