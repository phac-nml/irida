import React, { useEffect, useState } from "react";
import { Alert, Button } from "antd";
import { checkConnectionStatus } from "../../../../apis/remote-api/remote-api";
import { IconLoading } from "../../../../components/icons/Icons";
import { SPACE_XS } from "../../../../styles/spacing";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { authenticateRemoteClient } from "../../../../apis/oauth/oauth";
import { RemoteApi } from "../../../../types/irida";

interface RemoteApiStatusProps {
  api: RemoteApi;
  onConnect: () => void;
}

/**
 * React component to render the status of a Remote API.
 * If the API is not connected it will present the user a button allowing
 * them to connect.
 */
export function RemoteApiStatus({
  api,
  onConnect = () => undefined,
}: RemoteApiStatusProps): JSX.Element {
  const [loading, setLoading] = useState(true);
  const [connecting, setConnecting] = useState(false);
  const [expiration, setExpiration] = useState(undefined);

  const checkApiStatus = React.useCallback(() => {
    setLoading(true);
    checkConnectionStatus({ id: api.id })
      .then((data) => {
        setLoading(false);
        setExpiration(data);
        data && onConnect();
      })
      .finally(() => setLoading(false));
  }, [api.id, onConnect]);

  const updateRemoteApi = React.useCallback(
    (event: MessageEvent) => {
      if (
        event.origin === window.location.origin &&
        event.data === "remote_api_connect"
      ) {
        checkApiStatus();
      }
    },
    [checkApiStatus]
  );

  useEffect(checkApiStatus, [checkApiStatus]);

  useEffect(() => {
    // Listen for a remote api connection
    window.addEventListener("message", updateRemoteApi, false);
    return () => window.removeEventListener("message", updateRemoteApi);
  }, [updateRemoteApi]);

  useEffect(() => checkApiStatus(), [api.id, checkApiStatus]);

  /**
   * This will open a popup window with the Oauth for the Remote API
   */
  function updateConnectionStatus() {
    setConnecting(true);
    authenticateRemoteClient(api)
      .then(checkApiStatus)
      .finally(() => setConnecting(false));
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
          loading={connecting}
          size="small"
          type="link"
        >
          {i18n("RemoteApi.disconnected")}
        </Button>
      )}
    </div>
  );
}
