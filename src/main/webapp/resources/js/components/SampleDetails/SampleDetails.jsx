import React from "react";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Drawer, Form } from "antd";
import styled from "styled-components";
import { COLOURS, FONTS, SPACING } from "./../../styles";
import { sampleDetailsActions } from "./reducer";
import { formatDate } from "../../utilities/date-utilities";
import { getI18N } from "../../utilities/i18n-utilties";

const { Item } = Form;

/*
Need to overwrite some of the default css in the Drawer component
to allow for scrolling of the body panel only. I want to keep the
sample name always visible.
 */
const StyledDrawer = styled(Drawer)`
  .ant-drawer-wrapper-body {
    overflow-y: hidden !important;
  }
  .ant-drawer-body {
    overflow-y: auto !important;
    height: 100%;
    padding-bottom: 100px;
  }
`;

const DetailsHeading = styled.h4`
  font-weight: ${FONTS.WEIGHT_HEAVY};
  font-size: ${FONTS.SIZE_LG};
  color: ${COLOURS.TEXT_HIGHLIGHTED};
  margin-bottom: ${SPACING.DEFAULT};
`;

const DetailValue = styled.div`
  font-size: ${FONTS.SIZE_DEFAULT};
  line-height: 30px;
  height: 30px;
  padding-left: 11px;
`;

const IdWrapper = styled.span`
  margin-left: ${SPACING.SMALL};
  font-weight: ${FONTS.WEIGHT_DEFAULT};
  font-size: 14px;
  background-color: ${COLOURS.BG_LIGHT};
  padding: ${SPACING.XSMALL};
  border-radius: 4px;
`;

/**
 * Use this component to display a drawer on the side of the screen displaying the
 * details of a sample.
 */
class SampleDetailsComponent extends React.Component {
  static propTypes = {
    visible: PropTypes.bool.isRequired,
    hideDetails: PropTypes.func.isRequired
  };

  render() {
    const { sample, metadata, visible } = this.props;
    if (typeof sample === "undefined") return null;
    const metadataKeys = Object.keys(metadata);
    return (
      <StyledDrawer
        title={
          <div>
            {sample.label}
            <IdWrapper>ID: {sample.identifier}</IdWrapper>
          </div>
        }
        placement="right"
        width={600}
        closable={true}
        onClose={this.props.hideDetails}
        visible={visible}
      >
        <Form layout="vertical">
          <DetailsHeading>{getI18N("SampleDetails.general")}</DetailsHeading>
          <Item label={getI18N("SampleDetails.description")}>
            <DetailValue>{sample.description}</DetailValue>
          </Item>
          <Item label={getI18N("SampleDetails.organism")}>
            <DetailValue>{sample.organism}</DetailValue>
          </Item>
          <Item label={getI18N("SampleDetails.createdDate")}>
            <DetailValue>
              {formatDate({ date: sample.createdDate })}
            </DetailValue>
          </Item>
          <Item label={getI18N("SampleDetails.modifiedDate")}>
            <DetailValue>
              {formatDate({ date: sample.modifiedDate })}
            </DetailValue>
          </Item>
          <hr />
          {metadataKeys.length === 0 ? null : (
            <div>
              <DetailsHeading>{getI18N("SampleDetails.metadata")}</DetailsHeading>
              {metadataKeys.map((key, i) => {
                const item = metadata[key];
                return (
                  <Item label={key} key={item.id}>
                    <DetailValue>{item.value}</DetailValue>
                  </Item>
                );
              })}
            </div>
          )}
        </Form>
      </StyledDrawer>
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

export const SampleDetails = connect(
  mapStateToProps,
  mapDispatchToProps
)(SampleDetailsComponent);
