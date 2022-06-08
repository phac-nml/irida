import * as React from "react";
import { RouteComponentProps, useParams } from "react-router-dom";
import { getNcbiSubmission } from "../../../../apis/export/ncbi";

type RouteParams = {
  projectId: number;
  id: number;
};

type NcbiExportDetailsParams = RouteComponentProps<RouteParams>;

function NcbiExportDetails(): JSX.Element {
  const { projectId, id } = useParams<NcbiExportDetailsParams>();

  const [details, setDetails] = React.useState([]);
  const [samples, setSamples] = React.useState([]);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    getNcbiSubmission(projectId, id).then((data: NcbiExportDetails) => {
      console.log({ data });
    });
  }, [id, projectId]);

  return <div>foobar</div>;
}

export default NcbiExportDetails;
