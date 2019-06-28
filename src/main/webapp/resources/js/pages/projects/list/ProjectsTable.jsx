import React, { useEffect, useReducer } from "react";
import {
  Button,
  Col,
  Dropdown,
  Icon,
  Input,
  Menu,
  Row,
  Table,
  Typography
} from "antd";
import { getPagedProjectsForUser } from "../../../apis/projects/projects";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { blue6 } from "../../../styles/colors";
import { PageWrapper } from "../../../components/page/PageWrapper";

const { Text } = Typography;

const initialState = {
  loading: true, // true when table fetching data
  search: "", // Search query
  current: 1, // Current page in pagination
  pageSize: 10, // Current table size
  total: undefined, // Total number of elements in the table
  order: "descend", // Sort direction
  field: "modifiedDate" // Sort field
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
      /*
      Case when API is fetching updated data.
       */
      return { ...state, loading: true };
    case TYPES.SET_DATA:
      /*
      Case when API has returned data and needs to set it into the table.
       */
      return {
        ...state,
        data: action.payload.projects,
        loading: false,
        total: action.payload.total
      };
    case TYPES.TABLE_CHANGE:
      /*
      Case when the user has changed page or sort
       */
      return {
        ...state,
        ...action.payload
      };
    case TYPES.SEARCH:
      /*
      Case when the user is filtered the table
       */
      return {
        ...state,
        search: action.payload.search
      };
    default:
      throw new Error(`No action found for type: ${action.type}`);
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

/**
 * Component to display a
 * @returns {*}
 * @constructor
 */
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
      <Menu.Item key="excel">
        <a
          href={`${
            window.TL.BASE_URL
          }projects/ajax/export?dtf=xlsx&admin=${window.location.href.endsWith(
            "all"
          )}`}
          download={`IRIDA_projects_${new Date().getTime()}`}
        >
          <Icon className="spaced-right__sm" type="file-excel" />
          Excel
        </a>
      </Menu.Item>
      <Menu.Item key="csv">
        <a
          href={`${
            window.TL.BASE_URL
          }projects/ajax/export?dtf=csv&admin=${window.location.href.endsWith(
            "all"
          )}`}
          download={`IRIDA_projects_${new Date().getTime()}`}
        >
          <Icon className="spaced-right__sm" type="file" />
          CSV
        </a>
      </Menu.Item>
    </Menu>
  );

  return (
    <PageWrapper
      title={"__PROJECTS__"}
      headerExtras={
        <Row gutter={12} style={{ marginRight: 18 }}>
          <Col span={18}>
            <Input.Search onSearch={onSearch} />
          </Col>
          <Col span={6}>
            <Dropdown overlay={exportMenu} key="export">
              <Button>
                Export <Icon type="down" />
              </Button>
            </Dropdown>
          </Col>
        </Row>
      }
    >
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
    </PageWrapper>
  );
}
