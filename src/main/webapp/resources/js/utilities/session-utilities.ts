import { Project, Sample } from "../types/irida";

const SAMPLE_STORE = "samples";

export interface SessionSample {
  projectId: Pick<Project, "id">;
  samples: StoredSample[];
  timestamp: Date;
}

export interface StoredSample {
  id: number;
  name: string;
  projectId: number;
  owner: boolean;
}

export function storeSamples({
  samples,
  projectId,
}: {
  samples: StoredSample[];
  projectId: Pick<Project, "id">;
}): void {
  sessionStorage.setItem(
    SAMPLE_STORE,
    JSON.stringify({
      projectId,
      samples,
      timestamp: Date.now(),
    })
  );
}

export async function getStoredSamples(): Promise<SessionSample> {
  const stored = sessionStorage.getItem(SAMPLE_STORE);
  if (stored) {
    return Promise.resolve(JSON.parse(stored));
  }
  return Promise.reject("No shared samples");
}
