import React, { useEffect, useState } from "react";
import {
  deleteAnalysisSubmissions,
  fetchPagedAnalyses
} from "../apis/analysis/analysis";

let AnalysesContext;
const { Provider, Consumer } = (AnalysesContext = React.createContext());

/**
 * Context Provider the the Analyses Table.
 */
function AnalysesProvider({ children }) {
  const [tableState, setTableState] = useState({
    loading: true,
    analyses: undefined,
    search: "",
    current: 1,
    pageSize: 10,
    order: "descend",
    column: "createdDate",
    total: undefined,
    filters: {}
  });

  useEffect(() => updateTable(), [
    tableState.search,
    tableState.current,
    tableState.order,
    tableState.column,
    tableState.filters
  ]);

  /**
   * Called whenever the table needs to be re-rendered.
   */
  const updateTable = () => {
    setTableState({ ...tableState, loading: true });

    fetchPagedAnalyses({
      current: tableState.current - 1,
      pageSize: tableState.pageSize,
      sortColumn: tableState.column,
      sortDirection: tableState.order,
      search: tableState.search,
      filters: tableState.filters
    }).then(({ analyses, total }) => {
      setTableState({ ...tableState, ...{ total, analyses, loading: false } });
    });
  };

  /**
   * Handler for default table actions (paging, filtering, and sorting)
   *
   * @param {object} pagination
   * @param {object} filters
   * @param {object} sorter
   */
  const handleTableChange = (pagination, filters, sorter) => {
    const { pageSize, current } = pagination;
    const { order, field } = sorter;
    setTableState({
      ...tableState,
      ...{
        pageSize,
        current,
        order: order || "descend",
        column: field || "createdDate",
        filters
      }
    });
  };

  /**
   * Handler for deleting an analysis.
   *
   * @param {array} ids
   * @returns {void | Promise<*>}
   */
  const deleteAnalyses = ids =>
    deleteAnalysisSubmissions({ ids }).then(updateTable);

  return (
    <Provider value={{ ...tableState, handleTableChange, deleteAnalyses }}>
      {children}
    </Provider>
  );
}

export { AnalysesProvider, Consumer as AnalysesConsumer, AnalysesContext };
