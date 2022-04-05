import React from "react";
import { SamplesTable } from "../SamplesTable";
import { Col, Row } from "antd";

export default function ProjectSamples({}) {
  return (
    <Row gutter={[16, 16]}>
      <Col span={24}>
        <SamplesTable />
      </Col>
    </Row>
  );
}
