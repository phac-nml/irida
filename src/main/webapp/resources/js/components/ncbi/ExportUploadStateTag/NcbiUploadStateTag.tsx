import { Tag } from "antd";
import React from "react";
import { TagColor } from "../../../types/ant-design/TagColor";
import { ExportUploadState } from "../../../types/irida/export/ExportUploadState";
import tagStates from "./ExportTagStates";

export type ExportTagStates = {
  [state in ExportUploadState]: {
    color?: TagColor;
    text: string;
  };
};

/**
 * React component to render an NCBI Upload State in a tag with an appropriate
 * colour.  Also provides internationalization for the state.
 * @param state
 */
const ExportUploadStateTag = ({
  state,
}: {
  state: ExportUploadState;
}): JSX.Element => {
  return tagStates[state] ? (
    <Tag className="t-upload-status" color={tagStates[state].color}>
      {tagStates[state].text}
    </Tag>
  ) : (
    <Tag className="t-upload-status" color={TagColor.ORANGE}>
      {i18n("ExportUploadState.unknown")}
    </Tag>
  );
};

export default ExportUploadStateTag;
