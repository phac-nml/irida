import React from "react";
import { render } from "react-dom";
import Launch from "./Launch";
import { Col, Row, Space } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { PipelineLaunchPage } from "../pipeline-launch/PipelineLaunchPage";
import { LaunchProvider } from "../pipeline-launch/launch-context";

const pipelineId = "f609c177-c268-4ad0-9d7f-9f9d5187fef7";

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
