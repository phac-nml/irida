import type { RadioChangeEvent, TablePaginationConfig } from "antd";
import type { FilterValue, SorterResult } from "antd/es/table/interface";
import { Checkbox, Input, Layout, PageHeader, Radio, Space, Table } from "antd";
import React, { useEffect, useMemo, useState } from "react";
import { render } from "react-dom";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
  useLoaderData,
  useSearchParams,
} from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import userLoader from "./loaders/user-loader";
import { Sample } from "../../types/irida";
import axios from "axios";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { getPaginationOptions } from "../../utilities/antdesign-table-utilities";
import { ColumnType } from "antd/es/list";
import SearchProjectsTable from "./SearchProjectsTable";

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route
      path={setBaseUrl("/search")}
      element={<SearchLayout />}
      loader={userLoader}
      action={async ({ params, request }) => {}}
    />
  )
);

type SearchType = "projects" | "samples";
type SampleTableType = Pick<
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

function SearchLayout() {
  k;
  const user = useLoaderData();
  const [searchParams, setSearchParams] = useSearchParams();
  const [type, setType] = useState<SearchType>("projects");
  const [global, setGlobal] = useState<boolean>(false);

  const [projects, setProjects] = useState();
  const [projectsTotal, setProjectsTotal] = useState();

  const [tableParams, setTableParams] = useState<TableParams>({
    pagination: {
      current: 1,
      pageSize: 10,
    },
  });

  const handleTableChange = (
    pagination: TablePaginationConfig,
    filters: Record<string, FilterValue>,
    sorter: SorterResult<SampleTableType>
  ) => {
    setTableParams({
      pagination,
      filters,
      ...sorter,
    });
  };
  const fetchProjects = async () => {
    return axios.post(setBaseUrl(`/ajax/search/projects`), {
      global,
      pagination: tableParams.pagination,
      order: [{ property: "name", direction: `asc` }],
      search: [
        {
          property: `name`,
          value: searchParams.get("query"),
          operation: "MATCH_IN",
        },
      ],
    });
  };

  useEffect(() => {
    fetchProjects()
      .then(({ data }) => {
        setProjects(data.content);
        setProjectsTotal(data.total);
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [fetchProjects, global, searchParams]);

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
            <Input.Search
              size={"large"}
              defaultValue={searchParams.get("query") || ""}
              onChange={(e) => setSearchParams({ query: e.target.value })}
              allowClear
            />
            <section>
              <Space>
                <Radio.Group
                  buttonStyle="solid"
                  defaultValue={type}
                  onChange={(e: RadioChangeEvent) => {
                    setType(e.target.value);
                  }}
                >
                  <Radio.Button value="projects">Projects</Radio.Button>
                  <Radio.Button value="samples">Samples</Radio.Button>
                </Radio.Group>
                {user.admin && (
                  <Checkbox onChange={(e) => setGlobal(e.target.checked)}>
                    Search Global
                  </Checkbox>
                )}
              </Space>
            </section>
          </Space>
          <SearchProjectsTable
            projects={projects}
            total={projectsTotal}
            handleTableChange={handleTableChange}
          />
        </Space>
      </Layout.Content>
    </PageHeader>
  );
}

const element = document.querySelector("#root");
render(
  <Layout
    style={{
      minHeight: `100%`,
      display: `flex`,
      flexDirection: "column",
    }}
  >
    <RouterProvider router={router} />
  </Layout>,
  element
);
