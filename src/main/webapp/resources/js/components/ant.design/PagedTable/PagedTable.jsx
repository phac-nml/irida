import React, { useContext } from "react";
import { PagedTableContext } from "./PagedTableContext";
import { SPACE_XS } from "../../../styles/spacing";
import { Input, Table } from "antd";

/**
 * Table component to standardize paged tables with ant.design.
 * @param search - Whether to display the search box or not.
 * @param {node} buttons any element to include in the button section.
 * @param {array} columns definitions as specified by Ant Design
 * @param {array} props list of remaining Ant Design table properties.
 * @returns {*}
 * @constructor
 */
export function PagedTable({ search = true, buttons, columns, ...props }) {
  const { onSearch, pagedConfig } = useContext(PagedTableContext);

  return (
    <>
      <div
        style={{
          display: "flex",
          marginBottom: SPACE_XS
        }}
      >
        <div style={{ flex: 1 }}>{buttons}</div>
        {search ? (
          <div>
            <Input.Search
              style={{ width: 250 }}
              onChange={e => onSearch(e.target.value)}
            />
          </div>
        ) : null}
      </div>
      <Table
        {...props}
        {...pagedConfig}
        columns={columns}
        scroll={{ x: "max-content" }}
      />
    </>
  );
}
