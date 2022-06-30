import * as React from "react";
import ExportUploadStateTag from "../ExportUploadStateTag";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import {
  NcbiBioSampleFiles,
  NcbiSubmission,
  PairedEndSequenceFile,
  SingleEndSequenceFile,
} from "../../../types/irida";
import { BasicListItem } from "../../lists/BasicList";

/**
 * Format all details in a NcbiSubmission (without the files) to a form that
 * can be consumed by the BasicList component.
 * @param submission
 */
export const formatNcbiSubmissionDetails = (
  submission: Omit<NcbiSubmission, "bioSampleFiles">
): BasicListItem[] => {
  const releaseDate = submission.releaseDate
    ? formatInternationalizedDateTime(submission.releaseDate)
    : i18n("NcbiExportDetailsView.not-released");

  return [
    {
      title: i18n("iridaThing.id"),
      desc: <span className="t-details-id">{submission.id}</span>,
    },
    {
      title: i18n("project.export.status"),
      desc: <ExportUploadStateTag state={submission.state} />,
    },
    {
      title: i18n("project.export.submitter"),
      desc: <span className="t-submitter">{submission.submitter.name}</span>,
    },
    {
      title: i18n("iridaThing.timestamp"),
      desc: (
        <span className="t-created">
          {formatInternationalizedDateTime(submission.createdDate)}
        </span>
      ),
    },
    {
      title: i18n("project.export.bioproject.title"),
      desc: <span className="t-bioproject">{submission.bioProject}</span>,
    },
    {
      title: i18n("project.export.organization.title"),
      desc: <span className="t-organization">{submission.organization}</span>,
    },
    {
      title: i18n("project.export.namespace.title"),
      desc: <span className="t-namespace">{submission.ncbiNamespace}</span>,
    },
    {
      title: i18n("project.export.release_date.title"),
      desc: <span className="t-release-date">{releaseDate}</span>,
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

/**
 * Format the files from a NcbiSubmission.  The files detail will be formatted into a manner that
 * can be consumed by a BasicList component.
 * @param bioSampleFiles
 */
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
