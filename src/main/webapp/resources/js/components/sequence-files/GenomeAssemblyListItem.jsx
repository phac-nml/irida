import React from "react";
import { Avatar, List } from "antd";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { FileOutlined } from "@ant-design/icons";
import { blue6, grey3 } from "../../styles/colors";

/**
 * Component to be used anywhere genome assemblies need to be listed
 * @param genomeAssembly The genome assembly to list
 * @param actions Actions for genome assembly
 * @returns {JSX.Element}
 * @constructor
 */
export function GenomeAssemblyListItem({ genomeAssembly, actions = [] }) {
  const { label, createdDate } = genomeAssembly.fileInfo;
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        width: `100%`,
        border: `solid 1px ${grey3}`,
        padding: 5,
      }}
    >
      <Avatar
        style={{
          backgroundColor: blue6,
          verticalAlign: "middle",
          marginRight: 10,
        }}
        icon={<FileOutlined />}
      />
      <List style={{ width: `100%` }} itemLayout="horizontal">
        <List.Item actions={actions} className="t-file-details">
          <List.Item.Meta
            title={
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <span className="t-file-label">{label}</span>
              </div>
            }
            description={formatInternationalizedDateTime(createdDate)}
          />
        </List.Item>
      </List>
    </div>
  );
}
