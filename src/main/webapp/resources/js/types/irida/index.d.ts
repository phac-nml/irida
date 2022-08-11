export = IRIDA;
export as namespace IRIDA;

declare namespace IRIDA {
  interface IridaBase {
    id: number;
    key: string;
    name: string;
    createdDate: Date;
    modifiedDate: Date;
    identifier: number;
  }

  export type AnalysisState =
    | "NEW"
    | "PREPARING"
    | "PREPARED"
    | "SUBMITTING"
    | "RUNNING"
    | "FINISHED_RUNNING"
    | "COMPLETING"
    | "COMPLETED"
    | "TRANSFERRED"
    | "POST_PROCESSING"
    | "ERROR";

  interface Announcement extends IridaBase {
    title: string;
    message: string;
    priority: boolean;
    createdBy: User;
    users: User[];
  }

  interface GenomeAssembly {
    createdDate: Date;
    file: string;
    identifier: number;
    label: string;
    links: [];
  }

  interface FastQC {
    additionalProperties: Record<string, unknown>[];
    analysisType: Record<string, string>;
    createdDate: Date;
    description: string;
    encoding: string;
    executionManagerAnalysisId: string;
    fastqcVersion: string;
    fileType: string;
    filteredSequences: number;
    gcContent: number;
    identifier: number;
    label: string;
    links: any[];
    maxLength: number;
    minLength: number;
    overrepresentedSequences: OverrepresentedSequences[];
    totalBases: number;
    totalSequences: number;
  }

  interface OverrepresentedSequences {
    sequences: string;
    overrepresentedSequenceCount: number;
    percentage: number;
    possibleSource: string;
    createdDate: Date;
    identifier: number;
  }

  export type ProcessingState =
    | "UNPROCESSED"
    | "QUEUED"
    | "PROCESSING"
    | "FINISHED"
    | "ERROR";

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
    defaultSequencingObject: SequencingObject;
    defaultGenomeAssembly: GenomeAssembly;
    sampleName: string;
  }

  interface SampleAnalyses {
    analysisType: string;
    createdDate: Date;
    id: number;
    name: string;
    state: AnalysisState;
  }

  interface SampleGenomeAssembly {
    fileInfo: {
      createdDate: Date;
      file: string;
      fileName: string;
      fileRevisionNumber: number;
      identifier: number;
      label: string;
      links: [];
    };
    fileType: string;
    firstFileSize: string;
  }

  interface SampleSequencingObject {
    fileInfo: SequencingObject;
    secondFileSize: string;
    firstFileSize: string;
    processingState: ProcessingState;
    fileType: string;
    file: any | undefined; //TODO: (deep - 08/08/22): Flush this out
    qcEntries: any;
  }

  interface SequencingFile {
    createdDate: Date;
    file: string;
    fileName: string;
    identifier: string;
    label: string;
    links: [];
    modifiedDate: Date;
    uploadSha: string;
  }

  interface SequencingObject {
    createdDate: Date;
    fileProcessor: string;
    files: SequencingFile[];
    forwardSequenceFile: SequencingFile;
    identifier: number;
    label: string;
    links: [];
    processingState: ProcessingState;
    reverseSequenceFile: SequencingFile;
    sequenceFile: SequencingFile;
  }

  export type SystemRole =
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
