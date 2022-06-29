const CONTEXT_PATH = document.documentElement.dataset.context || "/";

type SearchParams =
  | string
  | string[][]
  | Record<string, string>
  | URLSearchParams
  | undefined;
type QueryOnly = (data: undefined, params: SearchParams) => string;

function formatUrl(path: TemplateStringsArray, ...keys: (number | string)[]) {
  return function (
    data?: { [key: string]: string | number },
    query?: SearchParams
  ) {
    let url = CONTEXT_PATH;
    if (data) {
      const temp = path.slice();
      keys.forEach((key, i) => {
        temp[i] = temp[i] + data[key];
      });
      url += temp.join("");
    } else {
      url += path;
    }

    /*
    If there is a query then format it properly as URLSearchParams
    and append it to the end or the URL.
     */
    if (query) {
      const searchParams = new URLSearchParams(query);
      url += `?${searchParams.toString()}`;
    }

    return url;
  };
}

export const activitiesRoute = formatUrl`/ajax/activities`;

export const activities_project: QueryOnly = formatUrl`/ajax/activities/project`;
export const activities_recent: QueryOnly = formatUrl`/ajax/activities/all`;
export const activities_user: QueryOnly = formatUrl`/ajax/activities/user`;
