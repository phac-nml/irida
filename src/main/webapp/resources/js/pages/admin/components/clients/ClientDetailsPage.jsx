import React, { useEffect, useReducer, useRef } from "react";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { navigate, useNavigate } from "@reach/router";
import {
  getClientDetails,
  updateClientDetails,
  removeClient
} from "../../../../apis/clients/clients";
import { Button, notification, Popconfirm, Tabs, Typography } from "antd";
import { BasicList } from "../../../../components/lists";
import { WarningAlert } from "../../../../components/alerts";
import { SPACE_SM } from "../../../../styles/spacing";
import { setBaseUrl } from "../../../../utilities/url-utilities";

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
    tab: "members",
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
    // updateClientDetails({ id, field, value }).then(() =>
    //   dispatch({ type: "update", payload: { [field]: value } })
    // );
  };

  /**
   * Action to take when delete is confirmed
   */
  function deleteClient() {
    removeClient(id).then((message) => {
      navigate(`admin/clients`, { replace: true });
      notification.success({ message });
    }).catch((message) => {
      notification.error({ message })
    });
  }

  const fields = state.loading
    ? []
    : [
      // {
      //   title: i18n("iridaThing.id"),
      //   desc: state.getId()
      // },
      {
        title: i18n("client.clientid"),
        desc:
          <Paragraph
            editable={{ onChange: (value) => updateField("clientId", value) }}
          >
            {state.clientId}
          </Paragraph>,
      },
      {
        title: i18n("client.details.clientSecret"),
        desc: state.clientSecret
      },
    ];

  const RemoveClient = () => (
    <div>
      <WarningAlert message={i18n("UserGroupDetailsPage.delete-warning")} />
      <div style={{ marginTop: SPACE_SM }}>
        <Popconfirm
          onConfirm={deleteClient}
          title={i18n("UserGroupDetailsPage.delete-confirm")}
          okButtonProps={{ className: "t-delete-confirm-btn" }}
        >
          <Button className="t-delete-group-btn" type="primary" danger>
            {i18n("UserGroupDetailsPage.delete-button")}
          </Button>
        </Popconfirm>
      </div>
    </div>
  );

  return (
    <PageWrapper
      title={"Clients"}
      onBack={() => navigate(setBaseUrl("/admin/clients"), { replace: true })}
    >
      <Tabs
        defaultActiveKey="details"
        tabPosition="left"
        tabBarStyle={{ width: 200 }}
      >
        <TabPane tab={i18n("Details")} key="details">
          <Title level={4}>{i18n("Client Details")}</Title>
          <BasicList dataSource={fields} />
        </TabPane>
        {/*<TabPane tab={i18n("UserGroupDetailsPage.tab.projects")} key="project">*/}
        {/*  <Title level={4}>*/}
        {/*    {i18n("UserGroupsDetailsPage.title.projects")}*/}
        {/*  </Title>*/}
        {/*</TabPane>*/}
        <TabPane tab={i18n("UserGroupDetailsPage.tab.delete")} key="delete">
          <Title level={4}>
            {i18n("UserGroupsDetailsPage.title.delete")}
          </Title>
          <RemoveClient />
        </TabPane>
      </Tabs>
    </PageWrapper>
  );
}
