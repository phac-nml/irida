import React from "react";
import { Collapse, Divider, Form, List, Radio, Select, Typography } from "antd";
import { useLaunchDispatch, useLaunchState } from "./launch-context";
import { DISPATCH_PARAMETERS_MODIFIED } from "../pipeline/lauch-constants";
import styled from "styled-components";

const { Panel } = Collapse;
const { Paragraph } = Typography;

const CollapseRadioGroup = styled(Radio.Group)`
  label.ant-radio-wrapper {
    display: inline-block !important;
  }
  width: 100%;
`;

export function PipelineParameters() {
  const { parameters, parametersWithOptions } = useLaunchState();
  const dispatch = useLaunchDispatch();

  const updateParameter = (value, index) =>
    dispatch({
      type: DISPATCH_PARAMETERS_MODIFIED,
      value,
      index,
    });

  return (
    <Form.Item name="parameters">
      <CollapseRadioGroup>
        <Collapse>
          {parameters.map((p) => (
            <Panel
              key={`parameter-${p.id}`}
              header={
                <span role="button" onClick={(e) => e.stopPropagation()}>
                  <Radio type="radio" name="parameters" value={p.id}>
                    {p.label}
                  </Radio>
                </span>
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
                          editable={{
                            onChange: (value) => updateParameter(value, index),
                          }}
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
      </CollapseRadioGroup>
      {parametersWithOptions ? (
        <section>
          <Divider />
          {parametersWithOptions.map((p) => (
            <Form.Item key={p.name} label={p.name}>
              {p.options.length < 5 ? (
                <Radio.Group defaultValue={p.value || p.options[0].value}>
                  {p.options.map((option) => (
                    <Radio key={option.value} value={option.value}>
                      {option.label}
                    </Radio>
                  ))}
                </Radio.Group>
              ) : (
                <Select>
                  {p.options.map((option) => (
                    <Select.Option value={options.value}>
                      {option.text}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          ))}
        </section>
      ) : null}
    </Form.Item>
  );
}
