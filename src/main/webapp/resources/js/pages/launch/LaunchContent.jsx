import React from "react";
import { LaunchPageHeader } from "./LaunchPageHeader";
import { LaunchDetails } from "./LaunchDetails";
import { Button, Form } from "antd";
import { IconLaunchPipeline } from "../../components/icons/Icons";
import { useLaunchDispatch, useLaunchState } from "./launch-context";

export function LaunchContent() {
  const { type } = useLaunchState();
  const { dispatchLaunch } = useLaunchDispatch();

  const [form] = Form.useForm();

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
        initialValues={{
          name: `${type.replace(" ", "_")}_${Date.now()}`,
        }}
      >
        <LaunchDetails />
        <Button type="primary" htmlType="submit" icon={<IconLaunchPipeline />}>
          {i18n("LaunchContent.submit")}
        </Button>
      </Form>
    </>
  );
}
