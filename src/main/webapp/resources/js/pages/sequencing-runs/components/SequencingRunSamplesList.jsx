import { Empty, List } from "antd";
import React from "react";
import { SequencingRunSample } from "./SequencingRunSample";

/**
 * React component to render the samples list.
 * @param {array} samples - list of samples
 * @returns {JSX.Element} - Returns a sample list component
 */
export function SequencingRunSamplesList({ samples }) {
  return samples.length === 0 ? (
    <Empty
      description={i18n("SequencingRunSamplesList.empty")}
      image={Empty.PRESENTED_IMAGE_SIMPLE}
    />
  ) : (
    <List
      grid={{ column: 1 }}
      dataSource={samples}
      renderItem={(sample, index) => {
        return (
          <List.Item>
            <SequencingRunSample
              samples={samples}
              sample={sample}
              index={index}
            />
          </List.Item>
        );
      }}
    />
  );
}
