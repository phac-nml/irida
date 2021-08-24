import { Button, Col, List } from "antd";
import React from "react";
import { FixedSizeList as VList } from "react-window";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";

export function SharedSamplesList({ list = [] }) {
  const Row = ({ index, style }) => {
    const sample = list[index];

    return (
      <List.Item style={style}>
        <List.Item.Meta
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
    <Col md={12} xs={24}>
      <List bordered rowKey={(sample) => sample.name}>
        <VList height={600} itemCount={list.length} itemSize={55} width="100%">
          {Row}
        </VList>
      </List>
    </Col>
  );
}
