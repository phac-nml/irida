import type { Project, Sample } from "../../types/irida";

export type ProjectSample = {
  coverage: unknown; // TODO: (Josh - 12/9/22) Figure this one out
  key: string;
  owner: boolean;
  project: Project;
  qcStatus: string;
  quality: string[];
  sample: Sample;
};
