import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Drawer, Form } from "antd";
import { sampleDetailsActions } from "./reducer";
import { formatDate } from "../../utilities/date-utilities";

const { Item } = Form;

/**
 * Use this component to display a drawer on the side of the screen displaying the
 * details of a sample.
 */
class SampleDetails extends React.Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    hideDetails: PropTypes.func.isRequired
  };

  render() {
    const { sample, metadata, visible } = this.props;
    if (typeof sample === "undefined") return null;
    const metadataKeys = Object.keys(metadata);
    return (
      <Drawer
        title={sample.label}
        placement="right"
        width={600}
        closable={true}
        onClose={this.props.hideDetails}
        visible={visible}
      >
        <div className="sample-details-wrapper">
          <Form layout="vertical">
            <div className="sample-details-heading">_General</div>
            <Item label="_Description">
              <div className="sample-detail">{sample.description}</div>
            </Item>
            <Item label="_Organism">
              <div className="sample-detail">{sample.organism}</div>
            </Item>
            <Item label="_Created">
              <div className="sample-detail">
                {formatDate({ date: sample.createdDate })}
              </div>
            </Item>
            <Item label="_Modified">
              <div className="sample-detail">
                {formatDate({ date: sample.modifiedDate })}
              </div>
            </Item>
            <hr />
            {metadataKeys.length === 0 ? null : (
              <div>
                <div className="sample-details-heading">_Metadata</div>
                {metadataKeys.map((key, i) => {
                  const item = metadata[key];
                  return (
                    <Item label={key} key={item.id}>
                      <div className="sample-detail sample-detail__editable">
                        {item.value}
                      </div>
                    </Item>
                  );
                })}
              </div>
            )}
          </Form>
        </div>
      </Drawer>
    );
  }
}

const mapStateToProps = state => ({
  sample: state.sampleDetailsReducer.sample,
  metadata: state.sampleDetailsReducer.metadata,
  modifiable: state.sampleDetailsReducer.modifiable,
  visible: state.sampleDetailsReducer.visible
});

const mapDispatchToProps = dispatch => ({
  hideDetails: () => dispatch(sampleDetailsActions.closeDisplay())
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SampleDetails);
