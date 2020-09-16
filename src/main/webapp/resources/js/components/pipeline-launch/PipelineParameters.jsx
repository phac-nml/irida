import React, { useState } from "react";
import { Divider, Dropdown, Form, List, Menu, Select, Typography } from "antd";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import {
  DISPATCH_PARAMETER_CHANGE,
  DISPATCH_PARAMETERS_MODIFIED,
} from "../pipeline/lauch-constants";
import { SPACE_XS } from "../../styles/spacing";
import { IconDropDown } from "../icons/Icons";
import { DuplicateParametersButton } from "./DuplicateParametersButton";

const { Option } = Select;
const { Paragraph } = Typography;

export function PipelineParameters() {
  const { parameters, original, modified } = useLaunchState();
  const dispatch = useLaunchDispatch();
  const [selected, setSelected] = useState(0);

  const getParameters = (index) => {
    dispatch({ type: DISPATCH_PARAMETER_CHANGE, index });
    setSelected(index);
  };

  const updateParameter = (value, index) =>
    dispatch({
      type: DISPATCH_PARAMETERS_MODIFIED,
      value,
      index,
    });

  return (
    <>
      <div style={{ display: "flex" }}>
        <Form.Item
          style={{ width: 400, marginRight: SPACE_XS }}
          help={
            selected === 0
              ? "Default parameters cannot be modified, make a duplicate copy and modify them"
              : null
          }
        >
          <Select
            style={{ flexGrow: 1 }}
            defaultValue={selected}
            onChange={getParameters}
          >
            {original.parameters.map((option) => (
              <Option key={option.id} value={option.id}>
                {option.label}
              </Option>
            ))}
          </Select>
        </Form.Item>
        {selected === 0 ? (
          <DuplicateParametersButton />
        ) : modified ? (
          <Dropdown.Button
            overlay={
              <Menu>
                <Menu.Item key="save-as">SAVE AS....</Menu.Item>
                <Menu.Item key="revert">REVERT</Menu.Item>
              </Menu>
            }
            icon={<IconDropDown />}
            style={{ marginLeft: SPACE_XS }}
          >
            Save
          </Dropdown.Button>
        ) : null}
      </div>
      <Divider />
      <List
        itemLayout="horizontal"
        dataSource={parameters}
        renderItem={(parameter, index) => (
          <List.Item>
            <List.Item.Meta
              title={parameter.label}
              description={
                <Paragraph
                  style={{ marginLeft: 15, marginBottom: 0 }}
                  editable={
                    selected === 0
                      ? null
                      : {
                          onChange: (value) => updateParameter(value, index),
                        }
                  }
                >
                  {parameter.value}
                </Paragraph>
              }
            />
          </List.Item>
        )}
      />
    </>
  );
}
