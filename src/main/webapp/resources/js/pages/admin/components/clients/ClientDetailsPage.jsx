import React, { useEffect, useReducer } from "react";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { useNavigate } from "@reach/router";
import {
  getClientDetails,
  removeClient,
  updateClientDetails,
} from "../../../../apis/clients/clients";
import { Button, notification, Popconfirm, Tabs, Typography } from "antd";
import { BasicList } from "../../../../components/lists";
import { WarningAlert } from "../../../../components/alerts";
import { SPACE_SM } from "../../../../styles/spacing";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { ClientTokens } from "./ClientTokens";

const { Paragraph, Title } = Typography;
const { TabPane } = Tabs;

const reducer = (state, action) => {
  switch (action.type) {
    case "load":
      return { ...state, loading: false, ...action.payload };
    case "update":
      return { ...state, ...action.payload };
    case "tab":
      return { ...state, ...action.payload };
    default:
      return { ...state };
  }
};

/**
 * React component to display a page for viewing Client Details.
 * @param {number} id - identifier for the client
 * @returns {*}
 * @constructor
 */
export default function ClientDetailsPage({ id }) {
  const [state, dispatch] = useReducer(reducer, {
    loading: true,
  });

  const navigate = useNavigate();

  useEffect(() => {
    getClientDetails(id).then((response) =>
      dispatch({ type: "load", payload: response })
    );
  }, [id]);

  /**
   * When the value of a field changes, update the server.
   * @param {string} field to be updated
   * @param {*} value to update to.
   */
  const updateField = (field, value) => {
    updateClientDetails({ id, field, value }).then(() =>
      dispatch({ type: "update", payload: { [field]: value } })
    );
  };

  /**
   * Action to take when delete is confirmed
   */
  function deleteClient() {
    removeClient(id)
      .then((message) => {
        navigate(setBaseUrl(`admin/clients`), { replace: true });
        notification.success({ message });
      })
      .catch((message) => {
        notification.error({ message });
      });
  }

  const fields = state.loading
    ? []
    : [
        {
          title: i18n("iridaThing.id"),
          desc: id,
        },
        {
          title: i18n("client.clientid"),
          desc: (
            <Paragraph
              editable={{ onChange: (value) => updateField("clientId", value) }}
            >
              {state.clientDetails.clientId}
            </Paragraph>
          ),
        },
        {
          title: i18n("client.details.clientSecret"),
          desc: state.clientDetails.clientSecret,
        },
        {
          title: i18n("client.grant-types"),
          desc: state.clientDetails.authorizedGrantTypes.join(", "),
        },
        {
          title: i18n("client.registeredRedirectUri"),
          desc: (
            <Paragraph
              editable={{
                onChange: (value) =>
                  updateField("registeredRedirectUri", value),
              }}
            >
              {state.clientDetails.registeredRedirectUri}
            </Paragraph>
          ),
        },
        {
          title: i18n("client.scopes"),
          desc: state.clientDetails.scope.join(", "),
        },
        {
          title: i18n("client.autoScopes"),
          desc: state.clientDetails.autoApprovableScopes,
        },
        {
          title: i18n("client.details.tokenValidity"),
          desc: state.clientDetails.accessTokenValiditySeconds,
        },
        {
          title: i18n("client.details.refreshValidity"),
          desc: state.clientDetails.refreshTokenValiditySeconds,
        },
        {
          title: i18n("iridaThing.timestamp"),
          desc: formatInternationalizedDateTime(
            state.clientDetails.createdDate
          ),
        },
      ];

  const RemoveClient = () => (
    <div>
      <WarningAlert message={i18n("client.remove.warning")} />
      <div style={{ marginTop: SPACE_SM }}>
        <Popconfirm
          onConfirm={deleteClient}
          title={i18n("client.remove.confirm")}
        >
          <Button type="primary" danger>
            {i18n("client.remove.button")}
          </Button>
        </Popconfirm>
      </div>
    </div>
  );

  return (
    <PageWrapper
      title={i18n("AdminPanel.clients")}
      onBack={() => navigate(setBaseUrl("/admin/clients"), { replace: true })}
    >
      <Tabs
        defaultActiveKey="details"
        tabPosition="left"
        tabBarStyle={{ width: 200 }}
      >
        <TabPane tab={i18n("UserGroupDetailsPage.tab.details")} key="details">
          <Title level={4}>{i18n("client.details.title")}</Title>
          <BasicList dataSource={fields} />
        </TabPane>
        <TabPane tab={i18n("client.details.token.title")} key="tokens">
          <Title level={4}>{i18n("client.details.token.title")}</Title>
          <ClientTokens id={id} />
        </TabPane>
        <TabPane tab={i18n("client.remove.button")} key="remove">
          <Title level={4}>{i18n("client.remove.title")}</Title>
          <RemoveClient />
        </TabPane>
      </Tabs>
    </PageWrapper>
  );
}
