import React from "react";
import { Progress, Space, Typography } from "antd";
import { Component } from "react";
import { connect } from "react-redux";
import { grey5 } from "../../../../../styles/colors";

class LoadingOverlay extends Component {
  render() {
    return (
      <div
        style={{
          backgroundColor: "#FFFFFF",
          boxShadow: `rgba(0, 0, 0, 0.16) 0px 1px 4px;`,
          borderRadius: "2px",
          border: `1px solid ${grey5}`,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "100%",
        }}
      >
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
          <Progress
            percent={Math.floor(
              (this.props.loading.count / window.PAGE.totalSamples) * 100
            )}
            type="circle"
          />
          <Typography.Title level={3}>
            {i18n("linelist.loading", window.PAGE.totalSamples)}
          </Typography.Title>
        </Space>
      </div>
    );
  }
}

const mapStateToProps = (state) => ({
  loading: state.entries.loading,
});

export default connect(mapStateToProps)(LoadingOverlay);
