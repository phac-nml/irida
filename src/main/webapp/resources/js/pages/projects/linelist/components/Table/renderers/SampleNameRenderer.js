import React, { Component } from "react";
import PropTypes from "prop-types";

const { BASE_URL } = window.TL;
class SampleNameRenderer extends Component {
  constructor(props) {
    super(props);
    this.href = `${BASE_URL}samples/${Number(props.data.sampleId)}`;
    this.name = props.value;
  }
  render() {
    return <a href={this.href}> {this.name} </a>;
  }
}

SampleNameRenderer.propTypes = {
  data: PropTypes.object.isRequired,
  value: PropTypes.string.isRequired
};

export default SampleNameRenderer;
