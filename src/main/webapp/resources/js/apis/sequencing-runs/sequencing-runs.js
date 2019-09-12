import axios from "axios";

const URL = `${window.TL.BASE_URL}ajax/sequencingRuns/list`;

export async function fetchSequencingRuns(params) {
  return axios.post(URL, params).then(({ data }) => data);
}
