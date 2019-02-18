import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import { Col, Row } from "antd";
import { GalaxyDetailsForm } from "./GalaxyDetailsForm";
import { actions } from "./reducer";
import { GalaxySubmission } from "./GalaxySubmission";
import { GalaxySamples } from "./GalaxySamples";
import { GalaxyStepOauth } from "./GalaxyStepOauth";

export class GalaxyExportComponent extends React.Component {
  static propTypes = {
    getGalaxySamples: PropTypes.func.isRequired
  };
  // checkSubmission = () => {
  //   const completed =
  //     this.state.samples !== undefined && this.state.oauth2 !== undefined;
  //   if (completed) {
  //     const library = `IRIDA-${Math.random()
  //       .toString()
  //       .slice(2, 14)}`;
  //     const query = {
  //       _embedded: {
  //         library: { name: library },
  //         user: { email: this.state.email },
  //         addtohistory: true, // Default according to Phil Mabon
  //         makepairedcollection: this.state.makepairedcollection,
  //         oauth2: this.state.oauth2,
  //         samples: this.state.samples
  //       }
  //     };
  //     this.setState({ query });
  //   }
  // };
  componentDidMount() {
    this.props.getGalaxySamples();
  }

  render() {
    return (
      <Row>
        <Col span={12}>
          <GalaxyDetailsForm />
          <GalaxySamples/>
          <GalaxyStepOauth/>
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
