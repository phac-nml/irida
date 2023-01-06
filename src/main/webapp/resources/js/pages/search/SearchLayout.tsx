import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useLoaderData, useSearchParams } from "react-router-dom";
import SearchProjectsTable from "./SearchProjectsTable";
import SearchSamplesTable from "./SearchSamplesTable";
import {
  Input,
  Layout,
  Menu,
  PageHeader,
  Select,
  Space,
  TablePaginationConfig,
  TableProps,
  Typography,
} from "antd";
import { FilterValue } from "antd/es/table/interface";
import type { CurrentUser, Sample } from "../../types/irida";
import { debounce } from "lodash";
import SearchCount from "./SearchCount";
import {
  fetchSearchProjects,
  fetchSearchSamples,
  SearchParams,
} from "../../apis/search/search";
import styled from "styled-components";

const Label = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

type SearchType = "projects" | "samples";
type SearchItem = {
  id: number;
  name: string;
  createdDate: string;
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
  columnKey?: string;
  order: "ascend" | "descend";
}

const initial_table_params = JSON.stringify({
  pagination: {
    current: 1,
    pageSize: 10,
  },
});

/**
 * React component to layout and handle events for the global search
 * @constructor
 */
export default function SearchLayout() {
  const user = useLoaderData() as CurrentUser;
  const [searchParams, setSearchParams] = useSearchParams();
  const debouncedSetSearchParams = debounce(async (value) => {
    setSearchParams(value);
  }, 500);
  const [type, setType] = useState<SearchType>("projects");
  const [global, setGlobal] = useState<boolean>(user.isAdmin);

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

  const handleProjectsTableChange: TableProps<SearchProject>["onChange"] = (
    pagination,
    filters,
    sorter
  ): void => {
    debouncedSetProjectTableParams({
      pagination,
      filters,
      ...sorter,
    });
  };

  const handleSamplesTableChange: TableProps<SearchSample>["onChange"] = (
    pagination,
    filters,
    sorter
  ): void => {
    debouncedSetSamplesTableParams({
      pagination,
      filters,
      ...sorter,
    });
  };

  const fetchData = useCallback(() => {
    const sampleParams: SearchParams = {
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
          value: searchParams.get("query") || "",
          operation: "MATCH_IN",
        },
      ],
    };

    const projectParams: SearchParams = {
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
          value: searchParams.get("query") || "",
          operation: "MATCH_IN",
        },
      ],
    };

    const promises = [];
    promises.push(fetchSearchProjects(projectParams));
    promises.push(fetchSearchSamples(sampleParams));
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
          <Label>
            {i18n("SearchLayout.projects").toUpperCase()}
            <SearchCount count={projects?.total} />
          </Label>
        ),
        key: "projects",
      },
      {
        label: (
          <Label>
            {i18n("SearchLayout.samples").toUpperCase()}
            <SearchCount count={samples?.total} />
          </Label>
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
      className="t-admin-search-type"
    >
      <Select.Option value="global">
        {i18n("SearchLayout.admin.global")}
      </Select.Option>
      <Select.Option value="personal">
        {i18n("SearchLayout.admin.personal")}
      </Select.Option>
    </Select>
  );

  return (
    <PageHeader
      title={i18n("search.title")}
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
              {i18n("SearchLayout.tooltip")}
            </Typography.Text>
            <Input.Search
              addonBefore={user.isAdmin && searchPrefix}
              size={"large"}
              className="t-search-input"
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
                className="t-search-nav"
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
