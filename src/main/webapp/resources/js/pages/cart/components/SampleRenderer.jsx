import React from "react";
import PropTypes from "prop-types";
import { Button, Dropdown, Icon, Menu, List } from "antd";
import styled from "styled-components";
import { getI18N } from "../../../utilities/i18n-utilties";
import {
  FONT_COLOR_MUTED,
  FONT_COLOR_PRIMARY,
  FONT_SIZE_SMALL
} from "../../../styles/fonts";
import { grey1, grey2, grey3, grey4 } from "../../../styles/colors";
import { SPACE_XS, SPACE_MD, SPACE_SM } from "../../../styles/spacing";

const ProjectLink = styled.a`
  display: block;
  color: ${FONT_COLOR_MUTED};
  font-size: ${FONT_SIZE_SMALL};
  text-decoration: underline;
`;

const DeleteMenu = ({ removeSample, removeProject }) => (
  <Menu className="t-delete-menu" style={{ border: `1px solid ${grey4}` }}>
    <Menu.Item>
      <div onClick={removeSample} className="t-delete-sample">
        {getI18N("SampleRenderer.remove.sample")}
      </div>
    </Menu.Item>
    <Menu.Item>
      <div onClick={removeProject} className="t-delete-project">
        {getI18N("SampleRenderer.remove.project")}
      </div>
    </Menu.Item>
  </Menu>
);

const IconText = ({ type, text }) => (
  <span>
    <Icon type={type} style={{ marginRight: SPACE_XS }} />
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
          padding: `0 ${SPACE_SM}`,
          backgroundColor: grey1,
          borderBottom: `1px solid ${grey4}`
        }}
        className="t-cart-sample"
      >
        <List.Item
          key={sample.id}
          extra={
            <Dropdown
              overlay={
                <DeleteMenu
                  removeSample={this.removeSample}
                  removeProject={this.removeProject}
                />
              }
              trigger={["click"]}
            >
              <Button className="t-delete-menu-btn" shape="circle" size="small">
                <Icon type="ellipsis" />
              </Button>
            </Dropdown>
          }
          actions={[
            <IconText
              type="folder"
              text={
                <a href={`${window.TL.BASE_URL}projects/${sample.project.id}`}>
                  {sample.project.label}
                </a>
              }
            />
          ]}
        >
          <List.Item.Meta
            title={
              <Button
                className="t-sample-name"
                size="small"
                onClick={this.displaySample}
              >
                {sample.label}
              </Button>
            }
          />
        </List.Item>
      </div>
    );
  }
}
