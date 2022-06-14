import * as React from "react";
import ExportUploadStateTag from "../../../../components/ncbi/ExportUploadStateTag";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import {
  NcbiBioSampleFiles,
  NcbiSubmission,
  PairedEndSequenceFile,
  SingleEndSequenceFile,
} from "../../../../types/irida";
import { BasicListItem } from "../../../../components/lists/BasicList.types";

export const formatNcbiSubmissionDetails = (
  submission: Omit<NcbiSubmission, "bioSampleFiles">
): BasicListItem[] => {
  const releaseDate = submission.releaseDate
    ? formatInternationalizedDateTime(submission.releaseDate)
    : i18n("NcbiExportDetailsView.not-released")

  return [
    {
      title: i18n("iridaThing.id"),
      desc: submission.id,
    },
    {
      title: i18n("project.export.status"),
      desc: <ExportUploadStateTag state={submission.state} />,
    },
    {
      title: i18n("project.export.submitter"),
      desc: (
        <a href={setBaseUrl(`/users/${submission.submitter.id}`)}>
          {submission.submitter.name}
        </a>
      ),
    },
    {
      title: i18n("iridaThing.timestamp"),
      desc: formatInternationalizedDateTime(submission.createdDate),
    },
    {
      title: i18n("project.export.bioproject.title"),
      desc: submission.bioProject,
    },
    {
      title: i18n("project.export.organization.title"),
      desc: submission.organization,
    },
    {
      title: i18n("project.export.namespace.title"),
      desc: submission.ncbiNamespace,
    },
    {
      title: i18n("project.export.release_date.title"),
      desc: releaseDate,
    },
  ];
};

export interface BioSampleFileDetails {
  key: string;
  details: BasicListItem[];
  files: {
    singles: SingleEndSequenceFile[];
    pairs: PairedEndSequenceFile[];
  };
}

export const formatNcbiBioSampleFiles = (
  bioSampleFiles: NcbiBioSampleFiles[]
): BioSampleFileDetails[] =>
  bioSampleFiles.map((bioSampleFile) => {
    return {
      key: bioSampleFile.bioSample,
      details: [
        {
          title: i18n("project.export.biosample.title"),
          desc: bioSampleFile.bioSample,
        },
        {
          title: i18n("project.export.status"),
          desc: <ExportUploadStateTag state={bioSampleFile.status} />,
        },
        {
          title: i18n("project.export.accession"),
          desc: bioSampleFile.accession,
        },
        {
          title: i18n("project.export.library_name.title"),
          desc: bioSampleFile.libraryName,
        },
        {
          title: i18n("project.export.instrument_model.title"),
          desc: bioSampleFile.instrumentModel,
        },
        {
          title: i18n("project.export.library_strategy.title"),
          desc: bioSampleFile.libraryStrategy,
        },
        {
          title: i18n("project.export.library_source.title"),
          desc: bioSampleFile.librarySource,
        },
        {
          title: i18n("project.export.library_construction_protocol.title"),
          desc: bioSampleFile.libraryConstructionProtocol,
        },
      ],
      files: {
        singles: bioSampleFile.singles,
        pairs: bioSampleFile.pairs,
      },
    };
  });
