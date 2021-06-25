import { Button, Checkbox, List, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";

import { FixedSizeList as VList } from "react-window";
import { SampleDetailViewer } from "../../../../components/samples/SampleDetailViewer";
import { ShareStatusAvatar } from "./ShareStatusAvatar";

export function ShareSamplesList({ samples }) {
  const dispatch = useDispatch();
  const { owner } = useSelector((stete) => state.reducer);

  const Row = ({ index, style }) => {
    const sample = samples[index];

    return (
      <List.Item style={style}>
        <List.Item.Meta
          avatar={<ShareStatusAvatar owner={sample.owner} />}
          title={
            <SampleDetailViewer sampleId={sample.id}>
              <Button>{sample.name}</Button>
            </SampleDetailViewer>
          }
        />
      </List.Item>
    );
  };

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Typography.Text>HELLO</Typography.Text>
      <List bordered>
        <VList
          height={600}
          itemCount={samples.length}
          itemSize={75}
          width={`100%`}
        >
          {Row}
        </VList>
      </List>
      <Checkbox checked={ownder}>
        Allow modification of samples in destination project
      </Checkbox>
    </Space>
  );
}
