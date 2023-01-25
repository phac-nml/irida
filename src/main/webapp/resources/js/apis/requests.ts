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

export async function post<
  T,
  P = FormData | Record<string, unknown> | undefined
>(url: string, params?: P, config?: AxiosRequestConfig): Promise<T> {
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
      let message;
      if (error instanceof Error) {
        message = error.message;
      } else {
        message = String(error);
      }
      return Promise.reject(message);
    } else {
      return Promise.reject("An unexpected error occurred");
    }
  }
}
