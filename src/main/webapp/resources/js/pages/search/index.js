import { Layout, PageHeader } from "antd";
import React from "react";
import { createRoot } from "react-dom/client";

function SearchPage() {
  return (
    <Layout style={{ minHeight: `100%` }}>
      <PageHeader title={"SEARCH"}>
        <Layout.Content style={{ backgroundColor: `var(--grey-1)` }}>
          AWESOME SEARCH HERE
        </Layout.Content>
      </PageHeader>
    </Layout>
  );
}

const element = document.querySelector("#root");
const root = createRoot(element);
root.render(<SearchPage />);
