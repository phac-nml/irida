import { List, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";

export function Legend() {
  const { terms, treeProps } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  return (
    <List
      header={<Typography.Text>Legend</Typography.Text>}
      dataSource={terms}
      renderItem={(item) => (
        <List.Item>
          <List.Item.Meta title={item} />
        </List.Item>
      )}
      style={{
        paddingLeft: "14px",
        paddingRight: "14px"
      }}
    />
  );
}