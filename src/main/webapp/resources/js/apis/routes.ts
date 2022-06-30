const CONTEXT_PATH = document.documentElement.dataset.context || "/";

type SearchParams =
  | string
  | string[][]
  | Record<string, string>
  | URLSearchParams
  | undefined;

function formatUrl(path: TemplateStringsArray, ...keys: (number | string)[]) {
  return function ({
    urlParams,
    queryParams,
  }: {
    urlParams?: Record<string, string | number>;
    queryParams?: SearchParams;
  }): string {
    let url = CONTEXT_PATH;
    if (urlParams) {
      const temp = path.slice();
      keys.forEach((key, i) => {
        temp[i] = temp[i] + urlParams[key];
      });
      url += temp.join("");
    } else {
      url += path;
    }

    /*
    If there is a query then format it properly as URLSearchParams
    and append it to the end or the URL.
     */
    if (queryParams) {
      const searchParams = new URLSearchParams(queryParams);
      url += `?${searchParams.toString()}`;
    }

    return url;
  };
}

export const activities_project_route = formatUrl`ajax/activities/project`;
export const activities_recent_route = formatUrl`ajax/activities/all`;
export const activities_user_route = formatUrl`ajax/activities/user`;
