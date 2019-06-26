import React, { useEffect, useReducer } from "react";
import {
  Button,
  Dropdown,
  Form,
  Icon,
  Input,
  Layout,
  Menu,
  PageHeader,
  Table,
  Typography
} from "antd";
import { getPagedProjectsForUser } from "../../../apis/projects/projects";
import { blue6 } from "../../../styles/colors";

const { Text } = Typography;
const { Content } = Layout;

const initialState = { loading: true, search: "" };

const TYPES = {
  LOADING: "PROJECTS/LOADING",
  SET_DATA: "PROJECTS/SET_DATA",
  SEARCH: "PROJECTS/SEARCH"
};

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.LOADING:
      return { ...state, loading: true };
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

/**
 * Download table in specified format.
 * @param {string} format format of downloaded doc.
 */
function downloadItem({ format = "xlsx" }) {
  const url = `${window.PAGE.urls.export}&dtf=${format}`;
  const anchor = document.createElement("a");
  anchor.style.display = "none";
  anchor.href = url;
  document.body.appendChild(anchor);
  anchor.click();
  document.body.removeChild(anchor);
}

export function ProjectsTable() {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    fetch();
  }, []);

  const fetch = (
    params = {
      current: 0,
      pageSize: 10,
      sortField: "modifiedDate",
      sortDirection: "descend"
    }
  ) => {
    dispatch({ type: TYPES.LOADING });
    params.search = state.search.label || "";
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

  const handleSearch = (dataIndex, selectedKeys, confirm) => {
    confirm();
    dispatch({
      type: TYPES.SEARCH,
      payload: {
        search: { [dataIndex]: selectedKeys[0] }
      }
    });
    fetch();
  };

  const handleReset = (dataIndex, clearFilters) => {
    clearFilters();
    dispatch({
      type: TYPES.SEARCH,
      payload: {
        search: { [dataIndex]: "" }
      }
    });
    fetch();
  };

  const getColumnSearchProps = dataIndex => {
    const SearchInput = React.forwardRef((props, ref) => (
      <Input
        {...props}
        ref={ref}
        style={{ width: 188, marginBottom: 8, display: "block" }}
      />
    ));
    const searchRef = React.createRef();
    return {
      filterDropdown: ({
        setSelectedKeys,
        selectedKeys,
        confirm,
        clearFilters
      }) => (
        <div style={{ padding: 8 }}>
          <SearchInput
            ref={searchRef}
            placeholder={`Search ${dataIndex}`}
            value={selectedKeys[0]}
            onChange={e =>
              setSelectedKeys(e.target.value ? [e.target.value] : [])
            }
            onPressEnter={() => handleSearch(dataIndex, selectedKeys, confirm)}
          />
          <Button
            type="primary"
            onClick={() => handleSearch(dataIndex, selectedKeys, confirm)}
            icon="search"
            size="small"
            style={{ width: 90, marginRight: 8 }}
          >
            Search
          </Button>
          <Button
            onClick={() => handleReset(dataIndex, clearFilters)}
            size="small"
            style={{ width: 90 }}
          >
            Reset
          </Button>
        </div>
      ),
      filterIcon: filtered => (
        <Icon
          type="search"
          style={{ color: filtered ? "#1890ff" : undefined }}
        />
      ),
      onFilter: (value, record) =>
        record[dataIndex]
          .toString()
          .toLowerCase()
          .includes(value.toLowerCase()),
      onFilterDropdownVisibleChange: visible => {
        if (visible) {
          setTimeout(() => searchRef.current.select());
        }
      },
      render: (text, data) => (
        <a href={`${window.TL.BASE_URL}projects/${data.id}`}>
          <Text
            ellipsis={true}
            style={{
              width: 250,
              display: "inline-block",
              color: blue6,
              textDecoration: "underline"
            }}
          >
            {text}
          </Text>
        </a>
      )
    };
  };

  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "identifier",
      sorter: true,
      width: 50
    },
    {
      title: "",
      dataIndex: "remote",
      key: "remote",
      width: 30,
      render: remote =>
        remote ? (
          <Icon type="swap" title="Remote Project" style={{ cursor: "help" }} />
        ) : null
    },
    {
      title: "Name",
      dataIndex: "label",
      key: "label",
      sorter: true,
      width: 300,
      ...getColumnSearchProps("label")
    },
    {
      title: "Organism",
      dataIndex: "organism",
      key: "organism",
      sorter: true,
      width: 150,
      render: text => (
        <Text style={{ width: 135 }} ellipsis={true} title={text}>
          {text}
        </Text>
      )
    },
    {
      title: "Samples",
      dataIndex: "samples",
      key: "samples",
      width: 100
    },
    {
      title: "Created",
      dataIndex: "createdDate",
      key: "created",
      sorter: true,
      width: 240,
      render: date => new Date(date).toLocaleString()
    },
    {
      title: "Modified",
      dataIndex: "modifiedDate",
      key: "modified",
      sorter: true,
      width: 240,
      render: date => new Date(date).toLocaleString()
    }
  ];

  const exportMenu = (
    <Menu>
      <Menu.Item key="excel" onClick={() => downloadItem({ format: "xlsx" })}>
        <Icon type="file-excel" />
        Excel
      </Menu.Item>
      <Menu.Item key="csv" onClick={() => downloadItem({ format: "csv" })}>
        <Icon type="file" />
        CSV
      </Menu.Item>
    </Menu>
  );

  return (
    <Layout style={{ padding: 24, height: "100%", minHeight: "100%" }}>
      <Content style={{ backgroundColor: "#ffffff", margin: 0 }}>
        <PageHeader
          title="Projects"
          extra={
            <Form layout="inline">
              <Form.Item>
                <Input.Search  />
              </Form.Item>
              <Form.Item>
                <Dropdown overlay={exportMenu} key="export">
                  <Button>
                    Export <Icon type="down" />
                  </Button>
                </Dropdown>
              </Form.Item>
            </Form>
          }
        />
        <Table
          style={{ margin: "6px 24px 0 24px" }}
          rowKey={record => record.id}
          loading={state.loading}
          pagination={state.pagination}
          columns={columns}
          dataSource={state.data}
          onChange={handleTableChange}
        />
      </Content>
    </Layout>
  );
}

ProjectsTable.propTypes = {};
