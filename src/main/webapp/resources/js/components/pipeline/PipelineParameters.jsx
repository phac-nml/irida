import React, { useEffect, useState } from "react";
import { Divider, Form, List, Select, Typography } from "antd";
import { SPACE_SM } from "../../styles/spacing";

const { Option } = Select;
const { Paragraph } = Typography;

export function PipelineParameters({ parameters }) {
  const [selected, setSelected] = useState(0);
  const [currentParameters, setCurrentParameters] = useState();

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
      <Form layout={"inline"} style={{ display: "flex" }}>
        <Select
          style={{ flexGrow: 1, marginRight: SPACE_SM }}
          defaultValue={selected}
          onChange={setSelected}
        >
          {parameters.map((option) => (
            <Option key={option.id} value={option.id}>
              {option.label}
            </Option>
          ))}
        </Select>
      </Form>
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
