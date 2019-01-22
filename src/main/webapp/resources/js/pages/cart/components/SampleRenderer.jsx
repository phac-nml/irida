import React from "react";
import { connect } from "react-redux";
import { sampleDetailsActions } from "../../../components/SampleDetails";
import { cartPageActions } from "../reducer";
import { Button, Col, Icon, Row, Tooltip } from "antd";
import { COLOURS } from "../../../styles";

export default class SampleRenderer extends React.Component {
  static propTypes = {};

  displaySample = () => this.props.displaySample(this.props.data);

  removeSample = () =>
    this.props.removeSample({
      sampleId: this.props.data.id,
      projectId: this.props.data.project.id
    });

  render() {
    const sample = this.props.data;
    return (
      <Row type="flex" align="top" justify="space-between">
        <Col>
          <a onClick={this.displaySample}>{sample.label}</a>
          <div style={{ color: COLOURS.TEXT_SECONDARY }}>
            {sample.project.label}
          </div>
        </Col>
        <Col>
          <Tooltip title="Remove from cart">
            <Button
              ghost
              shape="circle"
              size="small"
              onClick={this.removeSample}
            >
              <Icon
                type="close-circle"
                style={{ color: COLOURS.TEXT_PRIMARY }}
              />
            </Button>
          </Tooltip>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = state => ({});

const mapDispatchToProps = dispatch => ({
  displaySample: sample => dispatch(sampleDetailsActions.displaySample(sample)),
  emptyCart: () => dispatch(cartPageActions.emptyCart()),
  removeSample: data => dispatch(cartPageActions.removeSample(data))
});

const CartSampleRenderer = connect(
  mapStateToProps,
  mapDispatchToProps
)(SampleRenderer);

export { CartSampleRenderer };
