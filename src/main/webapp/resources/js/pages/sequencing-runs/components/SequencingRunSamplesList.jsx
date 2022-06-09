import { Card, Empty, List } from "antd";
import React from "react";
import { SequencingRunSampleFilesList } from "./SequencingRunSampleFilesList";

/**
 * React component to render the samples list.
 * @param {array} samples - list of samples
 * @param {function} returnItemsForList - the function that filters the list
 * @param {function} setFiles - the function that sets the list
 * @returns {JSX.Element} - Returns a sample list component
 */
export function SequencingRunSamplesList({
  samples,
  returnItemsForList,
  setFiles,
}) {
  return samples.length === 0 ? (
    <Empty
      description={i18n("SequencingRunSamplesList.empty")}
      image={Empty.PRESENTED_IMAGE_SIMPLE}
    />
  ) : (
    <List
      grid={{ column: 1 }}
      dataSource={samples}
      renderItem={(item) => (
        <List.Item>
          <Card title={item.sampleName}>
            <SequencingRunSampleFilesList
              item={item}
              returnItemsForList={returnItemsForList}
              setFiles={setFiles}
            />
          </Card>
        </List.Item>
      )}
    />
  );
}
