import React from "react";
import { Button, Col, Dropdown, Icon, Menu, Row } from "antd";
import { COLOURS } from "../../../styles";

const DeleteMenu = ({ removeSample, removeProject }) => (
  <Menu>
    <Menu.Item>
      <div onClick={removeSample}>Remove Sample</div>
    </Menu.Item>
    <Menu.Item>
      <div onClick={removeProject}>Remove Project</div>
    </Menu.Item>
  </Menu>
);

export class SampleRenderer extends React.Component {
  static propTypes = {};

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
          <a onClick={this.displaySample}>{sample.label}</a>
          <div style={{ color: COLOURS.TEXT_SECONDARY }}>
            {sample.project.label}
          </div>
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
              <Icon type="ellipsis" style={{ color: COLOURS.DARK_GRAY }} />
            </Button>
          </Dropdown>
        </Col>
      </Row>
    );
  }
}
