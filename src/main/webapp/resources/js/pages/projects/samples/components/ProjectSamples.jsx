import React from "react";
import { SamplesTable } from "./SamplesTable";
import { Col, Row } from "antd";
import SamplesMenu from "./SamplesMenu";

/**
 * React component to handle the layout and higher order functions of the project
 * samples page.
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function ProjectSamples() {
  return (
    <Row gutter={[16, 16]}>
      <Col span={24}>
        <SamplesMenu />
      </Col>
      <Col span={24}>
        <SamplesTable />
      </Col>
    </Row>
  );
}
