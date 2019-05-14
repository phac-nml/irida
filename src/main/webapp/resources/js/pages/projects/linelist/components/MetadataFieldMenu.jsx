import React from "react";
import { Dropdown, Icon, Menu, Modal, Typography } from "antd/lib/index";

const { Text } = Typography;

export function MetadataFieldMenu({ field, removeColumnData }) {
  function confirmDelete() {
    Modal.confirm({
      content: (
        <div>
          <p>
            This will delete all data in the{" "}
            <span style={{ textDecoration: "underline" }}>
              {field.headerName}
            </span>{" "}
            column. This cannot be undone.
          </p>
          <p>
            Locked (
            <Icon type="lock" theme="twoTone" />) samples will not be effected
            by this.
          </p>
        </div>
      ),
      title: (
        <div>
          Delete{" "}
          <span style={{ fontWeight: 600, textDecoration: "underline" }}>
            {field.headerName}
          </span>{" "}
          data
        </div>
      ),
      okType: "danger",
      onOk: () => removeColumnData(field.headerName),
      okText: "DELETE COLUMN DATA"
    });
  }
  return (
    <Dropdown
      trigger={["click"]}
      overlay={
        <Menu>
          <Menu.Item onClick={confirmDelete}>Delete All Column Data</Menu.Item>
        </Menu>
      }
    >
      <Icon style={{marginLeft: ".5rem"}} type="more" />
    </Dropdown>
  );
}
