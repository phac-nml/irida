import React from "react";
import { List, Typography } from "antd";
import { useLaunchState } from "./launch-context";

const { Paragraph } = Typography;

export function PipelineParameterSet({ id, label, parameters, modified }) {
  const { modifyParameter } = useLaunchState();

  const updateParameter = (index, value) =>
    modifyParameter({ id, index, value });

  return (
    <List
      bordered={true}
      itemLayout="horizontal"
      dataSource={modified || parameters}
      renderItem={(parameter, index) => (
        <List.Item>
          <List.Item.Meta
            title={parameter.label}
            description={
              <Paragraph
                style={{ marginLeft: 15, marginBottom: 0 }}
                editable={{
                  onChange: (value) => updateParameter(index, value),
                }}
              >
                {parameter.value}
              </Paragraph>
            }
          />
        </List.Item>
      )}
    />
  );
}
