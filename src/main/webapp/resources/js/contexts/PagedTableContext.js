import React, { useEffect, useState } from "react";
import { fetchPageTableUpdate } from "../apis/paged-table/paged-table";
import debounce from "lodash/debounce";

let PagedTableContext;
const { Provider, Consumer } = (PagedTableContext = React.createContext());

/**
 * Provider for all ant.design server paged tables.
 * @param children Child DOM elements
 * @param {string} url - to fetch the table contents from
 * @returns {*}
 * @constructor
 */
function PagedTableProvider({ children, url }) {
  const [tableState, setTableState] = useState({
    loading: true,
    dataSource: undefined,
    search: "",
    current: 1,
    pageSize: 10,
    order: "descend",
    column: "createdDate",
    total: undefined,
    filters: {}
  });

  /*
  Table updated whenever one of these are changed.
   */
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
    fetchPageTableUpdate(url, {
      current: tableState.current - 1,
      pageSize: tableState.pageSize,
      sortField: tableState.column,
      sortDirection: tableState.order,
      search: tableState.search,
      filters: tableState.filters
    }).then(data => {
      setTableState({
        ...tableState,
        ...{ total: data.total, dataSource: data.models, loading: false }
      });
    });
  };

  /**
   * Required when using an external filter on a table.
   * @param term - search term
   */
  const onSearch = debounce(term => {
    setTableState({
      ...tableState,
      search: term
    });
  }, 300);

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

  return (
    <Provider
      value={{ ...tableState, handleTableChange, updateTable, onSearch }}
    >
      {children}
    </Provider>
  );
}

export {
  PagedTableProvider,
  Consumer as PagedTableConsumer,
  PagedTableContext
};
