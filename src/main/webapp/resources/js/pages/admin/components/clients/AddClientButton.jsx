import React, { lazy, Suspense, useState } from "react";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";

const AddClientForm = lazy(() => import("./AddClientForm"));

/**
 * Component to add a button which will open a modal to add a client.
 * @returns {*}
 * @constructor
 */
export function AddClientButton() {
  const [visible, setVisible] = useState(false);

  return (
    <>
      <AddNewButton
        className={"t-add-client-btn"}
        onClick={() => setVisible(true)}
        text={i18n("AdminPanel.addClient")}
      />
      {visible ? (
        <Suspense fallback={null}>
          <AddClientForm visible={visible} />
        </Suspense>
      ) : null}
    </>
  );
}
