import React from "react";
import { List, Modal } from "antd";
import { VariableSizeList as VList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";

export default function RemoveModal({
  samples,
  visible,
  onComplete,
  onCancel,
}) {
  const removeListRef = React.useRef();

  const selected = Object.values(samples);

  const renderSample = ({ index, style, ...rest }, ...props) => {
    console.log({ props, rest });
    const sample = selected[index];
    return (
      <List.Item key={sample.key} {...style}>
        <List.Item.Meta title={sample.sampleName} />
      </List.Item>
    );
  };
  return (
    <Modal
      title={"REMOVE SAMPLES FROM PROJECT"}
      visible={visible}
      onCancel={onCancel}
      width={600}
    >
      <div style={{ height: 400, width: `100%` }}>
        <AutoSizer>
          {({ height = 400, width = 400 }) => (
            <List>
              <VList
                height={height}
                width={width}
                ref={removeListRef}
                itemKey={(index) => samples[index].key}
                itemCount={samples.length}
                itemSize={() => 47}
              >
                {renderSample}
              </VList>
            </List>
          )}
        </AutoSizer>
      </div>
    </Modal>
  );
}
