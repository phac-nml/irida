import {EditOutlined} from "@ant-design/icons";
import {Button, Cascader, Form, Popover} from "antd";
import React from "react";
import {Option} from "../../../types/ant-design";
import TextWithHelpPopover from "../TextWithHelpPopover";
import {TableWithOptionsHandles} from "./index";

interface Props {
  title: JSX.Element | string;
  options: Option[];
  onChange: (value: string, selectedOptions: Option[]) => void;
  helpText: string;
}

// TODO: WTF should I call this thing

/**
 *
 * @returns {JSX.Element}
 * @constructor
 */
export const TableHeaderWithCascaderOptions = React.forwardRef(
  function TableHeaderWithSelectOptions(
    { title, options = [], onChange, helpText }: Props,
    ref: React.ForwardedRef<TableWithOptionsHandles>
  ): JSX.Element {
    const [form] = Form.useForm();

    React.useImperativeHandle(ref, () => ({
      resetSelect() {
        form.resetFields();
      },
    }));

    return (
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <TextWithHelpPopover text={title} help={helpText} />
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
            </Form>k
          }
        >
          <Button
            size="small"
            type="text"
            icon={<EditOutlined style={{ color: `var(--grey-7)` }} />}
          />
        </Popover>
      </div>
    );
  }
);
