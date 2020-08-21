import React from "react";
import { AddNewButton } from "../../../../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../../../../utilities/url-utilities";

export function AddClientButton() {
  return (
    <AddNewButton
      className={"t-add-client-btn"}
      href={setBaseUrl(`clients/create`)}
      text={i18n("AddClientButton.add")}
    />
  );
}
