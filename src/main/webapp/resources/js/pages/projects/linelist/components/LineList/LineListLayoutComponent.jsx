import React from "react";
import { Layout } from "antd";
import { Table } from "../Table";
import { Toolbar } from "../Toolbar";
import { InfoBar } from "../InfoBar";
import TableControlPanel from "../TableControlPanel/TableControlPanel";

const { Sider, Content } = Layout;

export class LineListLayoutComponent extends React.Component {
  linelistRef = React.createRef();

  state = {
    collapsed: true,
    height: 800
  };

  /**
   * Multiple components need to be updated when the window height changes.  This determines
   * the new height required and sets it into the state.
   */
  updateHeight = () => {
    if (window.innerHeight > 600) {
      const BOTTOM_BUFFER = 90;
      /*
      Determine the height the linelist should be based on the size of the window,
      and a small buffer at the bottom of the page.
       */
      const height =
        window.innerHeight -
        this.linelistRef.current.getBoundingClientRect().top -
        BOTTOM_BUFFER;
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
  toggleTableControlPanel = () => {
    this.setState({
      collapsed: !this.state.collapsed
    });
  };

  /**
   * Export table to a csv file
   */
  exportCSV = () => this.tableRef.exportCSV();

  /**
   * Export table to an excel file
   */
  exportXLSX = () => this.tableRef.exportXLSX();

  /**
   * Update the state of the filter
   * @param count
   */
  updateFilterCount = count => {
    this.setState({ filterCount: count });
  };

  /**
   * Scroll the table to the top.
   */
  scrollTableToTop = () => {
    this.tableRef.scrollToTop();
  };

  render() {
    return (
      <div ref={this.linelistRef}>
        <Toolbar
          exportCSV={this.exportCSV}
          exportXLSX={this.exportXLSX}
          addSamplesToCart={this.addSamplesToCart}
          selectedCount={this.props.selectedCount}
          scrollTableToTop={this.scrollTableToTop}
        />
        <Layout className="ag-theme-balham">
          <Content>
            <Table
              ref={tableReference => (this.tableRef = tableReference)}
              onFilter={this.updateFilterCount}
              height={this.state.height}
            />
          </Content>
          <Sider
            className="tool-panel-slider"
            trigger={null}
            collapsedWidth="42"
            width="300"
            collapsible
            collapsed={this.state.collapsed}
          >
            <TableControlPanel
              height={this.state.height}
              saved={this.props.saved}
              saveTemplate={this.props.saveTemplate}
              useTemplate={this.props.useTemplate}
              togglePanel={this.toggleTableControlPanel}
              templates={this.props.templates}
              current={this.props.current}
              templateModified={this.props.templateModified}
            />
          </Sider>
        </Layout>
        <InfoBar
          selectedCount={this.props.selectedCount}
          filterCount={
            this.state.filterCount
              ? this.state.filterCount
              : this.props.entries
              ? this.props.entries.length
              : 0
          }
          totalSamples={this.props.entries ? this.props.entries.length : 0}
        />
      </div>
    );
  }
}
