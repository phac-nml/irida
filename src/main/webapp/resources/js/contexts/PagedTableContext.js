import React, { useEffect, useReducer } from "react";
import { fetchPageTableUpdate } from "../apis/paged-table/paged-table";
import debounce from "lodash/debounce";

let PagedTableContext;
const { Provider, Consumer } = (PagedTableContext = React.createContext());

const initialState = {
  dataSource: undefined,
  loading: true,
  search: "",
  current: 1,
  pageSize: 10,
  order: "descend",
  column: "createdDate",
  total: undefined,
  filters: {}
};

const types = {
  LOADING: 0,
  LOADED: 1,
  SEARCH: 2,
  CHANGE: 3
};

function reducer(state, action) {
  switch (action.type) {
    case types.LOADING:
      return { ...state, loading: true };
    case types.LOADED:
      return {
        ...state,
        loading: false,
        dataSource: action.payload.dataSource,
        total: action.payload.total
      };
    case types.SEARCH:
      return {
        ...state,
        search: action.payload.term
      };
    case types.CHANGE:
      return {
        ...state,
        pageSize: action.payload.pageSize,
        current: action.payload.current,
        order: action.payload.order || "descend",
        column: action.payload.column || "createdDate",
        filters: action.payload.filters || {}
      };
    default:
      return { ...state };
  }
}

/**
 * Provider for all ant.design server paged tables.
 * @param children Child DOM elements
 * @param {string} url - to fetch the table contents from
 * @returns {*}
 * @constructor
 */
function PagedTableProvider({ children, url }) {
  const [state, dispatch] = useReducer(reducer, initialState);

  /*
  Table updated whenever one of these are changed.
   */
  useEffect(updateTable, [
    state.search,
    state.current,
    state.order,
    state.column,
    state.filters
  ]);

  /**
   * Called whenever the table needs to be re-rendered.
   */
  function updateTable() {
    dispatch({ type: types.LOADING });
    fetchPageTableUpdate(url, {
      current: state.current - 1,
      pageSize: state.pageSize,
      sortColumn: state.column,
      sortDirection: state.order,
      search: state.search,
      filters: state.filters
    }).then(({ dataSource, total }) =>
      dispatch({
        type: types.LOADED,
        payload: {
          dataSource,
          total
        }
      })
    );
  }

  /**
   * Required when using an external filter on a table.
   * @param term - search term
   */
  const onSearch = debounce(
    term => dispatch({ type: types.SEARCH, payload: { term } }),
    300
  );

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
    dispatch({
      type: types.CHANGE,
      payload: {
        pageSize,
        current,
        order,
        column: field,
        filters
      }
    });
  };

  return (
    <Provider
      value={{
        onSearch,
        updateTable,
        pagedConfig: {
          dataSource: state.dataSource,
          loading: state.loading,
          onChange: handleTableChange,
          pagination: {
            total: state.total,
            pageSize: state.pageSize,
            hideOnSinglePage: true
          }
        }
      }}
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
