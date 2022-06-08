import { ExportUploadState } from "../../../types/irida";

export type ExportTagStates = {
  [state in ExportUploadState]: {
    color?: string;
    text: string;
  };
};
