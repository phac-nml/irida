import React, { useState, useEffect, useReducer } from "react";
import {
  deleteAnalysisSubmission,
  fetchAllPipelinesStates,
  fetchAllPipelinesTypes,
  fetchPagedAnalyses
} from "../apis/analysis/analysis";

const { Provider, Consumer } = React.createContext();

const initialState = {
  search: "",
  current: 1,
  pageSize: 10,
  order: "descend",
  column: "createdDate",
  filters: {}
};

const TYPES = {
  LOADED: "ANALYSES_TABLE/LOADED",
  SEARCH: "ANALYSES_TABLE/SEARCH",
  TABLE_CHANGE: "ANALYSES_TABLE/TABLE_CHANGE"
};

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.LOADED:
      return {
        ...state,
        loading: false,
        analyses: action.payload.analyses,
        total: action.payload.total
      };
    case TYPES.SEARCH:
      return { ...state, search: action.payload.search, current: 1 };
    case TYPES.TABLE_CHANGE:
      return {
        ...state,
        ...action.payload
      };
    default:
      return { ...state };
  }
};

function AnalysesProvider({ children }) {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [loading, setLoading] = useState(false);
  const [analyses, setAnalyses] = useState(undefined);
  const [total, setTotal] = useState(undefined);
  const [pipelineStates, setPipelineStates] = useState(undefined);
  const [types, setTypes] = useState(undefined);

  useEffect(() => {
    fetchAllPipelinesStates().then(data => setPipelineStates(data));
    fetchAllPipelinesTypes().then(data => setTypes(data));
  }, []);

  function updateTable() {
    setLoading(true);
    const params = {
      current: state.current - 1,
      pageSize: state.pageSize,
      sortColumn: state.column,
      sortDirection: state.order,
      search: state.search,
      filters: state.filters
    };

    fetchPagedAnalyses(params).then(data => {
      setAnalyses(data.analyses);
      setTotal(data.total);
      setLoading(false);
    });
  }

  useEffect(() => {
    updateTable();
  }, [state]);

  const onSearch = value =>
    dispatch({
      type: TYPES.SEARCH,
      payload: { search: value, current: 1 }
    });

  const handleTableChange = (pagination, filters, sorter) => {
    const { pageSize, current } = pagination;
    const { order, field } = sorter;
    const formattedFilter = {};
    Object.keys(filters).forEach(f => (formattedFilter[f] = filters[f][0]));
    dispatch({
      type: TYPES.TABLE_CHANGE,
      payload: {
        pageSize,
        current,
        order: order || "descend",
        column: field || "createdDate",
        filters: formattedFilter
      }
    });
  };

  const deleteAnalysis = id => deleteAnalysisSubmission({ id }).then(() => updateTable());

  return (
    <Provider
      value={{
        loading,
        analyses,
        total,
        pipelineStates,
        types,
        current: state.current,
        size: state.pageSize,
        column: state.column,
        order: state.order,
        search: state.search,
        filters: state.filters,
        onSearch,
        handleTableChange,
        deleteAnalysis
      }}
    >
      {children}
    </Provider>
  );
}

export { AnalysesProvider, Consumer as AnalysesConsumer };
