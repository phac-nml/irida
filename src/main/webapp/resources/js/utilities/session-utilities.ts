import { StoredSample } from "../types/irida";

const SAMPLE_STORE = "samples";

export interface SessionSample {
  projectId: number;
  samples: StoredSample[];
  timestamp: Date;
}

export function storeSamples({
  samples,
  projectId,
}: {
  samples: StoredSample[];
  projectId: number;
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
