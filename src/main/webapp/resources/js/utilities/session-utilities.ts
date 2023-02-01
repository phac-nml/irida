import type { SelectedSample } from "../pages/projects/types";

export interface SessionSample {
  projectId: number;
  samples: SelectedSample[];
  timestamp: Date;
}

/**
 * Store samples into sessionStorage
 * @param samples - List of samples to store
 * @param projectId - Current project identifier
 * @param path - path the samples will be used at
 */
export function storeSamples({
  samples,
  projectId,
  path,
}: {
  samples: SelectedSample[];
  projectId: number;
  path: string;
}): void {
  sessionStorage.setItem(
    `${path}-${projectId}`,
    JSON.stringify({
      projectId,
      samples,
      timestamp: Date.now(),
    })
  );
}

/**
 * Retrieve samples stored into local storage
 * @param path - location where the sample will be used
 */
export async function getStoredSamples(path: string): Promise<SessionSample> {
  const [, id] = window.location.href.match(/projects\/(\d+)/) || [];
  const stored = sessionStorage.getItem(`${path}-${id}`);
  if (stored) {
    return Promise.resolve(JSON.parse(stored));
  }
  return Promise.reject(new Error("No samples stored"));
}
