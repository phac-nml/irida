import {Project, Sample} from "../types/irida";

export interface SharedStorage  {
    projectId: Pick<Project, "id">;
    samples: {
        id: Pick<Sample, "id">;
        name: Pick<Sample, "name">;
        owner: boolean;
        projectId: Pick<Project, "id">
    }[],
    timestamp: Date;
}

export function storeSamples(samples: SharedStorage):void {
    sessionStorage.setItem("samples", JSON.stringify(samples));
}

export async function getSharedSamples(): Promise<SharedStorage> {
    const stored = sessionStorage.getItem('share');
    if (stored) {
        return Promise.resolve(JSON.parse(stored));
    }
    return  Promise.reject("No shared samples");
}