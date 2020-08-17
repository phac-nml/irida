import React, { useEffect, useState } from "react";
import { Divider, List, Select, Typography } from "antd";
import { useLaunchState } from "./launch-context";

const { Option } = Select;
const { Paragraph } = Typography;

export function PipelineParameters() {
  const [selected, setSelected] = useState(0);
  const [currentParameters, setCurrentParameters] = useState();
  const { parameters } = useLaunchState();

  useEffect(() => {
    if (parameters.length) {
      setCurrentParameters(parameters[selected].parameters);
    }
  }, [parameters, selected]);

  const updateParameter = (name, value) => {
    const p = [...parameters[selected].parameters];
    const index = p.findIndex((i) => i.name === name);
    p[index] = { ...p[index], value: value };
    setCurrentParameters(p);
  };

  return (
    <>
      <Select
        style={{ flexGrow: 1 }}
        defaultValue={selected}
        onChange={setSelected}
      >
        {parameters.map((option) => (
          <Option key={option.id} value={option.id}>
            {option.label}
          </Option>
        ))}
      </Select>
      <Divider />
      <List
        itemLayout="horizontal"
        dataSource={currentParameters}
        renderItem={(parameter) => (
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
                            updateParameter(parameter.name, value),
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
