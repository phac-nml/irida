import React from "react";
import { Col, Row } from "antd";
import { GalaxyDetailsForm } from "./GalaxyDetailsForm";
import { GalaxySubmission } from "./GalaxySubmission";
import { GalaxySamples } from "./GalaxySamples";

/**
 * Main UI component to render the export to galaxy component to the interface.
 */
export function GalaxyExport() {
  return (
    <Row>
      <Col lg={12} md={24}>
        <GalaxyDetailsForm />
        <GalaxySamples />
        <GalaxySubmission />
      </Col>
    </Row>
  );
}
