import React, { useEffect, useReducer } from "react";
import { fetchPagedAnalyses } from "../../apis/analysis/analysis";
import { Table } from "antd";

const initialState = {
  analyses: undefined,
  total: undefined,
  search: "",
  current: 0,
  pageSize: 10,
  order: "descend",
  column: "createdDate"
};

const types = {
  LOADING: "ANALYSES/LOADING",
  LOADED: "ANALYSES_TABLE/LOADED",
  SEARCH: "ANALYSES_TABLE/SEARCH"
};

const reducer = (state, action) => {
  switch (action.type) {
    case types.LOADING:
      return { ...state, loading: true };
    case types.LOADED:
      return {
        ...state,
        loading: false,
        analyses: action.payload.analyses,
        total: action.payload.total
      };
  }
};

export function AnalysesTable() {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    fetch();
  }, [state.current, state.pageSize, state.column, state.order, state.search]);

  const fetch = () => {
    dispatch({ type: types.LOADING });
    const params = {
      current: state.current,
      pageSize: state.pageSize,
      sortColumn: state.column,
      sortDirection: state.order, search: state.search
    };

    fetchPagedAnalyses(params).then(data => dispatch({
      type: types.LOADED, payload: {
        total: data.total,
        analyses: data.analyses
      }
    }));
  };

  const handleTableChange = (pagination, filters, sorter) => {
    const { pageSize, current } = pagination;
    const { order, field } = sorter;
    dispatch({
      type: TYPES.TABLE_CHANGE,
      payload: {
        pageSize,
        current,
        order: order || "descend",
        field: field || "modifiedDate"
      }
    });
  };

  const onSearch = value => dispatch({
    type: types.SEARCH,
    payload: { search: value }
  });

  const columns = [
    {
      title: "AnalysesTable_th_id",
      dataIndex: "id",
      key: "identifier",
      sorter: true,
      width: 50
    },
    {
      title: "AnalysesTable_th_name",
      dataIndex: "name",
      key: "name",
      sorter: true
    },
    {
      title:"AnalysesTable_th_created_date",
      dataIndex: "createdDate",
      key: "createdDate",
      sorter: true
    }
  ];

  return (<Table
    reowKey={record => record.id} loading={state.loading}
    pagination={{ total: state.total, pageSize: state.pageSize }}
    columns={columns} dataSource={state.analyses} onChange={handleTableChange}/>);
}

AnalysesTable.propTypes = {};
