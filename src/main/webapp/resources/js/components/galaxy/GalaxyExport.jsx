import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import { Col, Row } from "antd";
import { GalaxyDetailsForm } from "./GalaxyDetailsForm";
import { actions } from "./reducer";
import { GalaxySubmission } from "./GalaxySubmission";
import { GalaxySamples } from "./GalaxySamples";
import { GalaxyStepOauth } from "./GalaxyStepOauth";

/**
 * Main UI component to render the export to galaxy component to the interface.
 */
export class GalaxyExportComponent extends React.Component {
  static propTypes = {
    getGalaxySamples: PropTypes.func.isRequired
  };

  componentDidMount() {
    /*
    As soon as the page renders start asking for the samples to be foramtted.
    This will get a list of link to the samples to be transferred to galaxy.
     */
    this.props.getGalaxySamples();
  }

  render() {
    return (
      <Row>
        <Col lg={12} md={24}>
          <GalaxyDetailsForm />
          <GalaxySamples />
          <GalaxyStepOauth />
          <GalaxySubmission />
        </Col>
      </Row>
    );
  }
}

/*
Connect the component to redux to get all the required values and functions.
 */
const mapStateToProps = state => ({});

const mapDispatchToProps = dispatch => ({
  getGalaxySamples: () => dispatch(actions.getGalaxySamples())
});

export const GalaxyExport = connect(
  mapStateToProps,
  mapDispatchToProps
)(GalaxyExportComponent);
