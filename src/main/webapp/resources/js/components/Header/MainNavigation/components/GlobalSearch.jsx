import { Input } from "antd";
import React from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconSearch } from "../../../icons/Icons";
import "./GlobalSearch.css";

/**
 * React component to render a global search input to the main navigation.
 * @returns {JSX.Element}
 * @constructor
 */
export function GlobalSearch() {
  return (
    <form className="global-search" method="get" action={setBaseUrl("/search")}>
      <Input
        name="query"
        allowClear
        autoComplete="off"
        className="t-global-search"
        prefix={<IconSearch />}
        placeholder={i18n("nav.main.search")}
      />
    </form>
  );
}
