import ExportUploadStateTag from "../../../../components/ncbi/ExportUploadStateTag";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";

export const formatNcbiUploadDetails = (data) => [
  {
    title: i18n("iridaThing.id"),
    desc: data.id,
  },
  {
    title: i18n("project.export.status"),
    desc: <ExportUploadStateTag state={data.state} />,
  },
  {
    title: i18n("project.export.submitter"),
    desc: (
      <a href={setBaseUrl(`/users/${data.submitter.id}`)}>
        {data.submitter.label}
      </a>
    ),
  },
  {
    title: i18n("iridaThing.timestamp"),
    desc: formatInternationalizedDateTime(data.createdDate),
  },
  {
    title: i18n("project.export.bioproject.title"),
    desc: data.bioProject,
  },
  {
    title: i18n("project.export.organization.title"),
    desc: data.organization,
  },
  {
    title: i18n("project.export.namespace.title"),
    desc: data.ncbiNamespace,
  },
  {
    title: i18n("project.export.release_date.title"),
    desc: data.releaseDate
      ? formatInternationalizedDateTime(data.releaseDate)
      : "Not Released",
  },
];
