import React from "react";
import { Layout, Menu, Icon } from "antd";
import { TableContainer } from "./Table";
import { ToolPanelContainer } from "./ToolPanel";

const { Sider, Content } = Layout;

export class TableLayoutComponent extends React.Component {
  state = {
    collapsed: true
  };

  constructor(props) {
    super(props);
  }

  toggle = () => {
    console.log("TOGGLINE");
    this.setState({
      collapsed: !this.state.collapsed
    });
  };

  render() {
    return (
      <Layout
        className="ag-theme-balham"
        style={{ minHeight: "100%" }}
      >
        <Content>
          <TableContainer />
        </Content>
        <Sider
          style={{
            backgroundColor: "rgba(245, 247, 247, 1.00)",
            border: "1px solid rgba(189, 195, 199, 1.00)",
            position: "relative",
            overflowX: "hidden"
          }}
          trigger={null}
          collapsedWidth="20"
          width="250"
          collapsible
          collapsed={this.state.collapsed}
        >
          <ToolPanelContainer />
          <div className="ag-grid-tool-panel--buttons">
            <button
              className="ag-grid-tool-panel--button"
              onClick={this.toggle}
            >
              Columns
            </button>
          </div>
        </Sider>
      </Layout>
    );
  }
}
