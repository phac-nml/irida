import React from "react";
import { Button, Cascader, Form, Popover, Space, Typography } from "antd";
import { EditOutlined, QuestionCircleOutlined } from "@ant-design/icons";
import { grey } from "@ant-design/colors";

// TODO: WTF should I call this thing

/**
 *
 * @returns {JSX.Element}
 * @constructor
 */
export const TableHeaderWithCascaderOptions = React.forwardRef(
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
              <QuestionCircleOutlined style={{ color: grey[2] }} />
            </Popover>
          )}
        </Space>
        <Popover
          title={"Select value for all samples"}
          content={
            <Form form={form}>
              <Form.Item style={{ margin: 0 }} name="select">
                <Cascader
                  options={options}
                  style={{ display: "block" }}
                  onChange={onChange}
                />
              </Form.Item>
            </Form>
          }
        >
          <Button
            size="small"
            type="text"
            icon={<EditOutlined style={{ color: grey[2] }} />}
          />
        </Popover>
      </div>
    );
  }
);
