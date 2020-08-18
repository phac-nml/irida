import React, { useState } from "react";
import {
  Button,
  Divider,
  Dropdown,
  List,
  Menu,
  Select,
  Typography,
} from "antd";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import {
  DISPATCH_PARAMETER_CHANGE,
  DISPATCH_PARAMETERS_MODIFIED,
} from "./lauch-constants";
import { SPACE_SM } from "../../styles/spacing";
import { IconDropDown } from "../icons/Icons";

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
        {selected === 0 ? (
          <Button style={{ marginLeft: SPACE_SM }}>Duplicate</Button>
        ) : modified ? (
          <Dropdown.Button
            overlay={
              <Menu>
                <Menu.Item key="save-as">SAVE AS....</Menu.Item>
                <Menu.Item key="revert">REVERT</Menu.Item>
              </Menu>
            }
            icon={<IconDropDown />}
            style={{ marginLeft: SPACE_SM }}
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
