import { List } from "antd";
import React from "react";
import { useSelector } from "react-redux";

export function ShareSummary() {
  const { target, fields, samples, locked } = useSelector(
    (state) => state.reducer
  );
  return (
    <List>
      <List.Item>
        <List.Item.Meta
          title={"Destination Project"}
          description={target.label}
        />
      </List.Item>
      <List.Item>
        <List.Item.Meta
          title={"Samples"}
          description={`${samples.length} samples will be copied`}
        />
      </List.Item>
    </List>
  );
}
