import React from "react";
import { MenuBar } from "../MenuBar";
import { SampleFileUploader } from "./SampleFileUploader";
import { Button } from "antd";
import { IconPlusCircle } from "../icons/Icons";

/**
 * MenuBar instance for the Samples page.  Used to hold buttons for actions on
 * samples.
 *
 * @returns {*}
 * @constructor
 */
export default function SampleMenuBar() {
  return (
    <MenuBar>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <Button href="concatenate">
          <IconPlusCircle />
          {i18n("samples.files.concatenate.btn")}
        </Button>
        <SampleFileUploader />
      </div>
    </MenuBar>
  );
}
