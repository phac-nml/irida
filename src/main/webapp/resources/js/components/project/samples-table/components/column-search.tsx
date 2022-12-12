import React from "react";
import type { FilterDropdownProps } from "antd/lib/table/interface";
import { Button, Select, Space } from "antd";
import { IconSearch } from "../../../icons/Icons";
import { SearchOutlined } from "@ant-design/icons";
import type {
  HandleClearSearchFn,
  HandleSearchFn,
} from "../hooks/useSamplesTableState";
import type { ColumnSearchReturn } from "../../../../types/ant-design";

export type ColumnSearchFn = (
  dataIndex: string | string[],
  handleSearch: HandleSearchFn,
  handleClearSearch: HandleClearSearchFn,
  filterName: string,
  placeholder: string
) => ColumnSearchReturn;

export default function getColumnSearchProps(
  dataIndex: string | string[],
  handleSearch: HandleSearchFn,
  handleClearSearch: HandleClearSearchFn,
  filterName = "",
  placeholder = ""
): ColumnSearchReturn {
  return {
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }: FilterDropdownProps) => (
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
            onClick={() => handleClearSearch(confirm, clearFilters)}
            size="small"
            style={{ width: 89 }}
          >
            {i18n("Filter.clear")}
          </Button>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm)}
            icon={<IconSearch />}
            size="small"
            style={{ width: 90 }}
          >
            {i18n("Filter.search")}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered: boolean) => (
      <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
  };
}
