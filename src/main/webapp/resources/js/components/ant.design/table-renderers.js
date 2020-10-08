import React from "react";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { IconTableFilter } from "../icons/Icons";

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
    className: "t-name-col",
    filterIcon(filtered) {
      return <IconTableFilter className="t-name" filtered={filtered} />;
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

export const dateColumnFormat = ({ className = "" } = {}) => ({
  sorter: true,
  width: 230,
  render: date => (
    <span className={className}>{formatInternationalizedDateTime(date)}</span>
  )
});
