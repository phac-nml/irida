import { List } from "antd";
import React from "react";
import { FixedSizeList as VList } from "react-window";
import ShareSamplesListItem from "./ShareSampleListItem";

/**
 * Component to render a virtual list of sample to be copied to another project.
 * @param list
 * @returns {JSX.Element}
 * @constructor
 */
export function SharedSamplesList({ list = [], itemActionsRequired = true }) {
  const Row = ({ index, style }) => {
    const sample = list[index];

    return (
      <ShareSamplesListItem
        sample={sample}
        style={style}
        actionsRequired={itemActionsRequired}
      />
    );
  };

  const ROW_HEIGHT = 55;
  const MAX_LIST_HEIGHT = 600;
  const height =
    list.length * ROW_HEIGHT < MAX_LIST_HEIGHT
      ? list.length * ROW_HEIGHT + 2 // 2 is to offset for borders
      : MAX_LIST_HEIGHT;

  return (
    <List bordered rowKey={(sample) => sample.name}>
      <VList
        height={height}
        itemCount={list.length}
        itemSize={ROW_HEIGHT}
        width="100%"
      >
        {Row}
      </VList>
    </List>
  );
}
