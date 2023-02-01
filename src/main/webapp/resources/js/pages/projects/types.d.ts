import type { Project, Sample } from "../../types/irida";

declare namespace ProjectTypes {
  type ProjectSample = {
    coverage: unknown; // TODO: (Josh - 12/9/22) Figure this one out
    key: string;
    owner: boolean;
    project: Project;
    qcStatus: string;
    quality: string[];
    sample: Sample;
  };

  type SelectedSample = Pick<Sample, "id" | "key" | "sampleName"> & {
    owner: boolean;
    projectId: number;
  };
}

export = ProjectTypes;
export as namespace ProjectTypes;
