import React, { Key, useCallback } from "react";
import { Moment } from "moment/moment";
import { FilterDropdownProps } from "antd/lib/table/interface";
import { SearchOutlined } from "@ant-design/icons";
import { Button, DatePicker, Space } from "antd";
import { RangePickerProps } from "antd/es/date-picker";

const { RangePicker } = DatePicker;

/**
 * React component for date range filter for ant design tables.
 */
export default function TableDateRangeFilter({
  filterClassName,
  setSelectedKeys,
  selectedKeys,
  confirm,
  clearFilters,
}: FilterDropdownProps & { filterClassName: string }): JSX.Element {
  const onChange: RangePickerProps["onChange"] = (dates) => {
    const [firstDate, secondDate] = dates as [Moment, Moment];
    const startOf = firstDate.startOf("day");
    const endOf = secondDate.endOf("day");
    const range = [startOf, endOf] as unknown as Key;
    setSelectedKeys([range]);
    confirm({ closeDropdown: false });
  };

  const onClear = () => {
    if (typeof clearFilters === `function`) clearFilters();
    confirm({ closeDropdown: true });
  };

  const onFilter = () => confirm({ closeDropdown: true });

  const onChangeCallback = useCallback(onChange, [confirm, setSelectedKeys]);
  const onClearCallback = useCallback(onClear, [clearFilters, confirm]);
  const onFilterCallback = useCallback(onFilter, [confirm]);

  const values = selectedKeys[0] as unknown as [Moment, Moment];

  return (
    <div style={{ padding: 8 }} className={filterClassName}>
      <div style={{ marginBottom: 8, display: "block" }}>
        <RangePicker value={values} onChange={onChangeCallback} />
      </div>
      <Space>
        <Button
          disabled={selectedKeys.length === 0}
          onClick={onClearCallback}
          size="small"
          style={{ width: 89 }}
          className="t-clear-btn"
        >
          {i18n("Filter.clear")}
        </Button>
        <Button
          type="primary"
          onClick={onFilterCallback}
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
}
