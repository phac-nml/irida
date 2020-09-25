import React, { useRef, useState } from "react";
import { Button, Form, Input, Popover } from "antd";
import { IconSave } from "../../components/icons/Icons";
import { useLaunchState } from "./launch-context";

export function ParameterSetSaveButton({ set }) {
  const { api } = useLaunchState();
  const [visible, setVisible] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();
  const inputRef = useRef();

  const onFinish = () => {
    setSaving(true);
    form
      .validateFields()
      .then((values) => {
        api.saveParameters({
          parameters: set.modified,
          name: values.name,
        });
        setVisible(false);
      })
      .catch(() => alert("WRONG"))
      .finally(() => setSaving(false));
  };

  return (
    <Popover
      visible={visible}
      trigger="click"
      onVisibleChange={setVisible}
      title={"Save as new parameter set."}
      content={
        <section onClick={(event) => event.stopPropagation()}>
          <Form
            style={{ width: 400 }}
            form={form}
            layout="vertical"
            initialValues={{ name: `${set.label} (copy)` }}
            onFinish={onFinish}
          >
            <Form.Item
              label={"Set Name"}
              name="name"
              rules={[
                {
                  required: true,
                  message: "A name for this set is required",
                },
                () => ({
                  validator(rule, value) {
                    if (value !== set.label) {
                      return Promise.resolve();
                    }
                    return Promise.reject(
                      "This name already exists for a template on this pipeline"
                    );
                  },
                }),
              ]}
            >
              <Input ref={inputRef} onFocus={() => inputRef.current.select()} />
            </Form.Item>
            <Form.Item>
              <Button loading={saving} htmlType="submit">
                Save
              </Button>
            </Form.Item>
          </Form>
        </section>
      }
    >
      <Button
        onClick={(e) => e.stopPropagation()}
        shape="circle"
        size="small"
        icon={<IconSave />}
      />
    </Popover>
  );
}
