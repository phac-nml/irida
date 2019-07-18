import React from "react";
import { Typography } from "antd";
import { blue6 } from "../../styles/colors";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";

const { Text } = Typography;

export const idColumnFormat = () => ({
  dataIndex: "id",
  key: "identifier",
  sorter: true,
  width: 50
});

export const nameColumnFormat = url => {
  return {
    dataIndex: "name",
    key: "name",
    sorter: true,
    width: 300,
    render: (name, data) => (
      <a href={`${url}/${data.id}`}>
        <Text
          ellipsis
          style={{ width: 270, color: blue6, textDecoration: "underline" }}
        >
          {name}
        </Text>
      </a>
    )
  };
};

export const dateColumnFormat = () => ({
  sorter: true,
  width: 230,
  render: date => formatInternationalizedDateTime(date)
});
