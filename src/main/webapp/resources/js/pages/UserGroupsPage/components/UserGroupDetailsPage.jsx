import React, { useContext, useEffect, useReducer } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { useNavigate } from "@reach/router";
import {
  getUserGroupDetails,
  updateUserGroupDetails,
} from "../../../apis/users/groups";
import { Button, Popconfirm, Tabs, Typography } from "antd";
import { BasicList } from "../../../components/lists";
import { UserGroupRolesProvider } from "../../../contexts/UserGroupRolesContext";
import { UserGroupsContext } from "../../../contexts/UserGroupsContext";
import UserGroupMembersTable from "./UserGroupMembersTable";
import { WarningAlert } from "../../../components/alerts";
import { UserGroupProjectsTable } from "./UserGroupProjectsTable";
import { SPACE_SM } from "../../../styles/spacing";

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
 * React component to display a page for viewing User Group Details.
 * @param {number} id - identifier for the user group
 * @param baseUrl - either /admin/groups for admin panel or /groups for main app
 * baseUrl should already be set in parent component
 * @returns {*}
 * @constructor
 */
export default function UserGroupDetailsPage({ id, baseUrl }) {
  const { userGroupsContextDeleteUserGroup } = useContext(UserGroupsContext);

  const [state, dispatch] = useReducer(reducer, {
    loading: true,
    tab: "members",
  });

  const navigate = useNavigate();

  useEffect(() => {
    getUserGroupDetails(id).then((response) =>
      dispatch({ type: "load", payload: response })
    );
  }, [id]);

  /**
   * When the value of either the name or description is changes, update the
   * server.
   * @param {string} field to be updated
   * @param {string} value to update to.
   */
  const updateField = (field, value) => {
    updateUserGroupDetails({ id, field, value }).then(() =>
      dispatch({ type: "update", payload: { [field]: value } })
    );
  };

  /**
   * Update contents of the table
   * @returns {void | Promise<void>}
   */
  const updateTable = () =>
    getUserGroupDetails(id).then((response) =>
      dispatch({ type: "load", payload: response })
    );

  /**
   * Action to take when delete is confirmed
   */
  function deleteUserGroup() {
    userGroupsContextDeleteUserGroup(id, baseUrl);
  }

  const fields = state.loading
    ? []
    : [
        {
          title: i18n("UserGroupDetailsPage.name"),
          desc: state.canManage ? (
            <Paragraph
              className={"t-group-name"}
              editable={{ onChange: (value) => updateField("name", value) }}
            >
              {state.name}
            </Paragraph>
          ) : (
            state.name
          ),
        },
        {
          title: i18n("UserGroupDetailsPage.description"),
          desc: state.canManage ? (
            <Paragraph
              ellipsis={{ rows: 3, expandable: true }}
              editable={{
                onChange: (value) => updateField("description", value),
              }}
            >
              {state.description || ""}
            </Paragraph>
          ) : (
            <Paragraph ellipsis={{ rows: 3, expandable: true }}>
              state.description
            </Paragraph>
          ),
        },
        {
          title: i18n("UserGroupDetailsPage.members"),
          desc: (
            <UserGroupRolesProvider>
              <UserGroupMembersTable
                updateTable={updateTable}
                members={state.members}
                canManage={state.canManage}
                groupId={id}
              />
            </UserGroupRolesProvider>
          ),
        },
      ];

  const DeleteGroup = () => (
    <div>
      <WarningAlert message={i18n("UserGroupDetailsPage.delete-warning")} />
      <div style={{ marginTop: SPACE_SM }}>
        <Popconfirm
          onConfirm={deleteUserGroup}
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
      title={"User Groups"}
      onBack={() => navigate(baseUrl, { replace: true })}
    >
      <Tabs
        defaultActiveKey="details"
        tabPosition="left"
        tabBarStyle={{ width: 200 }}
      >
        <TabPane tab={i18n("UserGroupDetailsPage.tab.details")} key="details">
          <Title level={4}>{i18n("UserGroupsDetailsPage.title.details")}</Title>
          <BasicList dataSource={fields} />
        </TabPane>
        <TabPane tab={i18n("UserGroupDetailsPage.tab.projects")} key="project">
          <Title level={4}>
            {i18n("UserGroupsDetailsPage.title.projects")}
          </Title>
          <UserGroupProjectsTable groupId={id} />
        </TabPane>
        {state.canManage ? (
          <TabPane
            tab={
              <span className="t-tab-delete">
                {i18n("UserGroupDetailsPage.tab.delete")}
              </span>
            }
            key="delete"
          >
            <Title level={4}>
              {i18n("UserGroupsDetailsPage.title.delete")}
            </Title>
            <DeleteGroup />
          </TabPane>
        ) : null}
      </Tabs>
    </PageWrapper>
  );
}
