import React, { useEffect, useReducer, useState } from "react";
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
import { PageWrapper } from "../../../components/page/PageWrapper";
import { getI18N } from "../../../utilities/i18n-utilties";
import {
  dateColumnFormat,
  idColumnFormat,
  nameColumnFormat
} from "../../../components/ant.design/table-renderers";

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
  SEARCH: "PROJECTS/SEARCH",
  TABLE_CHANGE: "PROJECTS/TABLE_CHANGE"
};

const reducer = (state, action) => {
  switch (action.type) {
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
 * Component to display a
 * @returns {*}
 * @constructor
 */
export function ProjectsTable() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const [total, setTotal] = useState(undefined);
  const [loading, setLoading] = useState(false);
  const [projects, setProjects] = useState(undefined);

  useEffect(() => {
    setLoading(true);
    const params = {
      current: state.current - 1, // Offset since table starts on page 1
      pageSize: state.pageSize,
      sortField: state.field,
      sortDirection: state.order,
      search: state.search
    };
    getPagedProjectsForUser(params).then(data => {
      setProjects(data.projects);
      setTotal(data.total);
      setLoading(false);
    });
  }, [state]);

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
        search: value,
        current: 1
      }
    });

  const columns = [
    {
      ...idColumnFormat(),
      title: getI18N("ProjectsTable_th_id")
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
      ...nameColumnFormat(`${window.TL.BASE_URL}projects/`),
      title: getI18N("ProjectsTable_th_name")
    },
    {
      title: getI18N("ProjectsTable_th_organism"),
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
      title: getI18N("ProjectsTable_th_samples"),
      dataIndex: "samples",
      key: "samples",
      width: 100
    },
    {
      ...dateColumnFormat(),
      title: getI18N("ProjectsTable_th_created_date"),
      dataIndex: "createdDate",
      key: "created"
    },
    {
      ...dateColumnFormat(),
      title: getI18N("ProjectsTable_th_modified_date"),
      dataIndex: "modifiedDate",
      key: "modified",
      defaultSortOrder: "descend"
    }
  ];

  const IS_ADMIN = window.location.href.endsWith("all");
  const exportMenu = (
    <Menu>
      <Menu.Item key="excel">
        <a
          href={`${
            window.TL.BASE_URL
          }projects/ajax/export?dtf=xlsx&admin=${IS_ADMIN}`}
          download={`IRIDA_projects_${new Date().getTime()}`}
        >
          <Icon className="spaced-right__sm" type="file-excel" />
          {getI18N("ProjectsTable_export_excel")}
        </a>
      </Menu.Item>
      <Menu.Item key="csv">
        <a
          href={`${
            window.TL.BASE_URL
          }projects/ajax/export?dtf=csv&admin=${IS_ADMIN}`}
          download={`IRIDA_projects_${new Date().getTime()}`}
        >
          <Icon className="spaced-right__sm" type="file" />
          {getI18N("ProjectsTable_export_csv")}
        </a>
      </Menu.Item>
    </Menu>
  );

  return (
    <PageWrapper
      title={getI18N("ProjectsTable_header")}
      headerExtras={
        <Row gutter={12} style={{ marginRight: 18 }}>
          <Col span={18}>
            <Input.Search onSearch={onSearch} />
          </Col>
          <Col span={6}>
            <Dropdown overlay={exportMenu} key="export">
              <Button>
                {getI18N("ProjectsTable_export")} <Icon type="down" />
              </Button>
            </Dropdown>
          </Col>
        </Row>
      }
    >
      <Table
        style={{ margin: "6px 24px 0 24px" }}
        rowKey={record => record.id}
        loading={loading}
        pagination={{
          total: total,
          pageSize: state.pageSize
        }}
        columns={columns}
        dataSource={projects}
        onChange={handleTableChange}
      />
    </PageWrapper>
  );
}
