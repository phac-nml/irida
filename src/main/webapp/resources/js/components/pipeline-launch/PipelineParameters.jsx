import React, { useState } from "react";
import { Collapse, List, Space, Typography } from "antd";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import {
  DISPATCH_PARAMETER_CHANGE,
  DISPATCH_PARAMETERS_MODIFIED,
} from "../pipeline/lauch-constants";
import { IconCopy } from "../icons/Icons";

const { Panel } = Collapse;
const { Paragraph } = Typography;

export function PipelineParameters() {
  const { parameters, original, modified } = useLaunchState();
  const dispatch = useLaunchDispatch();
  const [selected, setSelected] = useState(parameters[0].id);

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

  const updateSelected = (e) => {
    setSelected(Number(e.target.value));
  };

  return (
    <>
      <Collapse expandIconPosition="right">
        {parameters.map((p) => (
          <Panel
            key={`parameter-${p.id}`}
            extra={<IconCopy />}
            header={
              <Space>
                <input
                  type="radio"
                  name="para"
                  value={p.id}
                  onChange={updateSelected}
                  onClick={(e) => e.stopPropagation()}
                  checked={selected === p.id}
                />
                {p.label}
              </Space>
            }
          >
            <List
              bordered={true}
              itemLayout="horizontal"
              dataSource={p.parameters}
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
                                onChange: (value) =>
                                  updateParameter(value, index),
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
          </Panel>
        ))}
      </Collapse>
    </>
  );
}
