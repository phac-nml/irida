import React from "react";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { FilterIcon } from "../Tables/fitlers/FilterIcon";

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
    sorter: true,
    ellipsis: true,
    filterIcon(filtered) {
      return <FilterIcon filtered={filtered} />;
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
