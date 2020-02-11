import React from "react";
import { Button, Input } from "antd";

function getTextSearchProps(dataIndex) {
  let searchInput;
  return {
    onFilter: (value, record) => {
      // Sometimes the values can be undefined so give it a default value.
      const item = record[dataIndex] || "";
      return item
        .toString()
        .toLowerCase()
        .includes(value.toLowerCase());
    },
    onFilterDropdownVisibleChange: visible => {
      if (visible) {
        setTimeout(() => searchInput.select());
      }
    },
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters
    }) => (
      <div style={{ padding: 8 }}>
        <Input
          ref={node => {
            searchInput = node;
          }}
          className={`t-${dataIndex}-filter`}
          placeholder={`Search ${dataIndex}`}
          value={selectedKeys[0]}
          onChange={e =>
            setSelectedKeys(e.target.value ? [e.target.value] : [])
          }
          onPressEnter={() => confirm()}
          style={{ width: 188, marginBottom: 8, display: "block" }}
        />
        <Button
          type="primary"
          className={`t-${dataIndex}-filter-ok`}
          onClick={() => confirm()}
          size="small"
          style={{ width: 90, marginRight: 8 }}
        >
          {i18n("Filter.search")}
        </Button>
        <Button
          className={`t-${dataIndex}-filter-clear`}
          onClick={() => clearFilters()}
          size="small"
          style={{ width: 90 }}
        >
          {i18n("Filter.reset")}
        </Button>
      </div>
    )
  };
}

export { getTextSearchProps };
