import React from "react";
import { fetchPipelineSamples } from "../../apis/pipelines/pipelines";
import { List } from "antd";
import { SampleDetailSidebar } from "../../components/samples/SampleDetailSidebar";

export function LaunchFiles() {
  const [samples, setSamples] = React.useState();

  React.useEffect(() => {
    fetchPipelineSamples().then(setSamples);
  }, []);

  return (
    <List
      bordered
      itemLayout="vertical"
      dataSource={samples}
      renderItem={(sample) => (
        <List.Item>
          <SampleDetailSidebar sampleId={sample.id}>
            {sample.label}
          </SampleDetailSidebar>
        </List.Item>
      )}
    />
  );
}
