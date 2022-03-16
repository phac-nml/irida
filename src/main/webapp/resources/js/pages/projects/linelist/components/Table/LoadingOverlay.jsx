import React from "react";
import { Progress, Space, Typography } from "antd";
import { Component } from "react";
import { connect } from "react-redux";

class LoadingOverlay extends Component {
  render() {
    return (
      <div className="ag-overlay-loading-center irida-ag-overlay">
        <Space
          style={{
            width: `100%`,
            height: 300,
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
