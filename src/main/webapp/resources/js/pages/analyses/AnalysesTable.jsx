import React, { useEffect, useReducer, useState } from "react";
import {
  fetchAllPipelinesStates,
  fetchPagedAnalyses
} from "../../apis/analysis/analysis";
import { Input, Row, Table } from "antd";
import { PageWrapper } from "../../components/page/PageWrapper";
import {
  dateColumnFormat,
  idColumnFormat,
  nameColumnFormat
} from "../../components/ant.design/table-renderers";

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

export function AnalysesTable() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [loading, setLoading] = useState(false);
  const [analyses, setAnalyses] = useState(undefined);
  const [total, setTotal] = useState(undefined);
  const [pipelineStates, setPipelineStates] = useState(undefined);

  useEffect(() => {
    fetchAllPipelinesStates().then(data => setPipelineStates(data));
  }, []);

  useEffect(() => {
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
  }, [state]);

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

  const onSearch = value =>
    dispatch({
      type: TYPES.SEARCH,
      payload: { search: value, current: 1 }
    });

  const columns = [
    {
      ...idColumnFormat(),
      title: "ID"
    },
    {
      title: "State",
      key: "state",
      dataIndex: "state",
      width: 100,
      filterMultiple: false,
      filters: pipelineStates
    },
    {
      ...nameColumnFormat(`${window.TL.BASE_URL}analysis/`),
      title: "Pipeline Name",
      key: "name"
    },
    {
      title: "Type",
      key: "type",
      dataIndex: "type"
    },
    {
      title: "Submitter",
      key: "submitter",
      sorter: true,
      dataIndex: "submitter"
    },
    {
      ...dateColumnFormat(),
      title: "Created Date",
      dataIndex: "createdDate",
      key: "createdDate"
    }
  ];

  return (
    <PageWrapper
      title={"__ANALYSES__"}
      headerExtras={
        <Row gutter={12} style={{ marginRight: 18 }}>
          <Input.Search onSearch={onSearch} />
        </Row>
      }
    >
      <Table
        style={{ margin: "6px 24px 0 24px" }}
        rowKey={record => record.id}
        loading={loading}
        pagination={{ total, pageSize: state.pageSize }}
        columns={columns}
        dataSource={analyses}
        onChange={handleTableChange}
      />
    </PageWrapper>
  );
}

AnalysesTable.propTypes = {};
