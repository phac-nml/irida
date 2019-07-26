import React from "react";
import moment from "moment";
import { Button, DatePicker, Icon } from "antd";
import { blue6 } from "../../../../../../styles/colors";

const { RangePicker } = DatePicker;

function getDateSearchProps(dataIndex) {
  let dateInput;
  return {
    filterIcon: filtered => (
      <Icon type="search" style={{ color: filtered ? blue6 : undefined }} />
    ),
    onFilter: (dates, record) => {
      const [start, end] = dates;
      const item = record[dataIndex];
      if (!item) return false;
      const date = moment(new Date(item));
      return date.isBetween(start, end);
    },
    onFilterDropdownVisibleChange: visible => {
      if (visible) {
        // setTimeout(() => dateInput.select());
      }
    },
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters
    }) => (
      <div style={{ padding: 8 }}>
        <RangePicker
          // ref={node => {
          //   dateInput = node;
          // }}
          // value={selectedKeys[0]}
          onChange={dates => setSelectedKeys([dates])}
          style={{ width: 250, marginBottom: 8, display: "block" }}
        />
        <Button
          type="primary"
          onClick={() => confirm()}
          icon="search"
          size="small"
          style={{ width: 90, marginRight: 8 }}
        >
          Search
        </Button>
        <Button
          onClick={() => clearFilters()}
          size="small"
          style={{ width: 90 }}
        >
          Reset
        </Button>
      </div>
    )
  };
}

export { getDateSearchProps };
