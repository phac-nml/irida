import * as React from "react";
import {
  NcbiBioSample,
  NcbiSubmission,
  PairedEndSequenceFile,
  SingleEndSequenceFile,
} from "../../../types/irida";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { BasicListItem } from "../../lists/BasicList";
import ExportUploadStateTag from "../ExportUploadStateTag";

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
      title: i18n("NcbiSubmission.id"),
      desc: <span className="t-details-id">{submission.id}</span>,
    },
    {
      title: i18n("NcbiSubmission.state"),
      desc: <ExportUploadStateTag state={submission.state} />,
    },
    {
      title: i18n("NcbiSubmission.submitter"),
      desc: <span className="t-submitter">{submission.submitter.name}</span>,
    },
    {
      title: i18n("NcbiSubmission.createdDate"),
      desc: (
        <span className="t-created">
          {formatInternationalizedDateTime(submission.createdDate)}
        </span>
      ),
    },
    {
      title: i18n("NcbiSubmission.bioProject"),
      desc: <span className="t-bioproject">{submission.bioProject}</span>,
    },
    {
      title: i18n("NcbiSubmission.organization"),
      desc: <span className="t-organization">{submission.organization}</span>,
    },
    {
      title: i18n("NcbiSubmission.ncbiNamespace"),
      desc: <span className="t-namespace">{submission.ncbiNamespace}</span>,
    },
    {
      title: i18n("NcbiSubmission.releaseDate"),
      desc: <span className="t-release-date">{releaseDate}</span>,
    },
  ];
};

export interface BioSampleDetails {
  key: string;
  details: BasicListItem[];
  files: {
    singles: SingleEndSequenceFile[];
    pairs: PairedEndSequenceFile[];
  };
}

/**
 * Format the files from a NcbiSubmission.
 * The file detail will be formatted into a manner that
 * can be consumed by a BasicList component.
 * @param bioSamples
 */
export const formatNcbiBioSampleFiles = (
  bioSamples: NcbiBioSample[]
): BioSampleDetails[] =>
  bioSamples.map((bioSample) => ({
    key: bioSample.id,
    details: [
      {
        title: i18n("NcbiBioSample.bioSample"),
        desc: bioSample.bioSample,
      },
      {
        title: i18n("NcbiBioSample.status"),
        desc: <ExportUploadStateTag state={bioSample.status} />,
      },
      {
        title: i18n("NcbiBioSample.accession"),
        desc: bioSample.accession,
      },
      {
        title: i18n("NcbiBioSample.libraryName"),
        desc: bioSample.libraryName,
      },
      {
        title: i18n("NcbiBioSample.instrumentModel"),
        desc: bioSample.instrumentModel,
      },
      {
        title: i18n("NcbiBioSample.libraryStrategy"),
        desc: bioSample.libraryStrategy,
      },
      {
        title: i18n("NcbiBioSample.librarySource"),
        desc: bioSample.librarySource,
      },
      {
        title: i18n("NcbiBioSample.libraryConstructionProtocol"),
        desc: bioSample.libraryConstructionProtocol,
      },
    ],
    files: {
      singles: bioSample.singles,
      pairs: bioSample.pairs,
    },
  }));
