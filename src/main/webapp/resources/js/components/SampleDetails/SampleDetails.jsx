import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Drawer } from "antd";

/**
 * Use this component to display a drawer on the side of the screen displaying the
 * details of a sample.
 */
class SampleDetailsComponent extends React.Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    hideSample: PropTypes.func.isRequired
  };

  render() {
    const { sample, visible } = this.props;
    return (
      typeof sample !== "undefined" ? <Drawer
        title={sample.label}
        placement="right"
        width={600}
        closable={true}
        onClose={this.props.hideSample}
        visible={visible}
      >
        I am the sample details drawer. Need to decide what we want in here.
      </Drawer> : null
    );
  }
}

const mapStateToProps = state => ({
  sample: state.cartPageReducer.sample
});

const mapDispatchToProps = dispatch => ({});

export default connect(mapStateToProps, mapDispatchToProps)(SampleDetailsComponent);

