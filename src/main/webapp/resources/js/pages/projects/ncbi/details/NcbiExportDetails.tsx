import * as React from "react";
import { useParams } from "react-router-dom";
import { getNcbiSubmission } from "../../../../apis/export/ncbi";
import { NcbiSubmission } from "../../../../types/irida";

interface RouteParams {
  projectId: string;
  id: string;
}

function NcbiExportDetails(): JSX.Element {
  const { projectId, id } = useParams<keyof RouteParams>() as RouteParams;

  const [details, setDetails] = React.useState([]);
  const [samples, setSamples] = React.useState([]);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    getNcbiSubmission(parseInt(projectId), parseInt(id)).then(
      (submission: NcbiSubmission) => {
        const { bioSampleFiles, ...details } = submission;
        console.log(JSON.stringify(details, null, 2));
      }
    );
  }, [id, projectId]);

  return <div>foobar</div>;
}

export default NcbiExportDetails;
