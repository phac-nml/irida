import { List } from "antd";
import React from "react";
import { SequencingRunSampleCard } from "./SequencingRunSampleCard";

/**
 * React component to render the samples list.
 * @param {array} samples - list of samples
 * @returns {JSX.Element} - Returns a sample list component
 */
export function SequencingRunSamplesList({ samples }) {
  return (
    <div
      style={{
        overflowY: "auto",
        maxHeight: window.innerHeight - 272,
        paddingRight: 10,
      }}
    >
      {samples.length > 0 && (
        <List
          grid={{ column: 1 }}
          dataSource={samples}
          renderItem={(sample, sampleIndex) => {
            return (
              <List.Item>
                <SequencingRunSampleCard
                  samples={samples}
                  sample={sample}
                  sampleIndex={sampleIndex}
                />
              </List.Item>
            );
          }}
        />
      )}
    </div>
  );
}
