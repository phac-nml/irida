import React from "react";

import PropTypes from "prop-types";
import { Button, Dropdown, Menu } from "antd";
import { grey1, grey4, grey5, grey6 } from "../../../styles/colors";
import { SPACE_SM, SPACE_XS } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { FolderOutlined, MoreOutlined } from "@ant-design/icons";

const DeleteMenu = ({ removeSample, removeProject }) => (
  <Menu
    className="t-delete-menu"
    style={{
      border: `1px solid ${grey4}`,
      borderRadius: 2,
      boxShadow: `0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24)`
    }}
  >
    <Menu.Item>
      <div onClick={removeSample} className="t-delete-sample">
        {i18n("SampleRenderer.remove.sample")}
      </div>
    </Menu.Item>
    <Menu.Item>
      <div onClick={removeProject} className="t-delete-project">
        {i18n("SampleRenderer.remove.project")}
      </div>
    </Menu.Item>
  </Menu>
);

const IconText = ({ text }) => (
  <span>
    <FolderOutlined
      style={{ marginRight: SPACE_XS, color: grey5, fontSize: 18 }}
    />
    {text}
  </span>
);

/**
 * Component to display sample information on the Cart page.  This is used
 * by ag-grid to render the a cell in the table.
 */
export class SampleRenderer extends React.Component {
  static propTypes = {
    api: PropTypes.shape({
      /** Function to open panel with details of a sample */
      displaySample: PropTypes.func.isRequired,
      /** Function to remove a sample from the cart */
      removeSample: PropTypes.func.isRequired,
      /** Function to remove an entire project from the cart */
      removeProject: PropTypes.func.isRequired
    }),
    /** Index in the ag-grid table of the current row */
    rowIndex: PropTypes.number.isRequired,
    /** All the information about the current sample */
    data: PropTypes.shape({
      label: PropTypes.string.isRequired,
      project: PropTypes.shape({
        label: PropTypes.string.isRequired,
        id: PropTypes.number.isRequired
      }).isRequired
    })
  };

  displaySample = () => this.props.displaySample(this.props.data);

  removeSample = () => this.props.removeSample(this.props.data);

  removeProject = () => this.props.removeProject(this.props.data.project.id);

  render() {
    const sample = this.props.data;
    return (
      <div
        style={{
          ...this.props.style,
          padding: SPACE_SM,
          backgroundColor: grey1,
          borderBottom: `1px solid ${grey4}`
        }}
      >
        <div
          className="t-cart-sample"
          key={sample.id}
          style={{
            display: "flex",
            alignItems: "center",
            marginBottom: SPACE_XS
          }}
        >
          <div style={{ flexGrow: 1 }}>
            <Button
              className="t-sample-name"
              size="small"
              onClick={this.displaySample}
            >
              {sample.label}
            </Button>
          </div>
          <Dropdown
            overlay={
              <DeleteMenu
                removeSample={this.removeSample}
                removeProject={this.removeProject}
              />
            }
            trigger={["hover"]}
          >
            <div style={{ display: "inline-block" }}>
              <MoreOutlined
                className="t-delete-menu-btn"
                style={{ color: grey6 }}
              />
            </div>
          </Dropdown>
        </div>
        <div>
          <IconText
            type="folder"
            text={
              <a href={setBaseUrl(`projects/${sample.project.id}`)}>
                {sample.project.label}
              </a>
            }
          />
        </div>
      </div>
    );
  }
}
