import * as React from "react";
import ExportUploadStateTag from "../../../../components/ncbi/ExportUploadStateTag";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { NcbiSubmission } from "../../../../types/irida";
import type { NcbiSubmissionDetail } from "./NcbiExportDetails.types";

export const formatNcbiUploadDetails = (
  submission: Omit<NcbiSubmission, "bioSampleFiles">
): NcbiSubmissionDetail[] => {
  const releaseDate = submission.releaseDate
    ? formatInternationalizedDateTime(submission.releaseDate)
    : "Not Released"; // TODO (Josh - 6/8/22): i18n

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
