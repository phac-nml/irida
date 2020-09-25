import React, { useRef, useState } from "react";
import { Button, Form, Input, Popover, Tooltip } from "antd";
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
      placement="bottom"
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
            onFinish={onFinish}
          >
            <Form.Item
              label={"Set Name"}
              name="name"
              rules={[
                {
                  required: true,
                  message: i18n("ParameterSetSaveButton.name.required"),
                },
                () => ({
                  validator(rule, value) {
                    if (api.validateSetName(value)) {
                      return Promise.resolve();
                    }
                    return Promise.reject(
                      i18n("ParameterSetSaveButton.name.error")
                    );
                  },
                }),
              ]}
            >
              <Input ref={inputRef} />
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
      <Tooltip title={i18n("ParameterSetSaveButton.tooltip")}>
        <Button
          onClick={(e) => e.stopPropagation()}
          shape="circle"
          size="small"
          icon={<IconSave />}
        />
      </Tooltip>
    </Popover>
  );
}
