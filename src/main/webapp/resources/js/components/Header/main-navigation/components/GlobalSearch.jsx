import React from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Input } from "antd";
import { IconSearch } from "../../../icons/Icons";
import { grey6 } from "../../../../styles/colors";
import { primaryColour } from "../../../../utilities/theme-utilities";

/**
 * React component to render a global search input to the main navigation.
 * @returns {JSX.Element}
 * @constructor
 */
export function GlobalSearch() {
  return (
    <form
      className="global-search"
      style={{ display: "inline-block", width: 300 }}
      method="get"
      action={setBaseUrl("/search")}
    >
      <Input
        name="query"
        className="t-global-search"
        prefix={<IconSearch style={{ color: grey6 }} />}
        placeholder={i18n("nav.main.search")}
        style={{
          borderBottom: `1px solid ${primaryColour}`,
        }}
      />
    </form>
  );
}
