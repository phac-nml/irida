import React, { useState } from "react";
import { Button, Form, Input, Popover } from "antd";
import { SPACE_SM } from "../../styles/spacing";
import { useLaunchDispatch } from "./launch-context";
import { DISPATCH_PARAMETERS_DUPLICATE } from "./lauch-constants";

export function DuplicateParametersButton() {
  const [visible, setVisible] = useState(false);
  const dispatch = useLaunchDispatch();
  const [form] = Form.useForm();

  const saveParameters = () =>
    form.validateFields().then((values) => {
      dispatch({
        type: DISPATCH_PARAMETERS_DUPLICATE,
        payload: {
          name: values.name,
        },
      });
    });

  const onCancel = () => {
    form.resetFields();
    setVisible(false);
  };

  const content = (
    <section>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <Form form={form} layout={"vertical"} onFinish={saveParameters}>
          <Form.Item
            label={"Unique name for this set of parameters"}
            rules={[
              { required: true, message: "Give it a name already" },
              { min: 5, message: "must be at least 5 letters" },
            ]}
            name="name"
          >
            <Input />
          </Form.Item>
          <Form.Item>
            <Button onClick={onCancel}>Cancel</Button>
            <Button
              style={{ marginLeft: SPACE_SM }}
              type="primary"
              htmlType="submit"
            >
              Duplicate
            </Button>
          </Form.Item>
        </Form>
      </div>
    </section>
  );

  return (
    <Popover
      title={"DUPLICATE DEFAULT PARAMETERS"}
      visible={visible}
      content={content}
      placement="bottom"
    >
      <Button onClick={() => setVisible(true)}>Duplicate Parameters</Button>
    </Popover>
  );
}
