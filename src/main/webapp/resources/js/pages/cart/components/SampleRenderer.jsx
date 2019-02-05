import React from "react";
import PropTypes from "prop-types";
import { Button, Col, Dropdown, Icon, Menu, Row } from "antd";
import styled from "styled-components";
import { COLOURS, FONTS } from "../../../styles";
import { getI18N } from "../../../utilities/i18n-utilties";

const SampleLink = styled.a`
  font-size: 1.4rem;
  font-weight: 700;
  color: ${COLOURS.TEXT_HIGHLIGHTED};
`;

const ProjectLink = styled.a`
  display: block;
  color: ${COLOURS.TEXT_MUTED};
  font-size: ${FONTS.SIZE_DEFAULT};
  text-decoration: underline;
`;

const DeleteMenu = ({ removeSample, removeProject }) => (
  <Menu>
    <Menu.Item>
      <div onClick={removeSample}>
        {getI18N("SampleRenderer.remove.sample")}
      </div>
    </Menu.Item>
    <Menu.Item>
      <div onClick={removeProject}>
        {getI18N("SampleRenderer.remove.project")}
      </div>
    </Menu.Item>
  </Menu>
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

  displaySample = () => this.props.api.displaySample(this.props.data);

  removeSample = () =>
    this.props.api.removeSample(this.props.rowIndex, this.props.data);

  removeProject = () =>
    this.props.api.removeProject(this.props.data.project.id);

  render() {
    const sample = this.props.data;
    return (
      <Row type="flex" align="top" justify="space-between">
        <Col>
          <SampleLink onClick={this.displaySample}>{sample.label}</SampleLink>
          <ProjectLink
            href={`${window.TL.BASE_URL}projects/${sample.project.id}/linelist`}
          >
            {sample.project.label}
          </ProjectLink>
        </Col>
        <Col>
          <Dropdown
            overlay={
              <DeleteMenu
                removeSample={this.removeSample}
                removeProject={this.removeProject}
              />
            }
            trigger={["click"]}
          >
            <Button ghost shape="circle" size="small">
              <Icon type="ellipsis" style={{ color: COLOURS.TEXT_MUTED }} />
            </Button>
          </Dropdown>
        </Col>
      </Row>
    );
  }
}
