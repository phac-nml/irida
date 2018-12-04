import React from "react";
import PropTypes from "prop-types";
import { Drawer } from "antd";

/**
 * Use this component to display a drawer on the side of the screen displaying the
 * details of a sample.
 */
export default class SampleDetailsComponent extends React.Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    hideSample: PropTypes.func.isRequired
  };

  render() {
    const { sample } = this.props;
    return (
      <Drawer
        title="THIS IS A DRAWER TITLE"
        placement="bottom"
        closable={true}
        onClose={this.props.hideSample}
        visible={this.props.visible}
      >
        I am the sample details drawer
      </Drawer>
    );
  }
}

