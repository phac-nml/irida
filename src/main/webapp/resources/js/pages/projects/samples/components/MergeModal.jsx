import React from "react";
import { Modal, Radio, Space } from "antd";

export default function MergeModal({ samples, visible, onOk }) {
  const copy = Object.entries(samples).map(([, sample]) => sample);
  const [value, setValue] = React.useState(copy[0].id);
  return (
    <Modal title="Merge Samples" visible={visible} onOk={onOk} onCancel={onOk}>
      <p>The following 2 samples will be merged into the selected sample.</p>

      <Radio.Group value={value}>
        <Space
          direction="vertical"
          onChange={e => setValue(e.target.value)}
          value={value}
        >
          {copy.map(sample => {
            return (
              <Radio value={sample.id} key={`sample-${sample.id}`}>
                {sample.sampleName}
              </Radio>
            );
          })}
        </Space>
      </Radio.Group>
    </Modal>
  );
}
