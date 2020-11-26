import React from "react";
import { LaunchPageHeader } from "./LaunchPageHeader";
import { Col, Grid, Row, Tabs } from "antd";
import { useLaunch } from "./launch-context";
import { LaunchForm } from "./LaunchForm";
import { SPACE_LG } from "../../styles/spacing";

const { useBreakpoint } = Grid;

/**
 * React component to layout the content of the pipeline launch.
 * It will act as the top level logic controller.
 */
export function LaunchContent() {
  const [{ pipeline }] = useLaunch();
  const screens = useBreakpoint();
  console.log(screens);

  return (
    <div style={{ padding: SPACE_LG }}>
      <LaunchPageHeader pipeline={pipeline} />
      {screens.lg ? (
        <Row gutter={[16, 16]}>
          <Col sm={24} md={12} xl={14}>
            <LaunchForm />
          </Col>
          <Col sm={24} md={12} xl={10}>
            SAMPLES
          </Col>
        </Row>
      ) : (
        <Tabs>
          <Tabs.TabPane tab={"Details"} key="details">
            <LaunchForm />
          </Tabs.TabPane>
          <Tabs.TabPane tab={"FILES"} key="files">
            FILES GO IN HERE
          </Tabs.TabPane>
        </Tabs>
      )}
    </div>
  );
}
