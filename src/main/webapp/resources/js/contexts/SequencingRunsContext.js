import React, { useEffect, useState } from "react";
import { fetchPagedAnalyses } from "../apis/analysis/analysis";

let SequencingRunsContext;
const { Provider, Consumer } = (SequencingRunsContext = React.createContext());

function SequencingRunsProvider({ children }) {
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
    updateTable,
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

  return <Provider value={{}}>{children}</Provider>;
}

export {
  SequencingRunsProvider,
  Consumer as SequencingRunConsumer,
  SequencingRunsContext
};
