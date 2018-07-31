import React from "react";
import { Layout } from "antd";
import { Table } from "../Table";
import { ToolPanel } from "../ToolPanel";
import { Toolbar } from "../Toolbar";
import { InfoBar } from "../InfoBar";

const { Sider, Content } = Layout;

export class LineListLayoutComponent extends React.Component {
  linelistRef = React.createRef();
  tableRef = React.createRef();
  state = {
    collapsed: true,
    height: 800
  };

  constructor(props) {
    super(props);
  }

  /**
   * Multiple components need to be updated when the window height changes.  This determines
   * the new height required and sets it into the state.
   */
  updateHeight = () => {
    if (window.innerHeight > 700) {
      const height = window.innerHeight - this.linelistRef.current.getBoundingClientRect().top - 90;
      this.setState({ height });
    } else {
      // Just preventing table from getting overly small!
      this.setState({ height: 300 });
    }
  };

  /**
   * Invoked immediately after a component is mounted (inserted into the tree).
   * Here we need to determine the initial height for the linelist component
   * and create an event handler to resize the table vertically if the browser is re-sized.
   */
  componentDidMount() {
    this.updateHeight();
    window.addEventListener("resize", this.updateHeight);
  }

  /**
   * Invoked immediately before a component is unmounted and destroyed.
   * Here we need ot unregister the event handler to prevent memory leaks in this component.
   */
  componentWillUnmount() {
    window.removeEventListener("resize", this.updateHeight);
  }

  /**
   * Toggle the open state of the tool panel.
   */
  toggleToolPanel = () => {
    this.setState({
      collapsed: !this.state.collapsed
    });
  };

  addSamplesToCart = () => {
    this.tableRef.current.addSamplesToCart();
  };

  exportCSV = () => {
    this.tableRef.current.exportCSV();
  };

  exportXLSX = () => {
    this.tableRef.current.exportXLSX();
  };

  render() {
    return (
      <div ref={this.linelistRef}>
        <Toolbar
          exportCSV={this.exportCSV}
          exportXLSX={this.exportXLSX}
          addSamplesToCart={this.addSamplesToCart}
          selectedCount={this.props.selectedCount}
        />
        <Layout className="ag-theme-balham">
          <Content>
            <Table {...this.props} height={this.state.height}
                   ref={this.tableRef}/>
          </Content>
          <Sider
            className="tool-panel-slider"
            trigger={null}
            collapsedWidth="20"
            width="300"
            collapsible
            collapsed={this.state.collapsed}
          >
            <div className="tool-panel-wrapper"
                 style={{ height: this.state.height }}>
              <ToolPanel {...this.props} />
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
        <InfoBar selectedCount={this.props.selectedCount}/>
      </div>
    );
  }
}
