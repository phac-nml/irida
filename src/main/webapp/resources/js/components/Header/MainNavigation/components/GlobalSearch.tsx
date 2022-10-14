import { Input } from "antd";
import React from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import "./GlobalSearch.css";
import { SearchOutlined } from "@ant-design/icons";

/**
 * React component to render a global search input to the main navigation.
 */
export function GlobalSearch(): JSX.Element {
  return (
    <form className="global-search" method="get" action={setBaseUrl("/search")}>
      <Input
        name="query"
        allowClear
        autoComplete="off"
        className="t-global-search"
        prefix={<SearchOutlined />}
        placeholder={i18n("nav.main.search")}
      />
    </form>
  );
}
