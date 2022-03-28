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
        <List.Item className="t-sample-details-metadata-item">
          <List.Item.Meta
            title={
              <span className="t-sample-details-metadata__field">{item}</span>
            }
            description={
              <span className="t-sample-details-metadata__entry">
                {metadata[item].value}
              </span>
            }
          />
        </List.Item>
      )}
      ce
    />
  ) : (
    <Empty description={i18n("SampleDetails.no-metadata")} />
  );
}
