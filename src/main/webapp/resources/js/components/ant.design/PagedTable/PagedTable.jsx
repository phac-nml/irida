import React, { useContext } from "react";
import { PagedTableContext } from "./PagedTableContext";
import { SPACE_XS } from "../../../styles/spacing";
import { Input, Table } from "antd";
import styled from "styled-components";
import { grey5, grey7 } from "../../../styles/colors";
import { IconSearch } from "../../icons/Icons";

const StyledTable = styled(Table)`
  tr.disabled,
  tr.disabled a {
    color: ${grey7};
  }
`;

/**
 * Table component to standardize paged tables with ant.design.
 * @param search - Whether to display the search box or not.
 * @param {node} buttons any element to include in the button section.
 * @param {array} columns definitions as specified by Ant Design
 * @param {array} props list of remaining Ant Design table properties.
 * @returns {*}
 * @constructor
 */
export function PagedTable({ search = true, buttons, ...props }) {
  const { onSearch, pagedConfig } = useContext(PagedTableContext);
  return (
    <>
      <div
        style={{
          display: "flex",
          marginBottom: SPACE_XS,
        }}
      >
        <div style={{ flex: 1 }}>{buttons}</div>
        {search ? (
          <div>
            <Input
              className="t-search"
              prefix={<IconSearch style={{ color: grey5 }} />}
              onChange={(e) => onSearch(e.target.value)}
            />
          </div>
        ) : null}
      </div>
      <StyledTable
        tableLayout="auto"
        scroll={{ x: "max-content" }}
        {...props}
        {...pagedConfig}
      />
    </>
  );
}
