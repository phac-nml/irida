import React, { useEffect, useState } from "react";
import { Alert, Button } from "antd";
import { checkConnectionStatus } from "../../../../apis/remote-api/remote-api";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconLoading, IconLogin } from "../../../../components/icons/Icons";
import { SPACE_XS } from "../../../../styles/spacing";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { authenticateRemoteClient } from "../../../../apis/oauth/oauth";

/**
 * React component to render the status of a Remote API.
 * If the API is not connected it will present the user a button allowing
 * them to connect.
 * @param {object} api - details about the remote API
 * @param {function} onConnect - what do when the connection is made.
 * @returns {*}
 * @constructor
 */
export function RemoteApiStatus({ api, onConnect = () => {} }) {
  const [loading, setLoading] = useState(true);
  const [expiration, setExpiration] = useState(undefined);

  useEffect(checkApiStatus, []);

  useEffect(() => {
    // Listen for a remote api connection
    window.addEventListener("message", updateRemoteApi, false);
    return () => window.removeEventListener("message", updateRemoteApi);
  }, []);

  useEffect(() => checkApiStatus(), [api.id]);

  function checkApiStatus() {
    setLoading(true);
    checkConnectionStatus({ id: api.id }).then((data) => {
      setLoading(false);
      setExpiration(data);
      data && onConnect();
    });
  }

  function updateRemoteApi(event) {
    if (
      event.origin === window.location.origin &&
      event.data === "remote_api_connect"
    ) {
      checkApiStatus();
    }
  }

  /**
   * This will open a popup window with the Oauth for the Remote API
   */
  function updateConnectionStatus() {
    authenticateRemoteClient(api).then(checkApiStatus);
  }

  return loading ? (
    <span>
      <IconLoading style={{ marginRight: SPACE_XS }} />
      {i18n("RemoteApi.checking")}
    </span>
  ) : (
    <div>
      {expiration ? (
        <Alert
          className="t-remote-status-connected"
          message={i18n(
            "RemoteApi.connected",
            formatInternationalizedDateTime(expiration)
          )}
          type="success"
          showIcon
        />
      ) : (
        <Button
          className="t-remote-status-connect"
          onClick={updateConnectionStatus}
          icon={<IconLogin />}
        >
          {i18n("RemoteApi.disconnected")}
        </Button>
      )}
    </div>
  );
}
