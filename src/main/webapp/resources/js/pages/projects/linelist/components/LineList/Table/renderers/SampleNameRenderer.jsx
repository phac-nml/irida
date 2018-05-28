import React from "react";
import PropTypes from "prop-types";

const { BASE_URL } = window.TL;

export class SampleNameRenderer extends React.Component {
  constructor(props) {
    super(props);
    this.href = `${BASE_URL}samples/${Number(props.data.sampleId)}`;
    this.name = props.value;
  }
  render() {
    return (
      <a target="_blank" href={this.href}>
        {this.name}
      </a>
    );
  }
}

SampleNameRenderer.propTypes = {
  data: PropTypes.array.isRequired
};
