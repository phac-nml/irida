import axios from "axios";

export function uploadFiles({ files, url }) {
  const formData = new FormData();
  files.forEach((f, i) => formData.append(`files[${i}]`, f));

  return axios
    .post(url, formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })
    .then(response => console.log(response))
    .catch(error => console.error(error));
}
