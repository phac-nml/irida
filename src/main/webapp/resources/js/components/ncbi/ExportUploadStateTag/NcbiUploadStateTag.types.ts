import {ExportUploadState} from "../../../types/irida/ExportUpoadState";
import {TagColor} from "../../ant.design/ant.types";

export type ExportTagStates = {
  [state in ExportUploadState]: {
    color: TagColor;
    text: string;
  };
};
