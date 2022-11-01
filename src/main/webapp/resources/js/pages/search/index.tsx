import { Input, Layout, PageHeader, Space } from "antd";
import React, { useEffect, useRef } from "react";
import { createRoot } from "react-dom/client";
import {
  BrowserRouter,
  Outlet,
  Route,
  Routes,
  useSearchParams,
} from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";

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
  const [searchParams, setSearchParams] = useSearchParams();
  const query = useRef<string>(searchParams.get("query"));

  return (
    <PageHeader title={"SEARCH"}>
      <Layout.Content style={{ backgroundColor: `var(--grey-1)`, padding: 12 }}>
        <Space direction="vertical" style={{ width: `100%` }} size="large">
          <Input.Search size={"large"} value={query.current} />
          <div>JELLO</div>
        </Space>
      </Layout.Content>
    </PageHeader>
  );
}

const element = document.querySelector("#root");
const root = createRoot(element);
root.render(
  <BrowserRouter>
    <SearchPage />
  </BrowserRouter>
);
