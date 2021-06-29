import { navigate } from "@reach/router";
import { Button, Checkbox, List, Space } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";

import { FixedSizeList as VList } from "react-window";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";
import { SampleDetailViewer } from "../../../../components/samples/SampleDetailViewer";
import { updatedSamplesOwnerStatus } from "../services/rootReducer";
import { ShareStatusAvatar } from "./ShareStatusAvatar";

export function ShareSamplesList({ samples }) {
  const dispatch = useDispatch();
  const { owner } = useSelector((state) => state.reducer);

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

  const updateOwnerShip = (e) =>
    dispatch(updatedSamplesOwnerStatus(e.target.checked));

  return (
    <Space direction="vertical" style={{ display: "block" }}>
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
      <Checkbox checked={owner} onChange={updateOwnerShip}>
        Allow modification of samples in destination project
      </Checkbox>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <Button onClick={() => navigate("projects")}>
          <Space>
            <IconArrowLeft />
            <span>Select a Project</span>
            <IconArrowRight />
          </Space>
        </Button>
        <Button onClick={() => navigate("fields")}>
          <Space>
            <span>Check Metadata Field Permissions</span>
            <IconArrowRight />
          </Space>
        </Button>
      </div>
    </Space>
  );
}
