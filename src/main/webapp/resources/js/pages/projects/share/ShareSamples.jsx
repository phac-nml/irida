import { List } from "antd";
import React from "react";
import { useSelector } from "react-redux";

export function ShareSamples({ sampleIds = [] }) {
  const { originalSamples } = useSelector((state) => state.shareReducer);
  const [samples, setSamples] = React.useState();

  React.useEffect(() => {
    if (sampleIds) {
      setSamples(
        originalSamples.map((sample) => ({
          ...sample,
          exists: sampleIds.includes(sample.id),
        }))
      );
    }
  }, [originalSamples, sampleIds]);

  return (
    <List
      header={
        <div>
          <h3>Samples To Copy</h3>
          {originalSamples?.length - samples?.length} Exist in the target
          project
        </div>
      }
      bordered
      dataSource={samples}
      renderItem={(sample) => (
        <List.Item>
          <List.Item.Meta title={sample.name} />
        </List.Item>
      )}
    />
  );
}
