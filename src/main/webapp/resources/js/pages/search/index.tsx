import {
  Checkbox,
  Col,
  Input,
  Layout,
  PageHeader,
  Radio,
  Row,
  Space,
  Table,
} from "antd";
import type { RadioChangeEvent } from "antd";
import React, { useEffect, useState } from "react";
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

function SearchLayout() {
  const user = useLoaderData();
  console.log(user);
  const [searchParams, setSearchParams] = useSearchParams();
  const [query, setQuery] = useState<string>(searchParams.get("query") || "");
  const [type, setType] = useState<SearchType>("projects");

  useEffect(async () => {
    await fetch(`/ajax/search/projects`);
  }, []);

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
                key: "1",
                name: "Mike",
                age: 32,
                address: "10 Downing Street",
              },
            ]}
            columns={[
              {
                title: "Name",
                dataIndex: "name",
                key: "name",
              },
              {
                title: "Age",
                dataIndex: "age",
                key: "age",
              },
              {
                title: "Address",
                dataIndex: "address",
                key: "address",
              },
            ]}
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
