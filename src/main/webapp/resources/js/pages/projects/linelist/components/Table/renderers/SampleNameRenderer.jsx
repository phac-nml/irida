import React from "react";
import PropTypes from "prop-types";

const { BASE_URL } = window.TL;

export class SampleNameRenderer extends React.Component {
  constructor(props) {
    super(props);
    this.href = `${BASE_URL}projects/${Number(
      props.data.projectId
    )}/samples/${Number(props.data.sampleId)}`;
    this.name = props.data.sample;
  }

  render() {
    return (
      <a target="_blank" className="t-sample-name" href={this.href}>
        {this.name}
      </a>
    );
  }
}

SampleNameRenderer.propTypes = {
  data: PropTypes.object.isRequired
};
