import React from "react";
import { Button, Input } from "antd";
import { SearchOutlined } from "@ant-design/icons";

function getTextSearchProps(dataIndex) {
  let searchInput;
  return {
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
          className="t-name-filter"
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
          className="t-name-filter-ok"
          onClick={() => confirm()}
          size="small"
          style={{ width: 90, marginRight: 8 }}
        >
          <SearchOutlined />
          {i18n("form.search")}
        </Button>
        <Button
          className="t-name-filter-clear"
          onClick={() => clearFilters()}
          size="small"
          style={{ width: 90 }}
        >
          {i18n("form.reset")}
        </Button>
      </div>
    )
  };
}

export { getTextSearchProps };
