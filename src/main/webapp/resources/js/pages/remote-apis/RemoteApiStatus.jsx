import React, { useEffect, useState } from "react";
import { Modal, Switch } from "antd";
import { Spinner } from "../../icons";
import { checkConnectionStatus } from "../../apis/remote-api/remote-api";
import styled from "styled-components";
import { setBaseUrl } from "../../utilities/url-utilities";

const StyledModal = styled(Modal)`
  .ant-modal-body {
    padding: 0;
  }
  .ant-body--content {
    padding: 24px;
  }
`;

export function RemoteApiStatus({ api }) {
  const [loading, setLoading] = useState(true);
  const [validToken, setValidToken] = useState(false);
  const [showConnectModal, setShowConnectModal] = useState(false);

  useEffect(() => {
    checkConnectionStatus({ id: api.id }).then(validity => {
      setLoading(false);
      setValidToken(validity);
    });
  });

  function updateConnectionStatus(value, e) {
    window.open(
      setBaseUrl(`remote_api/connect/${api.id}`),
      "WIndow TEst",
      "height=400, width=600, chrome=yes, centerscreen"
    );
  }

  return loading ? (
    <Spinner text={i18n("RemoteApi.checking")} />
  ) : (
    <div>
      <Switch
        checked={validToken}
        checkedChildren={<span>{i18n("RemoteApi.connected")}</span>}
        unCheckedChildren={<span>{i18n("RemoteApi.disconnected")}</span>}
        onClick={updateConnectionStatus}
      />
      {/*<StyledModal*/}
      {/*  title={i18n("remoteapi.connect.title")}*/}
      {/*  visible={showConnectModal}*/}
      {/*  onCancel={() => setShowConnectModal(false)}*/}
      {/*  width={600}*/}
      {/*  footer={null}*/}
      {/*>*/}
      {/*  <Alert message={i18n("remoteapi.connect.info")} banner />*/}
      {/*  <div className="ant-body--content">*/}
      {/*    <iframe*/}
      {/*      height={350}*/}
      {/*      src={setBaseUrl(`remote_api/connect/${api.id}`)}*/}
      {/*      style={{ width: "100%" }}*/}
      {/*      frameborder="0"*/}
      {/*    />*/}
      {/*  </div>*/}
      {/*</StyledModal>*/}
    </div>
  );
}
