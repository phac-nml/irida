import React from "react";
import { Button, DatePicker, Space } from "antd";
import { IconSearch } from "../../../icons/Icons";
import { FilterDropdownProps } from "antd/lib/table/interface";
import { SearchOutlined } from "@ant-design/icons";

const { RangePicker } = DatePicker;

export default function getDateColumnSearchProps(filterName) {
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
            value={selectedKeys[0]}
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
            icon={<IconSearch />}
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
