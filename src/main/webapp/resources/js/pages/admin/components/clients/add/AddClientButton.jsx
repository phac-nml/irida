import React, { useState } from "react";
import { AddNewButton } from "../../../../../components/Buttons/AddNewButton";
import { AddClientModal } from "./AddClientModal";

export function AddClientButton() {
  const [visible, setVisible] = useState(false);

  const onCancel = () => setVisible(false);

  const onComplete = () => {
    setVisible(false);
  };

  return (
    <>
      <AddNewButton
        className={"t-add-client-btn"}
        text={i18n("AddClientButton.add")}
        onClick={() => setVisible(true)}
      />
      <AddClientModal
        visible={visible}
        onCancel={onCancel}
        onComplete={onComplete}
      />
    </>
  );
}
