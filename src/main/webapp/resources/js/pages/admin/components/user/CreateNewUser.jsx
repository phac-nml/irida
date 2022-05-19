import React from "react";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { Modal } from "antd";
import {
  useVisibility,
  VisibilityProvider,
} from "../../../../contexts/visibility-context";
import CreateNewUserForm from "./CreateNewUserForm";

/**
 * Component to add a button which will open a modal to create a user.
 * @returns {*}
 * @constructor
 */
function CreateNewUserButton() {
  const [visible, setVisibility] = useVisibility();

  return (
    <>
      <AddNewButton
        className="t-add-user-btn"
        onClick={() => setVisibility(true)}
        text={i18n("CreateNewUser.button")}
      />
      <Modal
        title={i18n("CreateNewUser.title")}
        onCancel={() => setVisibility(false)}
        visible={visible}
        width={640}
        footer={null}
        maskClosable={false}
      >
        <CreateNewUserForm />
      </Modal>
    </>
  );
}

export default function CreateNewUser() {
  return (
    <VisibilityProvider>
      <CreateNewUserButton />
    </VisibilityProvider>
  );
}
