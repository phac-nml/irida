import React from "react";
import { Button, Icon } from "antd";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { blue6 } from "../../styles/colors";

export const idColumnFormat = () => ({
  dataIndex: "id",
  key: "identifier",
  sorter: true,
  width: 50
});

export const nameColumnFormat = ({ url, width }) => {
  return {
    dataIndex: "name",
    key: "name",
    sorter: true,
    width,
    filterIcon(filtered) {
      return (
        <Icon
          type="filter"
          theme="filled"
          style={{ color: filtered ? blue6 : undefined }}
          className="t-name"
        />
      );
    },
    render(name, data) {
      return (
        <Button
          type="link"
          className="t-name"
          href={`${url}/${data.id}`}
          title={name}
          style={{
            width,
            whiteSpace: "nowrap",
            overflow: "hidden",
            textOverflow: "ellipsis",
            textAlign: "left"
          }}
        >
          <span style={{ width: width - 60 }}>{name}</span>
        </Button>
      );
    }
  };
};

export const dateColumnFormat = () => ({
  sorter: true,
  width: 230,
  render: date => formatInternationalizedDateTime(date)
});
