export = IRIDA;
export as namespace IRIDA;

declare namespace IRIDA {
  interface BaseRecord {
    id: number;
    key: string;
    name: string;
    createdDate: Date;
    modifiedDate: Date;
  }

  interface Announcement extends BaseRecord {
    title: string;
    message: string;
    priority: boolean;
    createdBy: User;
    users: User[];
  }

  export type ExportUploadState =
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
    | "deleted"
    | "retired"
    | "unknown";

  interface NcbiBioSample {
    id: string;
    accession: string;
    bioSample: string;
    libraryName: string;
    libraryStrategy: NcbiStrategy;
    librarySource: NcbiSource;
    libraryConstructionProtocol: string;
    instrumentModel: NcbiInstrument;
    librarySelection: NcbiSelection;
    status: ExportUploadState;
    singles: SingleEndSequenceFile[];
    pairs: PairedEndSequenceFile[];
  }

  type NcbiInstrument = string;

  type NcbiPlatform =
    | "ABI_SOLID"
    | "BGISEQ"
    | "CAPILLARY"
    | "ILLUMINA"
    | "ION_TORRENT"
    | "LS454"
    | "OXFORD_NANOPORE"
    | "PACBIO_SMRT";

  type NcbiSelection = string;

  interface NcbiSubmission {
    id: number;
    project: ProjectMinimal;
    state: ExportUploadState;
    submitter: UserMinimal;
    createdDate: Date;
    organization: string;
    bioProject: string;
    ncbiNamespace: string;
    releaseDate: Date | null;
    bioSampleFiles: NcbiBioSample[];
  }

  type NcbiStrategy = string;

  type NcbiSource = string;

  interface PairedEndSequenceFile extends SequencingObject {
    files: SequencingObject[];
  }

  interface Project extends BaseRecord {
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

  type ProjectMinimal = Pick<Project, "id" | "name">;

  interface Sample extends BaseRecord {
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
    sequenceFiles: SequencingObject[];
  }

  interface SequencingObject extends Omit<BaseRecord, "id"> {
    identifier: number;
    fileSize: string;
  }

  interface SingleEndSequenceFile extends SequencingObject {
    file: SequencingObject;
  }

  enum SystemRole {
    ROLE_ANONYMOUS = "ROLE_ANONYMOUS",
    ROLE_ADMIN = "ROLE_ADMIN",
    ROLE_USER = "ROLE_USER",
    ROLE_MANAGER = "ROLE_MANAGER",
    ROLE_SEQUENCER = "ROLE_SEQUENCER",
    ROLE_TECHNICIAN = "ROLE_TECHNICIAN",
  }

  interface User extends BaseRecord {
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

  type UserMinimal = Pick<User, "name" | "id">;
}
