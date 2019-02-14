import React from "react";
import { Col, Row } from "antd";
import { GalaxyDetailsForm } from "./GalaxyDetailsForm";
import { GalaxySubmissionSteps } from "./GalaxySubmissionSteps";

export class GalaxyExport extends React.Component {
  state = { submitted: false, completed: false };

  handleSubmitToGalaxy = ({ email, makepairedcollection }) =>
    this.setState({ email, makepairedcollection, submitted: true });

  setOath = oauth2 => this.setState({ oauth2 }, this.checkSubmission);

  setSamples = samples => this.setState({ samples }, this.checkSubmission);

  checkSubmission = () => {
    const completed =
      this.state.samples !== undefined && this.state.oauth2 !== undefined;
    if (completed) {
      const library = `IRIDA-${Math.random()
        .toString()
        .slice(2, 14)}`;
      const query = {
        _embedded: {
          library: { name: library },
          user: { email: this.state.email },
          addtohistory: true, // Default according to Phil Mabon
          makepairedcollection: this.state.makepairedcollection,
          oauth2: this.state.oauth2,
          samples: this.state.samples
        }
      };
      this.setState({ query });
    }
  };

  render() {
    return (
      <Row gutter={24}>
        <Col span={12}>
          <GalaxyDetailsForm handleSubmitToGalaxy={this.handleSubmitToGalaxy} />
        </Col>
        <Col span={12}>
          {this.state.submitted ? (
            <GalaxySubmissionSteps
              setOauth={this.setOath}
              setSamples={this.setSamples}
              query={this.state.query}
            />
          ) : null}
        </Col>
      </Row>
    );
  }
}
