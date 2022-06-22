import { ExportUploadState } from "./export/ExportUploadState";

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

  interface NcbiBiosample {
    key: string;
    name: string;
    id: string;
    biosample: string;
    library_name: string;
    library_strategy: NcbiStrategy;
    library_source: NcbiSource;
    library_construction_protocol: string;
    instrument_model: NcbiInstrument;
    library_selection: NcbiSelection;
  }

  interface NcbiBioSampleFiles {
    id: number;
    bioSample: string;
    singles: SingleEndSequenceFile[];
    pairs: PairedEndSequenceFile[];
    instrumentModel: NcbiInstrument;
    libraryName: string;
    librarySelection: string;
    librarySource: string;
    libraryStrategy: string;
    libraryConstructionProtocol: string;
    status: ExportUploadState;
    accession: string;
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
    bioSampleFiles: NcbiBioSampleFiles[];
  }

  type NcbiStrategy = string;

  type NcbiSource = string;

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

  type ProjectMinimal = Pick<Project, "id" | "name">;

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
    sequenceFiles: SequenceFile[];
  }

  interface SequenceFile extends IridaBase {
    fileSize: string;
  }

  interface PairedEndSequenceFile extends IridaBase {
    files: SequenceFile[];
  }

  interface SingleEndSequenceFile extends IridaBase {
    file: SequenceFile;
  }

  enum SystemRole {
    ROLE_ANONYMOUS = "ROLE_ANONYMOUS",
    ROLE_ADMIN = "ROLE_ADMIN",
    ROLE_USER = "ROLE_USER",
    ROLE_MANAGER = "ROLE_MANAGER",
    ROLE_SEQUENCER = "ROLE_SEQUENCER",
    ROLE_TECHNICIAN = "ROLE_TECHNICIAN",
  }

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

  type UserMinimal = Pick<User, "name" | "id">;
}
