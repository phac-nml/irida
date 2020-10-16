import React from "react";
import { render } from "react-dom";
import { Col, Row } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { PipelineLaunchPage } from "../pipeline-launch/PipelineLaunchPage";
import { LaunchProvider } from "../pipeline-launch/launch-context";

render(
  <Row style={{ paddingTop: SPACE_LG }}>
    <Col
      xs={{ span: 24, offset: 0 }}
      xl={{ offset: 4, span: 16 }}
      xxl={{ span: 8, offset: 8 }}
    >
      <LaunchProvider>
        <PipelineLaunchPage />
      </LaunchProvider>
    </Col>
  </Row>,
  document.querySelector(".root")
);
