import React from "react";
import { Collapse, Form, Radio, Typography } from "antd";
import { useLaunchState } from "./launch-context";
import styled from "styled-components";
import { PipelineParameterSet } from "./PipelineParameterSet";
import { ParameterSaveButton } from "./ParameterSaveButton";

const { Panel } = Collapse;
const { Paragraph } = Typography;

const CollapseRadioGroup = styled(Radio.Group)`
  label.ant-radio-wrapper {
    display: inline-block !important;
  }
  width: 100%;
`;

export function PipelineParameters() {
  const {
    parameters,
    parametersWithOptions,
    selectedPipeline,
    updateDetailsField,
  } = useLaunchState();

  const generateSave = (index) => {
    const set = parameters[index];
    if (set.modified) {
      return <ParameterSaveButton parameter={set} />;
    }
    return null;
  };

  return (
    <Form.Item>
      <CollapseRadioGroup
        onChange={(e) =>
          updateDetailsField({
            field: "selectedPipeline",
            value: e.target.value,
          })
        }
        value={selectedPipeline}
      >
        <Collapse expandIconPosition="right">
          {parameters.map((p, index) => (
            <Panel
              key={`parameter-${p.id}`}
              extra={generateSave(index)}
              header={
                <span role="button" onClick={(e) => e.stopPropagation()}>
                  <Radio type="radio" name="parameters" value={p.id}>
                    {p.label}
                  </Radio>
                </span>
              }
            >
              <PipelineParameterSet {...p} />
            </Panel>
          ))}
        </Collapse>
      </CollapseRadioGroup>
      {/*{parametersWithOptions ? (*/}
      {/*  <section>*/}
      {/*    <Divider />*/}
      {/*    {parametersWithOptions.map((p) => (*/}
      {/*      <Form.Item key={p.name} label={p.name}>*/}
      {/*        {p.options.length < 5 ? (*/}
      {/*          <Radio.Group defaultValue={p.value || p.options[0].value}>*/}
      {/*            {p.options.map((option) => (*/}
      {/*              <Radio key={option.value} value={option.value}>*/}
      {/*                {option.label}*/}
      {/*              </Radio>*/}
      {/*            ))}*/}
      {/*          </Radio.Group>*/}
      {/*        ) : (*/}
      {/*          <Select>*/}
      {/*            {p.options.map((option) => (*/}
      {/*              <Select.Option value={options.value}>*/}
      {/*                {option.text}*/}
      {/*              </Select.Option>*/}
      {/*            ))}*/}
      {/*          </Select>*/}
      {/*        )}*/}
      {/*      </Form.Item>*/}
      {/*    ))}*/}
      {/*  </section>*/}
      {/*) : null}*/}
    </Form.Item>
  );
}
