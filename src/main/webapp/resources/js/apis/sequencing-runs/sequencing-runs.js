import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

export function deleteSequencingRun({ id }) {
  axios
    .delete(setBaseUrl(`/sequencingRuns/${id}`))
    .then(() => (window.location.href = setBaseUrl(`/admin/sequencing_runs`)));
}
