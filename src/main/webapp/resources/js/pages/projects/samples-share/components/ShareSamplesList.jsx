import { Button, List } from "antd";
import React from "react";
import { SampleDetailViewer } from "../../../../components/samples/SampleDetailViewer";
import { ShareStatusAvatar } from "./ShareStatusAvatar";

export function ShareSamplesList({ samples }) {
  return (
    <List
      dataSource={samples}
      renderItem={(sample) => (
        <List.Item>
          <List.Item.Meta
            avatar={<ShareStatusAvatar owner={sample.owner} />}
            title={
              <SampleDetailViewer sampleId={sample.id}>
                <Button>{sample.name}</Button>
              </SampleDetailViewer>
            }
          />
        </List.Item>
      )}
    />
  );
}
