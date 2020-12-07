import React from "react";
import { LaunchPageHeader } from "./LaunchPageHeader";
import { Col, Grid, Row } from "antd";
import { useLaunch } from "./launch-context";
import { LaunchForm } from "./LaunchForm";
import { SPACE_LG } from "../../styles/spacing";
import { LaunchFiles } from "./LaunchFiles";

const { useBreakpoint } = Grid;

/**
 * React component to layout the content of the pipeline launch.
 * It will act as the top level logic controller.
 */
export function LaunchContent() {
  const [{ pipeline }] = useLaunch();

  return (
    <div style={{ padding: SPACE_LG }}>
      <LaunchPageHeader pipeline={pipeline} />
      <Row gutter={[16, 16]}>
        <Col sm={24} md={12} xl={14}>
          <LaunchForm />
        </Col>
        <Col sm={24} md={12} xl={10}>
          <LaunchFiles />
        </Col>
      </Row>
    </div>
  );
}
