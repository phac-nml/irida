import { List } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { grey3 } from "../../../styles/colors";

export function ShareSamples({ sampleIds = [] }) {
  const { originalSamples } = useSelector((state) => state.shareReducer);
  const [existing, setExisting] = React.useState(0);
  const [samples, setSamples] = React.useState();

  React.useEffect(() => {
    if (sampleIds) {
      const updated = [];
      let count = 0;
      originalSamples.forEach((sample) => {
        const exists = sampleIds.includes(sample.id);
        if (exists) count++;
        updated.push({
          ...sample,
          exists,
        });
      });
      setSamples(updated);
      setExisting(count);
    }
  }, [originalSamples, sampleIds]);

  return (
    <List
      header={
        <div>
          <h3>Samples To Copy</h3>
          {existing} Exist in the target project and will not be recopied
        </div>
      }
      bordered
      dataSource={samples}
      renderItem={(sample) => (
        <List.Item
          style={{ backgroundColor: sample.exists ? grey3 : "transparent" }}
        >
          <List.Item.Meta title={sample.name} />
        </List.Item>
      )}
    />
  );
}
