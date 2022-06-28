export = IRIDA;
export as namespace IRIDA;

declare namespace IRIDA {
    interface IridaBase {
        id: number;
        key: string;
        name: string;
        createdDate: Date;
        modifiedDate: Date;
    }

    interface Announcement extends IridaBase {
        title: string;
        message: string;
        priority: boolean;
        createdBy: User;
        users: User[];
    }

    interface Project extends IridaBase {
        description: string;
        organism: string;
        genomeSize: number;
        minimumCoverage: number;
        maximumCoverage: number;
        remoteStatus: number;
        syncFrequency: string; // TODO (Josh - 6/7/22): is this a string?
        analysisPriority: string;
        users: User[];
        analysisTemplates: string[]; // TODO (Josh - 6/7/22): What should this be
    }

    interface Sample extends IridaBase {
        description: string;
        organism: string;
        isolate: string;
        strain: string;
        collectedBy: string;
        collectionDate: Date;
        geographicLocationName: string;
        isolationSource: string;
        latitude: string;
        longitude: string;
        projects: Project[];
        sequenceFiles: any[]; // TODO (Josh - 6/7/22): FLush this out
    }

    export type SystemRole =
        "ROLE_ANONYMOUS"
        | "ROLE_ADMIN"
        | "ROLE_USER"
        | "ROLE_MANAGER"
        | "ROLE_SEQUENCER"
        | "ROLE_TECHNICIAN";

    interface User extends IridaBase {
        username: string;
        email: string;
        firstName: string;
        lastName: string;
        phoneNumber: string;
        enabled: boolean;
        systemRole: SystemRole;
        lastLogin: Date;
        locale: string;
        projects: Project[];
        tokens: string[]; // TODO (Josh - 6/7/22): Look into this one
        announcements: Announcement[];
        subscriptions: string[]; // TODO (Josh - 6/7/22): Look into this one as well
    }
}