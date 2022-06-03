import React from "react";
import { Avatar, List } from "antd";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { FileOutlined } from "@ant-design/icons";
import { blue6 } from "../../styles/colors";

export function GenomeAssemblyListItem({ genomeAssembly, actions = [] }) {
  const { label, createdDate } = genomeAssembly.fileInfo;

  return (
    <List.Item>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          width: `100%`,
          border: "solid 1px #EEE",
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
          <List.Item actions={actions}>
            <List.Item.Meta
              title={
                <div
                  style={{ display: "flex", justifyContent: "space-between" }}
                >
                  <span>{label}</span>
                </div>
              }
              description={formatInternationalizedDateTime(createdDate)}
            />
          </List.Item>
        </List>
      </div>
    </List.Item>
  );
}
