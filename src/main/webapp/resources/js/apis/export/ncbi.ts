import axios from "axios";
import { NcbiSubmission } from "../../types/irida";

export async function getNcbiSubmission(
  projectId: number,
  uploadId: number
): Promise<NcbiSubmission> {
  try {
    const { data } = await axios.get(
      `/ajax/ncbi/project/${projectId}/details/${uploadId}`
    );
    return data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.reject(error.response.data.error);
      } else {
        return Promise.reject(error.message);
      }
    } else {
      return Promise.reject("An unexpected error occurred");
    }
  }
}
