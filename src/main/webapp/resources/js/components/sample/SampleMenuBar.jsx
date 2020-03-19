import React from "react";
import { MenuBar } from "../MenuBar";
import { SampleSequenceFileUploader } from "./SampleSequenceFileUploader";

/**
 * MenuBar instance for the Samples page.  Used to hold buttons for actions on
 * samples.
 * @returns {*}
 * @constructor
 */
export function SampleMenuBar() {
  return (
    <MenuBar>
      <div style={{ display: "flex", flexDirection: "row-reverse" }}>
        <SampleSequenceFileUploader />
      </div>
    </MenuBar>
  );
}
