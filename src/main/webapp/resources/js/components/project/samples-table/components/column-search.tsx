import React from "react";
import type { FilterDropdownProps } from "antd/lib/table/interface";
import { Button, Select, Space } from "antd";
import { IconSearch } from "../../../icons/Icons";
import type { ColumnSearchReturn } from "../../../../types/ant-design";
import TableSearchFilter from "./TableSearchFilter";

export type ColumnSearchFn = (
  dataIndex: string | string[],
  filterName: string,
  placeholder: string
) => ColumnSearchReturn;

export default function getColumnSearchProps(
  dataIndex: string | string[],
  filterName = "",
  placeholder = ""
): ColumnSearchReturn {
  return {
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }: FilterDropdownProps) => {
      function onClear() {
        if (typeof clearFilters === `function`) clearFilters();
        confirm({ closeDropdown: true });
      }

      function onFilter() {
        confirm({ closeDropdown: true });
      }

      return (
        <div style={{ padding: 8 }}>
          <Select
            className={filterName}
            mode="tags"
            placeholder={placeholder}
            value={selectedKeys[0]}
            onChange={(e) => {
              const values = Array.isArray(e) && e.length > 0 ? [e] : e;
              setSelectedKeys(values as string[]);
              confirm({ closeDropdown: false });
            }}
            style={{ marginBottom: 8, display: "block" }}
          />
          <Space>
            <Button
              disabled={
                selectedKeys.length === 0 ||
                (selectedKeys[0] as string).length === 0
              }
              onClick={onClear}
              size="small"
              style={{ width: 89 }}
            >
              {i18n("Filter.clear")}
            </Button>
            <Button
              type="primary"
              onClick={onFilter}
              icon={<IconSearch />}
              size="small"
              style={{ width: 90 }}
            >
              {i18n("Filter.search")}
            </Button>
          </Space>
        </div>
      );
    },
    filterIcon: (filtered: boolean) => (
      <TableSearchFilter filtered={filtered} />
    ),
  };
}
