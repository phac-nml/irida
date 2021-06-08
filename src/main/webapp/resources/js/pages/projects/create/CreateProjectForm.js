import React from "react";
import { Form } from "antd";

export function CreateProjectFrom({}) {
  return (
    <Form
      form={form}
      layout="vertical"
      validateMessages={validateMessages}
      onFinish={submit}
      initialValues={{
        name: "",
        description: "",
        organism: "",
        remoteURL: "",
        lock: false,
        samples: [],
      }}
    >
      {steps.map((step, index) => (
        <div
          key={`step-${index}`}
          style={{
            display: current === index ? "block" : "none",
          }}
        >
          {step.content}
        </div>
      ))}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          flexDirection: "row-reverse",
        }}
      >
        {current !== steps.length - 1 && (
          <Button
            onClick={() => {
              form.validateFields().then(() => {
                setCurrent(current + 1);
              });
            }}
          >
            NEXT
          </Button>
        )}
        {current === steps.length - 1 && (
          <Button htmlType="submit" loading={loading} type="primary">
            CREATE
          </Button>
        )}
        {current !== 0 && (
          <Button
            onClick={() => {
              form.validateFields().then(() => setCurrent(current - 1));
            }}
          >
            PREVIOUS
          </Button>
        )}
      </div>
    </Form>
  );
}
