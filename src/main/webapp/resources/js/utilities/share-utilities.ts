import { Project, Sample } from "../types/irida";

export interface SharedStorage {
  projectId: Pick<Project, "id">;
  samples: StoredSample[];
  timestamp: Date;
}

export type StoredSample = {
  id: Pick<Sample, "id">;
  name: Pick<Sample, "name">;
  projectId: Pick<Project, "id">;
  owner: boolean;
};

export function storeSamples({
  samples,
  projectId,
}: {
  samples: StoredSample[];
  projectId: Pick<Project, "id">;
}): void {
  sessionStorage.setItem(
    "share",
    JSON.stringify({
      projectId,
      samples,
      timestamp: Date.now(),
    })
  );
}

export async function getSharedSamples(): Promise<SharedStorage> {
  const stored = sessionStorage.getItem("share");
  if (stored) {
    return Promise.resolve(JSON.parse(stored));
  }
  return Promise.reject("No shared samples");
}
