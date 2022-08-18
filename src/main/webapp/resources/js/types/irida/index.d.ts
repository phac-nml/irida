import { GenomeAssembly, SequencingObject } from "../../apis/samples/samples";
import { ExportUploadState } from "./ExportUpoadState";

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
    defaultSequencingObject: SequencingObject;
    defaultGenomeAssembly: GenomeAssembly;
    sampleName: string;
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

  type ProjectMinimal = Pick<Project, "id" | "name">;

  interface SequenceFile extends IridaBase {
    fileSize: string;
  }

  interface PairedEndSequenceFile extends IridaBase {
    files: SequenceFile[];
  }

  interface SingleEndSequenceFile extends IridaBase {
    file: SequenceFile;
  }

  type UserMinimal = Pick<User, "name" | "id">;
}
