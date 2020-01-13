import React from "react";
import PropTypes from "prop-types";
import { Tag } from "antd";
import { SPACE_XS } from "../../../styles/spacing";

/**
 * A component to render the grant types given to a specific client.
 *
 * @param {object} data about the current client.
 * @return {*}
 * @constructor
 */
export function ClientGrantsRenderer({ data }) {
  // Default colors for displaying grant types
  const colors = { password: "purple", authorization_code: "volcano" };
  // Comes back as a coma separated text list
  return data.grants.split(",").map(grant => (
    <Tag
      key={grant}
      color={colors[grant] || ""}
      style={{ marginRight: SPACE_XS }}
    >
      {grant}
    </Tag>
  ));
}

ClientGrantsRenderer.prototypes = {
  data: PropTypes.shape({
    grants: PropTypes.string.isRequired
  })
};
