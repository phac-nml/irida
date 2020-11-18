import React from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Input } from "antd";
import { IconSearch } from "../../../icons/Icons";
import { grey3, grey6 } from "../../../../styles/colors";

/**
 * React component to render a global search input to the main navigation.
 * @returns {JSX.Element}
 * @constructor
 */
export function GlobalSearch() {
  return (
    <form
      style={{ display: "inline-block", width: 300 }}
      method="get"
      action={setBaseUrl("/search")}
    >
      <Input
        name="query"
        prefix={<IconSearch style={{ color: grey6 }} />}
        placeholder={i18n("nav.main.search")}
        style={{
          border: "none",
          borderBottom: `1px solid ${grey3}`,
        }}
      />
    </form>
  );
}
