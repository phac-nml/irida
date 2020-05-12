import React, { useEffect, useReducer, useRef } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { useNavigate } from "@reach/router";
import {
  getUserGroupDetails,
  updateUserGroupDetails,
} from "../../../apis/users/groups";
import { Button, Popconfirm, Tabs, Typography } from "antd";
import { BasicList } from "../../../components/lists";
import { UserGroupRolesProvider } from "../../../contexts/UserGroupRolesContext";
import UserGroupMembersTable from "./UserGroupMembersTable";
import { WarningAlert } from "../../../components/alerts";
import { SPACE_SM } from "../../../styles/spacing";
import { UserGroupProjectsTable } from "./UserGroupProjectsTable";

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

export default function UserGroupDetailsPage({ id }) {
  const [state, dispatch] = useReducer(reducer, {
    loading: true,
    tab: "members",
  });
  const deleteRef = useRef();

  const navigate = useNavigate();

  useEffect(() => {
    getUserGroupDetails(id).then((response) =>
      dispatch({ type: "load", payload: response })
    );
  }, [id]);

  const updateField = (field, value) => {
    updateUserGroupDetails({ id, field, value }).then(() =>
      dispatch({ type: "update", payload: { [field]: value } })
    );
  };

  const updateTable = () =>
    getUserGroupDetails(id).then((response) =>
      dispatch({ type: "load", payload: response })
    );

  const fields = state.loading
    ? []
    : [
        {
          title: i18n("UserGroupDetailsPage.name"),
          desc: state.canManage ? (
            <Paragraph
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
              {state.description}
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
      <form
        style={{ marginTop: SPACE_SM }}
        ref={deleteRef}
        action={`/groups/${id}/delete`}
        method="POST"
      >
        <Popconfirm
          onConfirm={() => deleteRef.current.submit()}
          title={i18n("UserGroupDetailsPage.delete-confirm")}
        >
          <Button type="primary" danger>
            {i18n("UserGroupDetailsPage.delete-button")}
          </Button>
        </Popconfirm>
      </form>
    </div>
  );

  return (
    <PageWrapper
      title={"User Groups"}
      onBack={() => navigate("/groups", { replace: true })}
    >
      <Tabs
        defaultActiveKey="details"
        tabPosition="left"
        tabBarStyle={{ width: 200 }}
      >
        <TabPane tab={i18n("UserGroupDetailsPage.tab.details")} key="details">
          <Title level={4}>User Group Details</Title>
          <BasicList dataSource={fields} />
        </TabPane>
        <TabPane tab={i18n("UserGroupDetailsPage.tab.projects")} key="project">
          <Title level={4}>User Group Projects</Title>
          <UserGroupProjectsTable groupId={id} />
        </TabPane>
        {state.canManage ? (
          <TabPane tab={i18n("UserGroupDetailsPage.tab.delete")} key="delete">
            <Title level={4}>Delete User Group</Title>
            <DeleteGroup />
          </TabPane>
        ) : null}
      </Tabs>
    </PageWrapper>
  );
}
