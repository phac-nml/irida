import React from "react";
import { Button, Form, Popover, Select, Typography } from "antd";
import { EditOutlined } from "@ant-design/icons";

// TODO: WTF should I call this thing

/**
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function TableHeaderWithSelectOptions({
  title,
  popoverText,
  options = [],
  onChange,
  formRef,
}) {
  return (
    <div style={{ display: "flex", justifyContent: "space-between" }}>
      <Typography.Text>{title}</Typography.Text>
      <Popover
        title={popoverText}
        content={
          <Form form={formRef}>
            <Form.Item style={{ margin: 0 }} name="select">
              <Select style={{ width: 200 }} onChange={onChange}>
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
