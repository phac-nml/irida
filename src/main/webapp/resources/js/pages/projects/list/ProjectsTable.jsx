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
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { blue6 } from "../../../styles/colors";

const { Text } = Typography;
const { Content } = Layout;

const initialState = {
  loading: true,
  search: "",
  current: 1,
  pageSize: 10,
  total: undefined,
  order: "descend",
  field: "modifiedDate"
};

const TYPES = {
  LOADING: "PROJECTS/LOADING",
  SET_DATA: "PROJECTS/SET_DATA",
  SEARCH: "PROJECTS/SEARCH",
  TABLE_CHANGE: "PROJECTS/TABLE_CHANGE"
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
        total: action.payload.total
      };
    case TYPES.TABLE_CHANGE:
      return {
        ...state,
        ...action.payload
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
  }, [state.current, state.pageSize, state.field, state.order, state.search]);

  const fetch = () => {
    dispatch({ type: TYPES.LOADING });
    const params = {
      current: state.current - 1, // Offset since table starts on page 1
      pageSize: state.pageSize,
      sortField: state.field,
      sortDirection: state.order,
      search: state.search
    };
    getPagedProjectsForUser(params).then(data =>
      dispatch({
        type: TYPES.SET_DATA,
        payload: data
      })
    );
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

  const onSearch = value =>
    dispatch({
      type: TYPES.SEARCH,
      payload: {
        search: value
      }
    });

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
      dataIndex: "name",
      key: "name",
      sorter: true,
      width: 300,
      render: (name, data) => (
        <a href={`${window.TL.BASE_URL}projects/${data.id}`} title={name}>
          <Text
            ellipsis
            style={{ width: 270, color: blue6, textDecoration: "underline" }}
          >
            {name}
          </Text>
        </a>
      )
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
      width: 230,
      render: date => formatInternationalizedDateTime(date)
    },
    {
      title: "Modified",
      dataIndex: "modifiedDate",
      key: "modified",
      sorter: true,
      width: 230,
      defaultSortOrder: "descend",
      render: date => formatInternationalizedDateTime(date)
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
                <Input.Search onSearch={onSearch} />
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
          pagination={{
            total: state.total,
            pageSize: state.pageSize
          }}
          columns={columns}
          dataSource={state.data}
          onChange={handleTableChange}
        />
      </Content>
    </Layout>
  );
}
