import { SearchOutlined } from "@ant-design/icons";
import { Button, DatePicker, Space } from "antd";
import { RangePickerProps } from "antd/es/date-picker";
import type { FilterDropdownProps } from "antd/lib/table/interface";
import React from "react";
import { ColumnSearchReturn } from "../../../../types/ant-design";
import type {
  HandleClearSearchFn,
  HandleSearchFn,
} from "../hooks/useSamplesTableState";

const { RangePicker } = DatePicker;

export type DateColumnSearchFn = (
  filterName: string,
  handleSearch: HandleSearchFn,
  handleClearSearch: HandleClearSearchFn
) => ColumnSearchReturn;

export default function getDateColumnSearchProps(
  filterName: string,
  handleSearch: HandleSearchFn,
  handleClearSearch: HandleClearSearchFn
): ColumnSearchReturn {
  return {
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }: FilterDropdownProps) => {
      const onChange: RangePickerProps["onChange"] = (dates) => {
        if (dates !== null) {
          setSelectedKeys([[dates[0].startOf("day"), dates[1].endOf("day")]]);
          confirm({ closeDropdown: false });
        } else {
          handleClearSearch(confirm, clearFilters);
        }
      };

      return (
        <div style={{ padding: 8 }} className={filterName}>
          <div style={{ marginBottom: 8, display: "block" }}>
            <RangePicker value={selectedKeys[0]} onChange={onChange} />
          </div>
          <Space>
            <Button
              disabled={selectedKeys.length === 0}
              onClick={() => handleClearSearch(confirm, clearFilters)}
              size="small"
              style={{ width: 89 }}
              className="t-clear-btn"
            >
              {i18n("Filter.clear")}
            </Button>
            <Button
              type="primary"
              onClick={() => handleSearch(confirm, clearFilters)}
              icon={<SearchOutlined />}
              size="small"
              style={{ width: 90 }}
              className="t-search-btn"
            >
              {i18n("Filter.search")}
            </Button>
          </Space>
        </div>
      );
    },
    filterIcon: (filtered) => (
      <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
  };
}
