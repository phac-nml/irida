/**
 * @file Component responsible for rendering a text filter for an ant.design table column.
 */
import React from "react";
import { Button, Input } from "antd";
import { grey4 } from "../../../styles/colors";
import { SPACE_XS } from "../../../styles/spacing";
import { IconSearch } from "../../icons/Icons";
import { FilterDropdownProps } from "antd/lib/table/interface";

/**
 * This callback type is `setSelectedKeys` and is used by ant.design table to
 * set the current value.
 *
 * @callback setSelectedKeys
 * @param {string[]} - where the only value is the value of the filter.
 */

/**
 * Render a text filter component to an ant.design column
 * @param setSelectedKeys - callback to handle the actual table filtering.
 * @param selectedKeys - the current value of the table filter
 * @param confirm - triggers the table filter to run.
 * @param clearFilters - clears the filters on the column.
 * @returns {*}
 * @constructor
 */
export const TextFilter = ({
  setSelectedKeys,
  selectedKeys,
  confirm,
  clearFilters
}: FilterDropdownProps): JSX.Element => {
  return (
    <div>
      <div style={{ padding: SPACE_XS, borderBottom: `1px solid ${grey4}` }}>
        <Input
          style={{ width: 188, display: "block" }}
          value={selectedKeys[0]}
          onChange={e =>
            setSelectedKeys(e.target.value ? [e.target.value] : [])
          }
          onPressEnter={() => confirm()}
        />
      </div>
      <div style={{ padding: SPACE_XS }}>
        <Button
          onClick={clearFilters}
          size="small"
          style={{ width: 90, marginRight: SPACE_XS }}
        >
          Reset
        </Button>
        <Button
          type="primary"
          onClick={() => confirm()}
          size="small"
          style={{ width: 90 }}
        >
          <IconSearch />
          {i18n("TextFilter.search")}
        </Button>
      </div>
    </div>
  );
};
