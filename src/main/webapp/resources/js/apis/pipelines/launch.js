import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`/ajax/pipelines`);

export const fetchPipelineDetails = async function (id) {
  return axios.get(`${URL}/${id}`).then(({ data }) => data);
};

export const launchPipeline = async function ({ details, id }) {
  return axios
    .post(`${URL}/${id}`, details)
    .then((response) => console.log(response));
};
