import React from "react";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import {
  useVisibility,
  VisibilityProvider,
} from "../../../../contexts/visibility-context";
import CreateNewUserForm from "./CreateNewUserForm";
import { ScrollableModal } from "../../../../components/ant.design/ScrollableModal";

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
      <ScrollableModal
        title={i18n("CreateNewUser.title")}
        onCancel={() => setVisibility(false)}
        visible={visible}
        maxHeight={window.innerHeight - 250}
        width={640}
        footer={null}
        maskClosable={false}
      >
        <CreateNewUserForm />
      </ScrollableModal>
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
