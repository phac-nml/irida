import type { GenomeAssembly } from "../../apis/samples/samples";
import type { Restriction } from "../../utilities/restriction-utilities";

declare namespace IRIDA {
  interface BaseModel {
    id: number;
    key: string;
    name: string;
    createdDate: Date;
    modifiedDate: Date;
    identifier: number;
  }

  interface AnalysisSubmission {
    analysisCleanedState: string;
    analysisDescription: string | null;
    analysisState: string;
    automated: boolean;
    createdDate: number;
    emailPipelineResultCompleted: boolean;
    emailPipelineResultError: boolean;
    identifier: number;
    inputParameters: Record<string, string>;
    label: string;
    links: [];
    modifiedDate: number;
    name: string;
    priority: PRIORITY;
    remoteInputDataId: string;
    remoteWorkflowId: string;
    updateSamples: boolean;
    workflowId: string;
  }

  interface Announcement extends BaseModel {
    title: string;
    message: string;
    priority: boolean;
    createdBy: User;
    users: User[];
  }

  export type CurrentUser = {
    admin: boolean;
    firstName: string;
    identifier: number;
    lastName: string;
    username: string;
  };

  export type PRIORITY = "LOW" | "MEDIUM" | "HIGH";

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
    bioSamples: NcbiBioSample[];
  }

  type NcbiStrategy = string;

  type NcbiSource = string;

  interface PairedEndSequenceFile extends SequencingObject {
    files: SequencingObject[];
  }

  interface Project extends BaseModel {
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

  interface Sample extends BaseModel {
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
    defaultSequencingObject: SequencingObject;
    defaultGenomeAssembly: GenomeAssembly;
    sampleName: string;
    label: string;
  }

  interface SequencingObject extends BaseModel {
    fileSize: string;
  }

  interface SingleEndSequenceFile extends SequencingObject {
    file: SequencingObject;
  }

  interface StoredSample {
    id: number;
    name: string;
    owner: boolean;
    projectId: number;
  }

  enum SystemRole {
    ROLE_ADMIN = "ROLE_ADMIN",
    ROLE_USER = "ROLE_USER",
    ROLE_MANAGER = "ROLE_MANAGER",
    ROLE_SEQUENCER = "ROLE_SEQUENCER",
    ROLE_TECHNICIAN = "ROLE_TECHNICIAN",
  }

  interface User extends BaseModel {
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

  interface NcbiBioSampleFiles {
    id: number;
    bioSample: string;
    singles: SingleEndSequenceFile[];
    pairs: PairedEndSequenceFile[];
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

  type SelectedSample = Pick<Sample, "id" | "key" | "sampleName"> & {
    owner: boolean;
    projectId: number;
  };

  interface SequenceFile extends BaseModel {
    fileSize: string;
  }

  interface PairedEndSequenceFile extends BaseModel {
    files: SequenceFile[];
  }

  interface SingleEndSequenceFile extends BaseModel {
    file: SequenceFile;
  }

  type UserMinimal = Pick<User, "name" | "id">;

  export interface MetadataField {
    id?: number;
    fieldKey?: string;
    label: string;
    type?: string;
    restriction: Restriction;
  }
}

export = IRIDA;
export as namespace IRIDA;
