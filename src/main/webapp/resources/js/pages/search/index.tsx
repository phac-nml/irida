import { Checkbox, Input, Layout, PageHeader, Space } from "antd";
import React, { useEffect, useRef } from "react";
import { render } from "react-dom";
import {
  DataBrowserRouter,
  Outlet,
  Route,
  Routes,
  useLoaderData,
  useSearchParams,
} from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import userLoader from "./loaders/user-loader";

function SearchPage() {
  return (
    <Layout style={{ minHeight: `100%` }}>
      <Routes>
        <Route path={setBaseUrl("/search")} element={<SearchLayout />} />
      </Routes>
    </Layout>
  );
}

function SearchLayout() {
  const user = useLoaderData();
  console.log(user);
  const [searchParams, setSearchParams] = useSearchParams();
  const query = useRef<string>(searchParams.get("query"));
  console.log(query);

  return (
    <PageHeader title={"SEARCH"}>
      <Layout.Content style={{ backgroundColor: `var(--grey-1)`, padding: 12 }}>
        <Space direction="vertical" style={{ width: `100%` }} size="small">
          <Input.Search size={"large"} value={query.current} />
          <div>
            <Checkbox>Project</Checkbox>
            <Checkbox>Samples</Checkbox>
            <Checkbox>Search Global</Checkbox>
          </div>
        </Space>
      </Layout.Content>
    </PageHeader>
  );
}

const element = document.querySelector("#root");
render(
  <Layout style={{ minHeight: `100%` }}>
    <DataBrowserRouter>
      <Route
        path={setBaseUrl("/search")}
        element={<SearchLayout />}
        loader={userLoader}
      />
    </DataBrowserRouter>
  </Layout>,
  element
);
