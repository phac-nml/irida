import React, { useState } from "react";
import { Button, Divider, Form, List, Select } from "antd";
import { SPACE_MD } from "../../styles/spacing";

const { Option } = Select;

export function PipelineParameters({ parameters }) {
  const [selected, setSelected] = useState(0);
  return (
    <>
      <Form layout={"inline"} style={{ display: "flex" }}>
        <Select
          style={{ flexGrow: 1, marginRight: SPACE_MD }}
          defaultValue={selected}
          onChange={setSelected}
        >
          {parameters.map((option) => (
            <Option key={option.id} value={option.id}>
              {option.label}
            </Option>
          ))}
        </Select>
        <Button>Modify</Button>
      </Form>
      <Divider />
      <List
        itemLayout="horizontal"
        dataSource={parameters[selected].parameters}
        renderItem={(parameter) => (
          <List.Item>
            <List.Item.Meta
              title={parameter.label}
              description={parameter.value}
            />
          </List.Item>
        )}
      />
    </>
  );
}
