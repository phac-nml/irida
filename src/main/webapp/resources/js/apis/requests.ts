import axios from "axios";

const http = axios.create({
  headers: {
    "Content-Type": "application/json",
  },
});

export async function get(url: string) {
  try {
    const { data } = await http.get(url);
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
