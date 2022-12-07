import axios, { AxiosRequestConfig } from "axios";

export async function get<T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> {
  try {
    const { data } = await axios.get(url, config);
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

export async function post<T>(
  url: string,
  params?: Record<string, unknown> | undefined,
  config?: AxiosRequestConfig
): Promise<T> {
  try {
    const { data } = await axios.post(url, params, config);
    return data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.reject(error.response.data.error);
      } else {
        return Promise.reject(error.message);
      }
    } else if (axios.isCancel(error)) {
      return Promise.reject(error.message);
    } else {
      return Promise.reject("An unexpected error occurred");
    }
  }
}
