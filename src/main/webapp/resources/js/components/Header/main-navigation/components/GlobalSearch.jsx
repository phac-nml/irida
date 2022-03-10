import { Input, Menu } from "antd";
import React from "react";
import styled from "styled-components";
import { grey6 } from "../../../../styles/colors";
import { primaryColour, theme } from "../../../../utilities/theme-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconSearch } from "../../../icons/Icons";

const SearchMenuItem = styled(Menu.Item)`
  &.ant-menu-item-active {
    background-color: transparent !important;
  }
`;

const SearchForm = styled.form`
  background-color: transparent;
  color: ${theme === "dark" ? "#fff" : "#000"};
  width: 300px;
  margin-right: 15px;

  .ant-input-prefix svg {
    color: ${grey6};
    font-size: 14px;
  }

  input {
    border-bottom: 2px solid ${primaryColour};
    border-radius: 10px;
  }
`;

/**
 * React component to render a global search input to the main navigation.
 * @returns {JSX.Element}
 * @constructor
 */
export function GlobalSearch() {
  return (
    <SearchMenuItem key="global-search">
      <SearchForm method="get" action={setBaseUrl("/search")}>
        <Input
          name="query"
          className="t-global-search"
          prefix={<IconSearch />}
          placeholder={i18n("nav.main.search")}
        />
      </SearchForm>
    </SearchMenuItem>
  );
}
