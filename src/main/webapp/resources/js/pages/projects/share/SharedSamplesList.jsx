import { Avatar, Button, List, Space, Typography } from "antd";
import React from "react";
import { FixedSizeList as VList } from "react-window";
import { IconLocked, IconUnlocked } from "../../../components/icons/Icons";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { green6, grey1, grey2, yellow6 } from "../../../styles/colors";

export function SharedSamplesList({ list = [], title = "" }) {
  const Row = ({ index, style }) => {
    const sample = list[index];

    return (
      <List.Item style={{ ...style, backgroundColor: grey1 }}>
        <List.Item.Meta
          avatar={
            sample.owner ? (
              <Avatar
                style={{ backgroundColor: green6 }}
                size="small"
                icon={<IconUnlocked />}
              />
            ) : (
              <Avatar
                style={{ backgroundColor: yellow6 }}
                size="small"
                icon={<IconLocked />}
              />
            )
          }
          title={
            <SampleDetailViewer sampleId={sample.id}>
              <Button size="small">{sample.name}</Button>
            </SampleDetailViewer>
          }
        />
      </List.Item>
    );
  };
  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Text>{title}</Typography.Text>
      <List
        title={title}
        bordered
        rowKey={(sample) => sample.name}
        style={{ backgroundColor: grey2 }}
      >
        <VList height={600} itemCount={list.length} itemSize={55} width="100%">
          {Row}
        </VList>
      </List>
    </Space>
  );
}
