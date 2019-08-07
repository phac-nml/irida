import React from "react";
import {
  deleteAnalysisSubmission,
  downloadAnalysis,
  fetchAllPipelinesStates,
  fetchAllPipelinesTypes,
  fetchPagedAnalyses
} from "../apis/analysis/analysis";
import * as PropTypes from "prop-types";

let AnalysesContext;
const { Provider, Consumer } = (AnalysesContext = React.createContext());

/**
 * Context Provider the the Analyses Table.
 */
class AnalysesProvider extends React.Component {
  static propTypes = {
    /**
     * Child element of this react component
     *
     * @ignore
     */
    children: PropTypes.element
  };

  constructor(props) {
    super(props);

    this.state = {
      analyses: undefined,
      search: "",
      current: 1,
      pageSize: 10,
      order: "descend",
      column: "createdDate",
      total: undefined,
      filters: {},
      clearFilters: this.clearFilters,
      onSearch: this.onSearch,
      handleTableChange: this.handleTableChange,
      deleteAnalysis: this.deleteAnalysis,
      downloadAnalysis: this.downloadAnalysis,
      updateTable: this.updateTable
    };
  }

  /*
  Made this asynchronous in order to use `await` so that we can get all
  the required values before we initialize the table.
   */
  async componentDidMount() {
    const [pipelineStates, types] = await Promise.all([
      fetchAllPipelinesStates(),
      fetchAllPipelinesTypes()
    ]);
    this.setState({ pipelineStates, types });
  }

  /**
   * Called whenever the table needs to be re-rendered.
   */
  updateTable = () => {
    this.setState({ loading: true }, () => {
      const params = {
        current: this.state.current - 1,
        pageSize: this.state.pageSize,
        sortColumn: this.state.column,
        sortDirection: this.state.order,
        search: this.state.search,
        filters: this.state.filters
      };

      fetchPagedAnalyses(params).then(data => {
        this.setState({
          analyses: data.analyses,
          total: data.total,
          loading: false
        });
      });
    });
  };

  /**
   * Handles search values entered in the tables global search box.
   *
   * @param {string} search - the value to search for
   */
  onSearch = search => this.setState({ search });

  clearFilters = () => this.setState({ filters: {}, search: "" });

  /**
   * Handler for default table actions (paging, filtering, and sorting)
   *
   * @param {object} pagination
   * @param {object} filters
   * @param {object} sorter
   */
  handleTableChange = (pagination, filters, sorter) => {
    const { pageSize, current } = pagination;
    const { order, field } = sorter;

    this.setState({
      pageSize,
      current,
      order: order || "descend",
      column: field || "createdDate",
      filters
    }, () => this.updateTable());
  };

  /**
   * Handler for deleting an analysis.
   *
   * @param {number} id
   * @returns {void | Promise<*>}
   */
  deleteAnalysis = id =>
    deleteAnalysisSubmission({ id }).then(this.updateTable);

  downloadAnalysis = id => downloadAnalysis({ id });

  render() {
    return <Provider value={this.state}>{this.props.children}</Provider>;
  }
}

export { AnalysesProvider, Consumer as AnalysesConsumer, AnalysesContext };
