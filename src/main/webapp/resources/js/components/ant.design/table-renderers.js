import React from "react";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { blue6 } from "../../styles/colors";
import { FilterOutlined } from "@ant-design/icons";

export const idColumnFormat = () => ({
  dataIndex: "id",
  key: "identifier",
  sorter: true,
  width: 120
});

export const nameColumnFormat = ({ url }) => {
  return {
    dataIndex: "name",
    key: "name",
    width: 200,
    sorter: true,
    ellipsis: true,
    filterIcon(filtered) {
      return (
        <FilterOutlined
          style={{ color: filtered ? blue6 : undefined }}
          className="t-name"
        />
      );
    },
    render(name, data) {
      return (
        <a className="t-name" href={`${url}/${data.id}`} title={name}>
          {name}
        </a>
      );
    }
  };
};

export const dateColumnFormat = () => ({
  sorter: true,
  width: 230,
  render: date => formatInternationalizedDateTime(date)
});
