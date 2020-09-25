import React from "react";
import { List, Typography } from "antd";
import { useLaunchState } from "./launch-context";

const { Paragraph } = Typography;

export function PipelineParameterSet({ id, label, parameters, modified }) {
  const { api } = useLaunchState();

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
                  onChange: (value) =>
                    api.modifyParameter({ id, index, value }),
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
