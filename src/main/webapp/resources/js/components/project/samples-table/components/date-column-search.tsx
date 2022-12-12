import React from "react";
import { Button, DatePicker, Space } from "antd";
import type { FilterDropdownProps } from "antd/lib/table/interface";
import { SearchOutlined } from "@ant-design/icons";
import type {
  HandleClearSearchFn,
  HandleSearchFn,
} from "../hooks/useSamplesTableState";
import { ColumnSearchReturn } from "../../../../types/ant-design";
import * as moment from "moment";

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
    }: FilterDropdownProps) => (
      <div style={{ padding: 8 }} className={filterName}>
        <div style={{ marginBottom: 8, display: "block" }}>
          <RangePicker
            value={selectedKeys[0] as unknown as moment.Moment}
            onChange={(dates) => {
              if (dates !== null) {
                setSelectedKeys([
                  [dates[0].startOf("day"), dates[1].endOf("day")],
                ]);
                confirm({ closeDropdown: false });
              } else {
                handleClearSearch(clearFilters, confirm);
              }
            }}
          />
        </div>
        <Space>
          <Button
            disabled={selectedKeys.length === 0}
            onClick={() => handleClearSearch(clearFilters, confirm)}
            size="small"
            style={{ width: 89 }}
            className="t-clear-btn"
          >
            {i18n("Filter.clear")}
          </Button>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm)}
            icon={<SearchOutlined />}
            size="small"
            style={{ width: 90 }}
            className="t-search-btn"
          >
            {i18n("Filter.search")}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered) => (
      <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
  };
}
