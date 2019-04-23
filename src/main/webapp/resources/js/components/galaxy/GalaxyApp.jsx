import React from "react";
import PropTypes from "prop-types";
import { GalaxyStateProvider } from "./GalaxyState";
import { initialState, reducer } from "./reducer";
import {
  Card,
  Col,
  Row
} from "antd";
import { GalaxyDetailsForm } from "./GalaxyDetailsForm";
import { GalaxySamples } from "./GalaxySamples";
import { GalaxySubmitError } from "./GalaxySubmitError";
import { GalaxySubmission } from "./GalaxySubmission";

export default function GalaxyApp() {
  return (
    <GalaxyStateProvider initialState={initialState} reducer={reducer}>
      <Row>
        <Col lg={{ span: 12, offset: 6 }} md={24}>
          <Card>
            <GalaxyDetailsForm />
            <GalaxySamples />
            <GalaxySubmitError />
            <GalaxySubmission />
          </Card>
        </Col>
      </Row>
    </GalaxyStateProvider>
  );
}

GalaxyApp.propTypes = {};
