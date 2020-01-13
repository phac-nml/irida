import React from "react";
import PropTypes from "prop-types";
import { setBaseUrl } from "../../../utilities/url-utilities";

/**
 * Used by ag-grid to render a Client name to with a link to that client.
 * @param {object} data
 * @return {*}
 * @constructor
 */
export function ClientNameRenderer({ data }) {
  return <a href={setBaseUrl(`clients/${data.id}`)}>{data.name}</a>;
}

ClientNameRenderer.propTypes = {
  data: PropTypes.shape({
    id: PropTypes.number.isRequired,
    name: PropTypes.string.isRequired
  })
};
