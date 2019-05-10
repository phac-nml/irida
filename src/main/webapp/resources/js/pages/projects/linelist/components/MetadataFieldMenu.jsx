import React from "react";
import { connect } from "react-redux";
import { Dropdown, Icon, Menu, Modal, Typography } from "antd/lib/index";

const { Text } = Typography;

function MetadataFieldMenuComponent({ field }) {
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
      <Icon type="more" />
    </Dropdown>
  );
}

MetadataFieldMenuComponent.propTypes = {};

const mapStateToProps = state => ({});

const mapDispatchToProps = dispatch => ({});

export const MetadataFieldMenu = connect(
  mapStateToProps,
  mapDispatchToProps
)(MetadataFieldMenuComponent);
