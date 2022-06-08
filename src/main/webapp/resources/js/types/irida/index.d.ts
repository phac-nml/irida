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

  type ExportUploadState =
    | "NEW"
    | "UPLOADING"
    | "UPLOADED"
    | "UPLOAD_ERROR"
    | "created"
    | "failed"
    | "queued"
    | "processing"
    | "processed-ok"
    | "processed-error"
    | "waiting"
    | "submitted"
    | "Submission deleted"
    | "retired"
    | "unknown";

  interface NcbiBioSampleFile {
    id: number;
    bioSample: string;
    files: SingleEndSequenceFile[];
    pairs: SequenceFilePair[];
    instrumentModel: string;
    libraryName: string;
    librarySelection: string;
    librarySource: string;
    libraryStrategy: string;
    libraryConstructionProtocol: string;
    status: ExportUploadState;
    accession: string;
  }

  interface NcbiSubmission {
    id: number;
    project: Pick<Project, "id" | "name">;
    state: ExportUploadState;
    submitter: Pick<User, "id" | "name">;
    createdDate: Date;
    organization: string;
    bioProject: string;
    ncbiNamespace: string;
    releaseDate: Date | null;
    bioSampleFiles: NcbiBioSampleFile[];
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

  interface SequenceFile extends IridaBase {
    // TODO (Josh - 6/8/22): Anything in here?
  }

  interface SequenceFilePair {
    label: string;
    identifier: number;
    files: SequenceFile[];
  }

  interface SingleEndSequenceFile {
    label: string;
    identifier: number;
    file: SequenceFile;
  }

  type SystemRole =
    | "ROLE_ANONYMOUS"
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
