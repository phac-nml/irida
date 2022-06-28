const CONTEXT_PATH = document.documentElement.dataset.context;

function formatUrl(url: TemplateStringsArray, ...keys: (number | string)[]) {
  return function (data?: { [key: string]: string | number }) {
    if (data) {
      const temp = url.slice();
      console.log(temp);
      keys.forEach((key, i) => {
        temp[i] = temp[i] + data[key];
      });
      return `${CONTEXT_PATH}${temp.join("")}`;
    } else {
      return `${CONTEXT_PATH}${url}`;
    }
  };
}

export const activitiesRoute = formatUrl`/ajax/activities`;
