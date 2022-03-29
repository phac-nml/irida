import React from "react";
import { render } from "react-dom";
import { Col, Row } from "antd";
import { SamplesTable } from "./SamplesTable";

render(
  <Row gutter={[16, 16]}>
    <Col span={24}>
      <SamplesTable />
    </Col>
  </Row>,
  document.getElementById("root")
);
