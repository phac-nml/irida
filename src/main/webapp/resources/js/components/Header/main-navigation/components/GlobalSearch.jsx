import React from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Input } from "antd";
import { IconSearch } from "../../../icons/Icons";
import { grey3, grey6, grey7 } from "../../../../styles/colors";
import styled from "styled-components";
import { primaryColour, theme } from "../../../../utilities/style-utilities";

/**
 * React component to render a global search input to the main navigation.
 * @returns {JSX.Element}
 * @constructor
 */
export function GlobalSearch() {
  const darkTheme = styled(Input)`
    border: none;
    background-color: #334454;
    input {
      background-color: transparent;
      color: ${grey3};
    }
  `;

  const lightTheme = styled(Input)`
    border: none;
    border-bottom: 1px solid ${primaryColour};
    border-radius: 0;
  `;

  const ThemedInput = theme === "dark" ? darkTheme : lightTheme;

  return (
    <form
      style={{ display: "inline-block", width: 300 }}
      method="get"
      action={setBaseUrl("/search")}
    >
      <ThemedInput
        name="query"
        prefix={<IconSearch style={{ color: grey6 }} />}
        placeholder={i18n("nav.main.search")}
      />
    </form>
  );
}
