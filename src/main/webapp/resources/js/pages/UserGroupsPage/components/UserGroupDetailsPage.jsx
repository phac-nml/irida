import React, { useEffect, useReducer, useRef } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { useNavigate } from "@reach/router";
import {
  getUserGroupDetails,
  updateUserGroupDetails,
} from "../../../apis/users/groups";
import { Button, Popconfirm, Typography } from "antd";
import { BasicList } from "../../../components/lists";
import { UserGroupRolesProvider } from "../../../contexts/UserGroupRolesContext";
import UserGroupMembersTable from "./UserGroupMembersTable";
import { WarningAlert } from "../../../components/alerts";
import { SPACE_SM } from "../../../styles/spacing";

const { Paragraph } = Typography;

const reducer = (state, action) => {
  switch (action.type) {
    case "load":
      return { ...state, loading: false, ...action.payload };
    case "update":
      return { ...state, ...action.payload };
    default:
      return { ...state };
  }
};

export default function UserGroupDetailsPage({ id }) {
  const [state, dispatch] = useReducer(reducer, { loading: true });
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
          title: "Name",
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
          title: "Description",
          desc: state.canManage ? (
            <Paragraph
              editable={{
                onChange: (value) => updateField("description", value),
              }}
            >
              {state.description}
            </Paragraph>
          ) : (
            state.description
          ),
        },
        {
          title: "Members",
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

  if (state.canManage) {
    fields.push({
      title: "Delete User Group",
      desc: (
        <div>
          <WarningAlert
            message={
              "Warning! Deletion of a user group is a permanent action!  Members will no longer have access to the project this group belonged to."
            }
          />
          <form
            style={{ marginTop: SPACE_SM }}
            ref={deleteRef}
            action={`/groups/${id}/delete`}
            method="POST"
          >
            <Popconfirm
              onConfirm={() => deleteRef.current.submit()}
              title={"Delete this group?"}
            >
              <Button danger>Delete</Button>
            </Popconfirm>
          </form>
        </div>
      ),
    });
  }

  return (
    <PageWrapper
      title={"User Groups"}
      onBack={() => navigate("/groups", { replace: true })}
    >
      <BasicList dataSource={fields} />
    </PageWrapper>
  );
}
