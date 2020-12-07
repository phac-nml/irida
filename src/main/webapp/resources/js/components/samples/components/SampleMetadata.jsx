import React from "react";
import { Empty, List } from "antd";

/**
 * React component to display metadata associated with a sample
 *
 * @param {array} metadata
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleMetadata({ metadata }) {
  return Object.keys(metadata).length ? (
    <List
      itemLayout="horizontal"
      dataSource={Object.keys(metadata).sort((a, b) => a.localeCompare(b))}
      renderItem={(item) => (
        <List.Item>
          <List.Item.Meta title={item} description={metadata[item].value} />
        </List.Item>
      )}
    />
  ) : (
    <Empty description={i18n("SampleDetails.no-metadata")} />
  );
}
