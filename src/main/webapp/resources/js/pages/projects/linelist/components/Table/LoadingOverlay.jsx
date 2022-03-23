import React from "react";
import { Progress, Space, Typography } from "antd";
import { Component } from "react";
import { connect } from "react-redux";
import { grey1, grey5 } from "../../../../../styles/colors";
import styled from "styled-components";

const Overlay = styled.div`
  background-color: ${grey1};
  box-shadow: rgba(0, 0, 0, 0.16) 0px 1px 4px;
  border-radius: 4px;
  border: 1px solid ${grey5};
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
`;

/**
 * React component for AG GRID to display a loading overlay
 */
class LoadingOverlay extends Component {
  render() {
    const percentage =
      Math.floor((this.props.loading.count / window.PAGE.totalSamples) * 100);
    return (
      <Overlay>
        <Space
          style={{
            width: `100%`,
            height: 300,
            padding: 20,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            flexDirection: "column",
          }}
        >
          <Progress percent={percentage} type="circle" />
          <Typography.Title level={3}>
            {i18n("linelist.loading", window.PAGE.totalSamples)}
          </Typography.Title>
        </Space>
      </Overlay>
    );
  }
}

const mapStateToProps = (state) => ({
  loading: state.entries.loading,
});

export default connect(mapStateToProps)(LoadingOverlay);
