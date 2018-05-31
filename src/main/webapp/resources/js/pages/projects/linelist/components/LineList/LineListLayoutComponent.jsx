import React from "react";
import { Layout, Menu, Icon } from "antd";
import { TableContainer } from "../Table";
import { ToolPanelContainer } from "../ToolPanel";

const { Sider, Content } = Layout;

export class LineListLayoutComponent extends React.Component {
  state = {
    collapsed: false
  };

  constructor(props) {
    super(props);
  }

  /**
   * Toggle the open state of the tool panel.
   */
  toggleToolPanel = () => {
    this.setState({
      collapsed: !this.state.collapsed
    });
  };

  render() {
    return (
      <Layout className="ag-theme-balham" style={{ minHeight: "100%" }}>
        <Content>
          <TableContainer />
        </Content>
        <Sider
          className="tool-panel-slider"
          trigger={null}
          collapsedWidth="20"
          width="300"
          collapsible
          collapsed={this.state.collapsed}
        >
          <div className="tool-panel-wrapper">
            <ToolPanelContainer />
            <div className="ag-grid-tool-panel--buttons">
              <button
                className="ag-grid-tool-panel--button"
                onClick={this.toggleToolPanel}
              >
                Columns
              </button>
            </div>
          </div>
        </Sider>
      </Layout>
    );
  }
}
