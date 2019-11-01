import React from "react";
import { Button, Input } from "antd";
import { grey4 } from "../../../styles/colors";
import { SPACE_XS } from "../../../styles/spacing";

export const TextFilter = ({
  setSelectedKeys,
  selectedKeys,
  confirm,
  clearFilters
}) => {
  return (
    <div>
      <div style={{ padding: SPACE_XS, borderBottom: `1px solid ${grey4}` }}>
        <Input
          style={{ width: 188, display: "block" }}
          value={selectedKeys[0]}
          onChange={e =>
            setSelectedKeys(e.target.value ? [e.target.value] : [])
          }
          onPressEnter={confirm}
        />
      </div>
      <div style={{ padding: SPACE_XS }}>
        <Button
          onClick={clearFilters}
          size="small"
          style={{ width: 90, marginRight: SPACE_XS }}
        >
          Reset
        </Button>
        <Button
          type="primary"
          onClick={confirm}
          icon="search"
          size="small"
          style={{ width: 90 }}
        >
          Search
        </Button>
      </div>
    </div>
  );
};
