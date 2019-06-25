import React, { useEffect, useReducer, useRef } from "react";
import { Button, Icon, Input, Table } from "antd";
import Highlighter from "react-highlight-words";
import { getPagedProjectsForUser } from "../../../apis/projects/projects";

const initialState = { loading: true, search: "" };



const TYPES = {
  SET_DATA: "PROJECTS/SET_DATA",
  SEARCH: "PROJECTS/SEARCH"
};

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.SET_DATA:
      return {
        ...state,
        data: action.payload.projects,
        loading: false,
        pagination: { ...state.pagination, total: action.payload.total }
      };
    case TYPES.SEARCH:
      return {
        ...state,
        search: action.payload.search
      };
    default:
      throw new Error();
  }
};

export function ProjectsTable() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const searchRef = useRef(null);

  useEffect(() => {
    fetch();
  }, []);

  const fetch = (
    params = {
      current: 1,
      pageSize: 10,
      sortField: "modifiedDate",
      sortDirection: "ascend"
    }
  ) => {
    getPagedProjectsForUser(params).then(data =>
      dispatch({
        type: TYPES.SET_DATA,
        payload: data
      })
    );
  };

  const handleTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    const { order, field } = sorter;
    const sortDirection = typeof order === "undefined" ? "ascend" : order;
    const sortField = typeof field === "undefined" ? "modifiedDate" : field;
    // Current offset by 1 because the server Page object starts at 0.
    fetch({ current: current - 1, pageSize, sortDirection, sortField });
  };

  const handleSearch = (selectedKeys, confirm) => {
    confirm();
    dispatch({
      type: TYPES.SEARCH,
      payload: {
        search: selectedKeys[0]
      }
    });
  };

  const handleReset = clearFilters => {
    clearFilters();
    dispatch({
      type: TYPES.SEARCH,
      payload: {
        search: ""
      }
    });
  };

  const getColumnSearchProps = dataIndex => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters
    }) => (
      <div style={{ padding: 8 }}>
        <Input
          ref={searchRef}
          placeholder={`Search ${dataIndex}`}
          value={selectedKeys[0]}
          onChange={e =>
            setSelectedKeys(e.target.value ? [e.target.value] : [])
          }
          onPressEnter={() => handleSearch(selectedKeys, confirm)}
          style={{ width: 188, marginBottom: 8, display: "block" }}
        />
        <Button
          type="primary"
          onClick={() => handleSearch(selectedKeys, confirm)}
          icon="search"
          size="small"
          style={{ width: 90, marginRight: 8 }}
        >
          Search
        </Button>
        <Button
          onClick={() => handleReset(clearFilters)}
          size="small"
          style={{ width: 90 }}
        >
          Reset
        </Button>
      </div>
    ),
    filterIcon: filtered => (
      <Icon type="search" style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
    onFilter: (value, record) =>
      record[dataIndex]
        .toString()
        .toLowerCase()
        .includes(value.toLowerCase()),
    onFilterDropdownVisibleChange: visible => {
      if (visible) {
        setTimeout(() => searchRef.select());
      }
    },
    render: text => (
      <Highlighter
        highlightStyle={{ backgroundColor: "#ffc069", padding: 0 }}
        searchWords={[state.search]}
        autoEscape
        textToHighlight={text.toString()}
      />
    )
  });

  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "identifier",
      sorter: true,
      width: 100
    },
    {
      title: "Name",
      dataIndex: "label",
      key: "label",
      sorter: true,
      render: (label, data) => (
        <a href={`${window.TL.BASE_URL}projects/${data.id}`}>
          {label}
          {data.remote ? <Icon type="swap"/> : null}
        </a>
      ),
      ...getColumnSearchProps("label")
    },
    { title: "Organism", dataIndex: "organism", key: "organism", sorter: true },
    {
      title: "Samples",
      dataIndex: "samples",
      key: "samples"
    },
    {
      title: "Created",
      dataIndex: "createdDate",
      key: "created",
      sorter: true,
      render: date => new Date(date).toLocaleString()
    },
    {
      title: "Modified",
      dataIndex: "modifiedDate",
      key: "modified",
      sorter: true,
      render: date => new Date(date).toLocaleString()
    }
  ];

  return (
    <div style={{ width: "100%", height: "100%" }}>
      <Table
        rowKey={record => record.id}
        loading={state.loading}
        pagination={state.pagination}
        columns={columns}
        dataSource={state.data}
        onChange={handleTableChange}
      />
    </div>
  );
}

ProjectsTable.propTypes = {};
