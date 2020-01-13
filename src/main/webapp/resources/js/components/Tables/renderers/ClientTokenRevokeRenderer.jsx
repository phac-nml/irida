import React from "react";
import PropTypes from "prop-types";
import { Button, Popconfirm } from "antd";
import { revokeClientTokens } from "../../../apis/clients/clients";

/**
 * A component renderer for ag-grid token cells.  Will render the number
 * of tokens, and if there is greater than 1 token, a button will be
 * rendered to revoke those tokens.
 *
 * @param {object} data in the row (about the current client).
 * @param {object} node (row) currently being interacted with.
 * @return {*}
 * @constructor
 */
export function ClientTokenRevokeRenderer({ data, node }) {
  /**
   * Revoke the tokens for the current client described
   * in the current row.
   */
  function revokeTokens() {
    revokeClientTokens(data.id).then(() => {
      node.setDataValue("tokens", 0);
    });
  }

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center"
      }}
    >
      {data.tokens}
      {data.tokens > 0 ? (
        <Popconfirm
          title={"Revoke all tokens for this client?"}
          onConfirm={revokeTokens}
        >
          <Button type="dashed">{i18n("client.details.token.revoke")}</Button>
        </Popconfirm>
      ) : null}
    </div>
  );
}

ClientTokenRevokeRenderer.propTypes = {
  data: PropTypes.shape({
    id: PropTypes.number.isRequired,
    tokens: PropTypes.number.isRequired
  }),
  node: PropTypes.shape({
    setDataValue: PropTypes.func.isRequired
  })
};
