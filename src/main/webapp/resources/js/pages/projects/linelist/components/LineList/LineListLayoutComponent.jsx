import React from "react";
import { Layout } from "antd";
import { LinelistTable } from "../LineListTable";
import { Toolbar } from "../Toolbar";
import { InfoBar } from "../InfoBar";
import TableControlPanel from "../TableControlPanel/TableControlPanel";
import { MetadataEntriesProvider } from "../../../../../contexts/MetadataEntriesContext";
import { MetadataTemplatesProvider } from "../../../../../contexts/MetadataTemplatesContext";

const { Sider, Content } = Layout;

export class LineListLayoutComponent extends React.Component {
  linelistRef = React.createRef();

  state = {
    collapsed: true,
  };

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
        <Layout>
          <Content style={{backgroundColor: "white"}}>
            <MetadataTemplatesProvider>
              <MetadataEntriesProvider>
                <LinelistTable
                  ref={tableReference => (this.tableRef = tableReference)}
                  onFilter={this.updateFilterCount}
                />
              </MetadataEntriesProvider>
            </MetadataTemplatesProvider>
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
