/* eslint-disable @typescript-eslint/no-explicit-any */

interface IridaWindow extends Window {
  project?: any;
  translations?: Record<string, string>;
  TL?: {
    BASE_URL?: string;
  };
  PAGE?: any;
  IRIDA?: any;
  GALAXY?: any;
}
