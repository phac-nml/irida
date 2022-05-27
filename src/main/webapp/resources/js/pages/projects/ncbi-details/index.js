import { LoadingOutlined } from "@ant-design/icons";
import { Card, Col, Row, Skeleton, Typography } from "antd";
import * as React from "react";
import { useParams } from "react-router-dom";
import { BasicList } from "../../../components/lists/index";
import NcbiUploadStates from "../../../components/ncbi/upload-states/index";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";

function LoadingValue({ loading, value }) {
  return loading ? (
    <LoadingOutlined />
  ) : value && value.length ? (
    <Typography.Text>{value}</Typography.Text>
  ) : (
    ""
  );
}

export default function NCBIExportDetails() {
  const { projectId, id } = useParams();

  const [details, setDetails] = React.useState([]);
  const [loading, setLoading] = React.useState(true);

  const formatResponse = (data) => [
    {
      title: i18n("iridaThing.id"),
      desc: data.id,
    },
    {
      title: i18n("project.export.status"),
      desc: <NcbiUploadStates state={data.state} />,
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

  React.useEffect(() => {
    fetch(`/ajax/ncbi/project/${projectId}/details/${id}`)
      .then((response) => response.json())
      .then((response) => {
        console.log({ response });
        setDetails(formatResponse(response));
        setLoading(false);
      });
  }, [id, projectId]);

  return (
    <Row gutter={[16, 16]}>
      <Col
        xxl={{ span: 12, offset: 6 }}
        xl={{ span: 20, offset: 2 }}
        sm={{ span: 22, offset: 1 }}
      >
        <Card>
          <Skeleton active={true} loading={loading}>
            <BasicList dataSource={details} grid={{ gutter: 16, column: 4 }} />
          </Skeleton>
        </Card>
      </Col>
    </Row>
  );
}
