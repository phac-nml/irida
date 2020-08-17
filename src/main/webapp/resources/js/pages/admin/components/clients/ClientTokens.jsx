import React, { useEffect, useState } from "react";
import { revokeClientTokens } from "../../../../apis/clients/clients";
import { Button, notification, Popconfirm, Table } from "antd";
import { SPACE_SM, SPACE_XS } from "../../../../styles/spacing";
import { BasicList } from "../../../../components/lists";

/**
 * Display the current clients tokens.
 * @param {number} id of the client.
 * @returns {*}
 * @constructor
 */
export function ClientTokens({ id }) {
  const fields = [
    {
      title: i18n("client.details.token.active"),
      desc: window.PAGE.activeTokens
    },
    {
      title: i18n("client.details.token.expired"),
      desc: window.PAGE.expiredTokens
    },
  ];

  /**
   * Action to take when revoke tokens button is pressed
   */
  function revokeTokens() {
    revokeClientTokens(id).then(() => {
      notification.success(i18n("client.revoke.success"));
    })
  }

  const RevokeTokens = () => (
    <div>
      <div style={{ marginTop: SPACE_SM }}>
        <Popconfirm
          onConfirm={revokeTokens}
          title={i18n("client.revoke.confirm")}
        >
          <Button type="default">
            {i18n("client.details.token.revoke")}
          </Button>
        </Popconfirm>
      </div>
    </div>
  );

  return (
    <>
      <div style={{ display: "flex", marginBottom: SPACE_XS }}>
        <div style={{ flex: 1 }}>
          <RevokeTokens />
        </div>
      </div>
      <BasicList dataSource={fields} />
    </>
  );
}