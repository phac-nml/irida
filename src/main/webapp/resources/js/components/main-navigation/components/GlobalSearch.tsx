import { Input } from "antd";
import React from "react";
import { ROUTE_SEARCH } from "../../../data/routes";
import { SearchOutlined } from "@ant-design/icons";

/**
 * React component to render a global search input to the main navigation.
 */
export default function GlobalSearch(): JSX.Element {
  return (
    <form className="global-search" method="get" action={ROUTE_SEARCH}>
      <Input
        name="query"
        autoComplete="off"
        className="t-global-search"
        prefix={<SearchOutlined />}
        placeholder={i18n("nav.main.search")}
      />
    </form>
  );
}
