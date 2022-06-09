import { List } from "antd";
import { DnDList } from "./example/DnDList";
import { DnDCard } from "./example/DnDCard";
import React from "react";

/**
 * React component to render the sample files list.
 * @param {object} item - a sample
 * @param {function} returnItemsForList - the function that filters the list
 * @param {function} setFiles - the function that sets the list
 * @returns {JSX.Element} - Returns a sample files list component
 */
export function SequencingRunSampleFilesList({
  item,
  returnItemsForList,
  setFiles,
}) {
  const files = returnItemsForList(item.list);

  return (
    <DnDList
      name={item.list}
      emptyDescription={i18n("SequencingRunSampleFilesList.empty")}
      dropCondition={files.length < 2}
      grid={{ column: 2 }}
      dataSource={files}
      renderItem={(item) => (
        <List.Item>
          <DnDCard id={item.id} setList={setFiles}>
            {item.fileName}
          </DnDCard>
        </List.Item>
      )}
    />
  );
}
