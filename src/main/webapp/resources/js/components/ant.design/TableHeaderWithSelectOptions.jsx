import React from "react";
import { Button, Form, Popover, Select, Space, Typography } from "antd";
import { EditOutlined, QuestionCircleOutlined } from "@ant-design/icons";

// TODO: WTF should I call this thing

/**
 *
 * @returns {JSX.Element}
 * @constructor
 */
export const TableHeaderWithSelectOptions = React.forwardRef(
  function TableHeaderWithSelectOptions(
    { title, options = [], onChange, helpText },
    ref
  ) {
    const [form] = Form.useForm();

    React.useImperativeHandle(ref, () => ({
      resetSelect() {
        form.resetFields();
      },
    }));

    return (
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <Space size="small">
          <Typography.Text>{title}</Typography.Text>
          {helpText && (
            <Popover
              content={<div dangerouslySetInnerHTML={{ __html: helpText }} />}
            >
              <QuestionCircleOutlined />
            </Popover>
          )}
        </Space>
        <Popover
          title={"Select value for all samples"}
          content={
            <Form form={form}>
              <Form.Item style={{ margin: 0 }} name="select">
                <Select style={{ display: "block" }} onChange={onChange}>
                  {options.map((option) => (
                    <Select.Option key={option}>{option}</Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Form>
          }
        >
          <Button size="small" type="text" icon={<EditOutlined />} />
        </Popover>
      </div>
    );
  }
);
