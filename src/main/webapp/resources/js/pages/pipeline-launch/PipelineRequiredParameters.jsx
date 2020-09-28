import React from "react";
import { Button, Collapse, Form, Radio, Typography } from "antd";
import { PipelineParameterSet } from "./PipelineParameterSet";
import { useLaunchState } from "./launch-context";
import { ParameterSetActionButtons } from "./ParameterSetActionButtons";
import styled from "styled-components";

const { Panel } = Collapse;
const { Text } = Typography;

const CollapseRadioGroup = styled(Radio.Group)`
  label.ant-radio-wrapper {
    display: inline-block !important;
  }
  width: 100%;
`;

export function PipelineRequiredParameters() {
  const { parameters, selectedPipeline, api } = useLaunchState();

  const generateSave = (set) => {
    if (set.modified) {
      return <ParameterSetActionButtons set={set} />;
    }
    return null;
  };

  return (
    <Form.Item label={i18n("PipelineParameters.requiredParameters")}>
      <CollapseRadioGroup
        onChange={(e) =>
          api.updateDetailsField({
            field: "selectedPipeline",
            value: e.target.value,
          })
        }
        value={selectedPipeline}
      >
        <Collapse expandIconPosition="right">
          {parameters.map((set) => (
            <Panel
              key={`parameter-${set.id}`}
              extra={generateSave(set)}
              header={
                <Button ghost onClick={(e) => e.stopPropagation()}>
                  <Radio type="radio" name="parameters" value={set.id}>
                    {set.label}
                  </Radio>
                  {set.modified ? (
                    <Text type="warning">{"* Modified"}</Text>
                  ) : null}
                </Button>
              }
            >
              <PipelineParameterSet {...set} />
            </Panel>
          ))}
        </Collapse>
      </CollapseRadioGroup>
    </Form.Item>
  );
}
