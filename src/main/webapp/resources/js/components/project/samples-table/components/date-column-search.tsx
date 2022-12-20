import { SearchOutlined } from "@ant-design/icons";
import { Button, DatePicker, Space } from "antd";
import type { FilterDropdownProps } from "antd/lib/table/interface";
import { Moment } from "moment";
import React from "react";
import { ColumnSearchReturn } from "../../../../types/ant-design";
import { RangePickerDateProps } from "antd/es/date-picker/generatePicker";

const { RangePicker } = DatePicker;

export type DateColumnSearchFn = (filterName: string) => ColumnSearchReturn;

export default function getDateColumnSearchProps(
  filterName: string
): ColumnSearchReturn {
  return {
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }: FilterDropdownProps) => {
      function onChange(dates: RangePickerDateProps<Moment>) {
        setSelectedKeys([[dates[0].startOf("day"), dates[1].endOf("day")]]);
        confirm({ closeDropdown: false });
      }

      function onClear() {
        if (typeof clearFilters === `function`) clearFilters();
        confirm({ closeDropdown: true });
      }

      function onFilter() {
        confirm({ closeDropdown: true });
      }

      return (
        <div style={{ padding: 8 }} className={filterName}>
          <div style={{ marginBottom: 8, display: "block" }}>
            <RangePicker value={selectedKeys[0]} onChange={onChange} />
          </div>
          <Space>
            <Button
              disabled={selectedKeys.length === 0}
              onClick={onClear}
              size="small"
              style={{ width: 89 }}
              className="t-clear-btn"
            >
              {i18n("Filter.clear")}
            </Button>
            <Button
              type="primary"
              onClick={onFilter}
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
