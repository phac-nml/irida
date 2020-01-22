import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFilter } from "@fortawesome/free-solid-svg-icons";
import { blue6 } from "../../../styles/colors";

export const FilterIcon = ({ filtered }) => (
  <div
    style={{
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      height: "100%",
      width: "100%"
    }}
  >
    <FontAwesomeIcon
      icon={faFilter}
      style={{ color: filtered ? blue6 : undefined }}
      className="t-state"
    />
  </div>
);
