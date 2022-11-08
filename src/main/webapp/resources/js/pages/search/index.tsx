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
interface TableParams {
  pagination?: TablePaginationConfig;
  sortField?: string;
  sortOrder?: string;
  filters?: Record<string, FilterValue>;
}

function SearchLayout() {
  const user = useLoaderData();
  console.log(user);
  const [searchParams, setSearchParams] = useSearchParams();
  const [query, setQuery] = useState<string>(searchParams.get("query") || "");
  const [type, setType] = useState<SearchType>("projects");

  const [tableParams, setTableParams] = useState<TableParams>({
    pagination: {
      current: 1,
      pageSize: 10,
    },
  });

  const fetchProjects = async () => {
    const response = await fetch(setBaseUrl(`/ajax/search/projects`), {
      method: "POST",
      body: JSON.stringify({
        global: true,
        pagination: tableParams.pagination,
        order: [{ property: "label", direction: `asc` }],
        search: [{ property: `label`, value: query, operation: "MATCH_IN" }],
      }),
    })
      .then((response) => response.json())
      .catch((error) => {
        console.error("Error:", error);
      });
    console.log(response);
  };

  useEffect(() => {
    fetchProjects();
  }, [JSON.stringify(tableParams)]);

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

  const columns = useMemo<ColumnType<SampleTableType>>(
    () => [
      {
        key: `name`,
        dataIndex: "sampleName",
        title: "NAME",
      },
      {
        key: `organism`,
        dataIndex: `organism`,
        title: "ORGANISM",
      },
      {
        key: `projects`,
        dataIndex: `projects`,
        title: `PROJECTS`,
      },
      {
        key: `createdDate`,
        dataIndex: `createdDate`,
        title: `CREATED DATE`,
      },
      {
        key: `modifiedDate`,
        dataIndex: `modifiedDate`,
        title: `MODIFIED DATE`,
      },
    ],
    []
  );

  return (
    <PageHeader
      title={"SEARCH"}
      style={{
        border: `5px solid green`,
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
              value={query}
              onChange={(e) => setQuery(e.target.value)}
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
                {user.admin && <Checkbox>Search Global</Checkbox>}
              </Space>
            </section>
          </Space>
          <Table
            dataSource={[
              {
                key: 1,
                name: "Mike",
                age: 32,
                address: "10 Downing Street",
              },
            ]}
            columns={columns}
            pagination={tableParams.pagination}
            onChange={handleTableChange}
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
      border: `2px solid blue`,
      display: `flex`,
      flexDirection: "column",
    }}
  >
    <RouterProvider router={router} />
  </Layout>,
  element
);
