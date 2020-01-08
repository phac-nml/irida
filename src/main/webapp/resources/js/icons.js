import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";
import { grey7 } from "./styles/colors";
import { SPACE_XS } from "./styles/spacing";

export const Spinner = ({ text, ...props }) => (
  <span style={{ color: grey7 }}>
    <FontAwesomeIcon icon={faSpinner} spin {...props} />
    <span style={{ marginLeft: SPACE_XS }}>{text}</span>
  </span>
);
