import React from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Input } from "antd";
import { IconSearch } from "../../../icons/Icons";
import { grey3, grey4, grey6, grey9 } from "../../../../styles/colors";
import styled from "styled-components";
import { primaryColour, theme } from "../../../../utilities/theme-utilities";

/**
 * React component to render a global search input to the main navigation.
 * @returns {JSX.Element}
 * @constructor
 */
export function GlobalSearch() {
  const inputStyle = styled(Input)`
    border: none !important;
    border-bottom: 2px solid ${primaryColour} !important;
    border-radius: 0 !important;
  `;

  const darkTheme = styled(inputStyle)`
    background-color: ${grey9}!important;
    input {
      background-color: transparent;
      color: ${grey3};
    }
  `;

  const lightTheme = styled(inputStyle)`
    border-bottom: 2px solid ${grey4} !important;
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
