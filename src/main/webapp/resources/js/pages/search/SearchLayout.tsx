import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useLoaderData, useSearchParams } from "react-router-dom";
import SearchProjectsTable from "./SearchProjectsTable";
import SearchSamplesTable from "./SearchSamplesTable";
import {
  Badge,
  Input,
  Layout,
  Menu,
  PageHeader,
  Select,
  Space,
  TablePaginationConfig,
  Typography,
} from "antd";
import { FilterValue, SorterResult } from "antd/es/table/interface";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Sample } from "../../types/irida";
import { CurrentUser } from "../../apis/users/user";
import { debounce } from "lodash";
import SearchCount from "./SearchCount";

type SearchType = "projects" | "samples";
type SearchItem = {
  id: number;
  name: string;
  createdDate: number;
  modifiedDate: number;
  organism: string;
};

export type SearchProject = SearchItem & {
  samples: SearchSample[];
};

export type SearchSample = SearchItem & {
  projects: SearchProject[];
};

export type SampleTableType = Pick<
  Sample,
  "name" | "organism" | "projects" | "createdDate" | "modifiedDate"
> & {
  key: "string";
};

export interface TableParams {
  pagination?: TablePaginationConfig;
  sortField?: string;
  sortOrder?: string;
  filters?: Record<string, FilterValue>;
}

const initial_table_params = JSON.stringify({
  pagination: {
    current: 1,
    pageSize: 10,
  },
});

export default function SearchLayout() {
  const user = useLoaderData() as CurrentUser;
  const [searchParams, setSearchParams] = useSearchParams();
  const debouncedSetSearchParams = debounce(async (value) => {
    setSearchParams(value);
  }, 500);
  const [type, setType] = useState<SearchType>("projects");
  const [global, setGlobal] = useState<boolean>(user.admin);

  const [projects, setProjects] = useState<{
    content: SearchProject[];
    total: number;
  }>();
  const [projectsTableParams, setProjectsTableParams] = useState<TableParams>(
    JSON.parse(initial_table_params)
  );
  const debouncedSetProjectTableParams = debounce(async (params) => {
    setProjectsTableParams(params);
  }, 300);

  const [samples, setSamples] = useState<{
    content: SearchSample[];
    total: number;
  }>();
  const [samplesTableParams, setSamplesTableParams] = useState<TableParams>(
    JSON.parse(initial_table_params)
  );
  const debouncedSetSamplesTableParams = debounce(async (params) => {
    setSamplesTableParams(params);
  }, 300);

  const handleProjectsTableChange = (
    pagination: TablePaginationConfig,
    filters: Record<string, FilterValue>,
    sorter: SorterResult<SampleTableType>
  ) => {
    debouncedSetProjectTableParams({
      pagination,
      filters,
      ...sorter,
    });
  };

  const handleSamplesTableChange = (
    pagination: TablePaginationConfig,
    filters: Record<string, FilterValue>,
    sorter: SorterResult<SampleTableType>
  ) => {
    debouncedSetSamplesTableParams({
      pagination,
      filters,
      ...sorter,
    });
  };

  const fetchData = useCallback(() => {
    const fetchSamples = async () =>
      axios.post(setBaseUrl(`/ajax/search/samples`), {
        global,
        pagination: samplesTableParams.pagination,
        order: [
          {
            property: samplesTableParams.columnKey || `sampleName`,
            direction: samplesTableParams.order === "ascend" ? `asc` : `desc`,
          },
        ],
        search: [
          {
            property: `name`,
            value: searchParams.get("query"),
            operation: "MATCH_IN",
          },
        ],
      });

    const fetchProjects = async () =>
      axios.post(setBaseUrl(`/ajax/search/projects`), {
        global,
        pagination: projectsTableParams.pagination,
        order: [
          {
            property: projectsTableParams.columnKey || `name`,
            direction: projectsTableParams.order === "ascend" ? `asc` : `desc`,
          },
        ],
        search: [
          {
            property: `name`,
            value: searchParams.get("query"),
            operation: "MATCH_IN",
          },
        ],
      });

    const promises = [];
    promises.push(fetchProjects());
    promises.push(fetchSamples());
    Promise.all(promises).then(
      ([{ data: projectsData }, { data: samplesData }]) => {
        setProjects(projectsData);
        setSamples(samplesData);
      }
    );
  }, [
    global,
    projectsTableParams.columnKey,
    projectsTableParams.order,
    projectsTableParams.pagination,
    samplesTableParams.columnKey,
    samplesTableParams.order,
    samplesTableParams.pagination,
    searchParams,
  ]);

  useEffect(() => {
    fetchData();
  }, [fetchData, global, searchParams]);

  const menuItems = useMemo(
    () => [
      {
        label: (
          <div
            style={{
              width: `100%`,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <span>PROJECTS</span>
            <SearchCount count={projects?.total || 0} />
          </div>
        ),
        key: "projects",
      },
      {
        label: (
          <div
            style={{
              width: `100%`,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <span>SAMPLES</span>
            <SearchCount count={samples?.total || 0} />
          </div>
        ),
        key: "samples",
      },
    ],
    [projects?.total, samples?.total]
  );

  const searchPrefix = (
    <Select
      style={{ width: 120 }}
      defaultValue="global"
      onChange={(value) => setGlobal(value === "global")}
    >
      <Select.Option value="personal">PERSONAL</Select.Option>
      <Select.Option value="global">GLOBAL</Select.Option>
    </Select>
  );

  return (
    <PageHeader
      title={"SEARCH"}
      style={{
        flexGrow: 1,
        display: `flex`,
        flexDirection: "column",
      }}
    >
      <Layout.Content
        style={{
          backgroundColor: `var(--grey-1)`,
          padding: `var(--padding-md)`,
          flexGrow: 1,
        }}
      >
        <Space size="large" direction="vertical" style={{ width: `100%` }}>
          <Space direction="vertical" size="small" style={{ width: `100%` }}>
            <Typography.Text strong>
              What are you searching for?
            </Typography.Text>
            <Input.Search
              addonBefore={user.admin && searchPrefix}
              size={"large"}
              defaultValue={searchParams.get("query") || ""}
              onChange={(e) =>
                debouncedSetSearchParams({ query: e.target.value })
              }
              allowClear
            />
          </Space>
          <Layout>
            <Layout.Sider>
              <Menu
                mode="inline"
                theme="light"
                style={{
                  height: "100%",
                  borderRight: 0,
                }}
                selectedKeys={[type]}
                onClick={(e) => setType(e.key as SearchType)}
              >
                {menuItems.map((item) => (
                  <Menu.Item key={item.key}>{item.label}</Menu.Item>
                ))}
              </Menu>
            </Layout.Sider>
            <Layout.Content
              style={{
                backgroundColor: `var(--grey-1)`,
                paddingLeft: `var(--padding-md)`,
              }}
            >
              {type === "projects" ? (
                <SearchProjectsTable
                  projects={projects}
                  handleTableChange={handleProjectsTableChange}
                />
              ) : (
                <SearchSamplesTable
                  samples={samples}
                  handleTableChange={handleSamplesTableChange}
                />
              )}
            </Layout.Content>
          </Layout>
        </Space>
      </Layout.Content>
    </PageHeader>
  );
}
