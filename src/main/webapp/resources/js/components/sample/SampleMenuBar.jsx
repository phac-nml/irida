import React from "react";
import { MenuBar } from "../MenuBar";
import { SampleFileUploader } from "./SampleFileUploader";
import { Button } from "antd";
import { IconPlusCircle } from "../icons/Icons";
import { SPACE_XS } from "../../styles/spacing";

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
      <Button href="concatenate" style={{ marginRight: SPACE_XS }}>
        <IconPlusCircle />
        {i18n("samples.files.concatenate.btn")}
      </Button>
      <SampleFileUploader />
    </MenuBar>
  );
}
